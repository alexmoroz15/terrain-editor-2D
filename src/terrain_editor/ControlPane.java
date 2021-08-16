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

    public ControlPane(int layerNum,
                       FractalMap2D noiseMap,
                       PreviewPaneParams previewPaneParams,
                       Layer.ChangeCallback changeCallback,
                       Callback removeLayerCallback,
                       TypedCallback<Boolean> setVisibleCallback,
                       Layer.MoveThisLayerCallback moveLayerCallback) {

        var mapPropertyPane = new MapPropertyPane(noiseMap);
        previewPropertyPane = new PreviewPropertyPane(previewPaneParams, changeCallback);

        var deleteButton = new Button("Delete Layer");
        deleteButton.setOnAction(actionEvent -> removeLayerCallback.callback());

        var visibleToggle = new CheckBox("Visible");
        visibleToggle.setSelected(true);
        visibleToggle.setOnAction(actionEvent -> setVisibleCallback.callback(visibleToggle.isSelected()));

        var moveLayerUpButton = new Button("Move layer up");
        moveLayerUpButton.setOnAction(actionEvent -> moveLayerCallback.move(Main.MoveLayerDirection.UP));
        var moveLayerDownButton = new Button("Move layer down");
        moveLayerDownButton.setOnAction(actionEvent -> moveLayerCallback.move(Main.MoveLayerDirection.DOWN));

        getChildren().addAll(
                new Label("Layer " + layerNum),
                visibleToggle,
                deleteButton,
                moveLayerUpButton,
                moveLayerDownButton,
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
