package terrain_editor;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import terrain_editor.layer.Layer;
import terrain_editor.noisemap.FractalMap2D;

public class ControlPane extends VBox {
    PreviewPropertyPane previewPropertyPane;

    public ControlPane(int layerNum, FractalMap2D noiseMap, PreviewPaneParams previewPaneParams, Layer.ChangeCallback changeCallback) {

        var mapPropertyPane = new MapPropertyPane(noiseMap);
        previewPropertyPane = new PreviewPropertyPane(previewPaneParams, changeCallback);

        getChildren().addAll(
                new Label("Layer " + layerNum),
                mapPropertyPane,
                previewPropertyPane
        );
        setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                new CornerRadii(4.0),
                new BorderWidths(2.0))));
        setPadding(new Insets(4.0));
    }

    public int getNumRows() {
        return previewPropertyPane.getNumRows();
    }

    public int getNumColumns() {
        return previewPropertyPane.getNumColumns();
    }

    public double getXOffset() {
        return previewPropertyPane.getXOffset();
    }

    public double getYOffset() {
        return previewPropertyPane.getYOffset();
    }

    public Image getTileImage() {
        return previewPropertyPane.getTileImage();
    }

    public double getMinAmplitude() {
        return previewPropertyPane.getMinAmplitude();
    }

    public double getMaxAmplitude() {
        return previewPropertyPane.getMaxAmplitude();
    }

    public boolean getMinStrict() {
        return previewPropertyPane.getMinStrict();
    }

    public boolean getMaxStrict() {
        return previewPropertyPane.getMaxStrict();
    }
}
