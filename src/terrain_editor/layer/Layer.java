package terrain_editor.layer;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import terrain_editor.ControlPane;
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
    ImageView previewImage; // Should be updated when noiseMap changes

    int canvasWidth = 256;
    int canvasHeight = 256;

    public Layer(FractalMapParams fractalMapParams, ControlPaneParams controlPaneParams) {
        this.noiseMap = new FractalMap2D(
                fractalMapParams.seed,
                fractalMapParams.frequency,
                fractalMapParams.lacunarity,
                fractalMapParams.gain,
                fractalMapParams.numLayers
        );

        this.controlPane = new ControlPane(this.noiseMap);

        NoiseMap2D.ChangeListener redrawPreview = newValue -> {
            var canvas = new Canvas(canvasWidth, canvasHeight);
            var gc = canvas.getGraphicsContext2D();



            var vals = new double[20][20];
            double maxVal1 = -Double.MAX_VALUE;
            for (int row = 0; row < vals1.length; row++) {
                for (int col = 0; col < vals1[row].length; col++) {
                    vals1[row][col] = newValue.get(col, row);
                    if (maxVal1 < vals1[row][col]) {
                        maxVal1 = vals1[row][col];
                    }
                }
            }

            var cv = layers.get(0);
            var gc1 = cv.getGraphicsContext2D();
            for (int row = 0; row < vals1.length; row++) {
                for (int col = 0; col < vals1[row].length; col++) {
                    // Normalize vals as we draw.
                    vals1[row][col] /= maxVal1;
                    gc1.setFill(new Color(vals1[row][col], 0.0, 0.0, 1.0));
                    gc1.fillRect(col * 16.0, row * 16.0, 16.0, 16.0);
                }
            }

            var sp1 = new SnapshotParameters();
            sp1.setFill(Color.TRANSPARENT);
            cv.snapshot(sp1, layerImages.get(0));
        };
    }

    public static class FractalMapParams {
        public long seed;
        public double frequency;
        public double lacunarity;
        public double gain;
        public int numLayers;

        public FractalMapParams() {
            this.seed = 0;
            this.frequency = 1.0;
            this.lacunarity = 1.0;
            this.gain = 1.0;
            this.numLayers = 1;
        }
    }

    public static class ControlPaneParams {
        public int numRows;
        public int numColumns;
        public double xOffset;
        public double yOffset;
        public Image tileImage;
        public double minAmplitude;
        public double maxAmplitude;
        public boolean minStrict;
        public boolean maxStrict;

        public ControlPaneParams() {
            this.numRows = 20;
            this.numColumns = 20;
            this.xOffset = 0.0;
            this.yOffset = 0.0;
            this.tileImage = new Image("/images/swamp_ground.png");
            this.minAmplitude = 0.0;
            this.maxAmplitude = 1.0;
            this.minStrict = false;
            this.maxStrict = false;
        }
    }
}
