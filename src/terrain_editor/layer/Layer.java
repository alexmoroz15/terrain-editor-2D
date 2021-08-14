package terrain_editor.layer;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import terrain_editor.ControlPane;
import terrain_editor.FractalMapParams;
import terrain_editor.PreviewPaneParams;
import terrain_editor.noisemap.FractalMap2D;
import terrain_editor.noisemap.NoiseMap2D;

/*
Represents a layer in the final, composite image.

Each layer keeps track of:
- its noise map
- the control box for the noise map
  - seed input
  - frequency input
  - lacunarity
  - gain
  - tiled sprite/image
  - amplitude cutoffs for where to display image
  - number of rows and columns
- Image object which contains the noise map preview
 */
public class Layer {
    ControlPane controlPane; // Controls noiseMap
    FractalMap2D noiseMap;

    WritableImage previewImage;
    ImageView previewImageView; // Should be updated when noiseMap changes

    final int canvasWidth = 64 * 20;
    final int canvasHeight = 64 * 20;

    public Layer(int layerNum, FractalMapParams fractalMapParams, PreviewPaneParams previewPaneParams) {
        previewImageView = new ImageView();
        previewImage = new WritableImage(canvasWidth, canvasHeight);
        noiseMap = new FractalMap2D(
                fractalMapParams.seed,
                fractalMapParams.frequency,
                fractalMapParams.lacunarity,
                fractalMapParams.gain,
                fractalMapParams.numLayers
        );

        controlPane = new ControlPane(layerNum, this.noiseMap, previewPaneParams, this::redrawPreview);
        noiseMap.addChangeListener(this::redrawPreview);
        redrawPreview(noiseMap);
    }

    private void redrawPreview() {
        redrawPreview(noiseMap);
    }

    private void redrawPreview(NoiseMap2D newValue) {
        // Get all values from noise map and the max value
        var numRows = controlPane.getNumRows();
        var numCols = controlPane.getNumColumns();
        var vals = new double[numRows][numCols];
        var maxVal = -Double.MAX_VALUE;

        var xOffset = controlPane.getXOffset();
        var yOffset = controlPane.getYOffset();

        for (int row = 0; row < vals.length; row++) {
            for (int col = 0; col < vals[row].length; col++) {
                vals[row][col] = newValue.get(col + xOffset, row + yOffset);
                if (maxVal < vals[row][col]) {
                    maxVal = vals[row][col];
                }
            }
        }

        // Draw to the canvas
        var canvas = new Canvas(canvasWidth, canvasHeight);
        var gc = canvas.getGraphicsContext2D();
        for (int row = 0; row < vals.length; row++) {
            for (int col = 0; col < vals[row].length; col++) {
                // Normalize val
                var val = vals[row][col] / maxVal;

                var minAmp = controlPane.getMinAmplitude();
                var maxAmp = controlPane.getMaxAmplitude();
                var minStrict = controlPane.getMinStrict();
                var maxStrict = controlPane.getMaxStrict();

                if ((val > minAmp || (val == minAmp && !minStrict)) &&
                        (val < maxAmp || (val == maxAmp && !maxStrict))) {

                    gc.drawImage(controlPane.getTileImage(),
                            col * canvasWidth / (double) numCols,
                            row * canvasHeight / (double) numRows,
                            canvasWidth / (double) numCols,
                            canvasHeight / (double) numRows);
                }
            }
        }

        var sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        canvas.snapshot(sp, previewImage);
        previewImageView.setImage(previewImage);
    }

    public Image getPreviewImage() {
        return previewImage;
    }

    public ControlPane getControlPane() {
        return controlPane;
    }

    public interface ChangeCallback {
        void callback();
    }
}
