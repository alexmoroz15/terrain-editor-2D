package terrain_editor;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import terrain_editor.layer.Layer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static terrain_editor.PropertyPaneUtils.createDoubleTextFormatter;
import static terrain_editor.PropertyPaneUtils.createIntTextFormatter;

public class PreviewPropertyPane extends VBox {
    TextField rowsField;
    TextField colsField;
    TextField xOffsetField;
    TextField yOffsetField;

    ImageView tileImageView;
    TextField minAmpField;
    TextField maxAmpField;
    CheckBox minStrictBox;
    CheckBox maxStrictBox;

    public PreviewPropertyPane(PreviewPaneParams previewPaneParams, Layer.ChangeCallback changeCallback) {
        rowsField = new TextField();
        rowsField.setTextFormatter(createIntTextFormatter(previewPaneParams.numRows));
        rowsField.setOnAction(actionEvent -> changeCallback.callback());
        var rowsLabel = new Label("Number of Rows: ");
        var rowsHolder = new HBox(rowsLabel, rowsField);

        colsField = new TextField();
        colsField.setTextFormatter(createIntTextFormatter(previewPaneParams.numColumns));
        colsField.setOnAction(actionEvent -> changeCallback.callback());
        var colsLabel = new Label("Number of Columns: ");
        var colsHolder = new HBox(colsLabel, colsField);

        var gridPropsHolder = new VBox(rowsHolder, colsHolder);

        xOffsetField = new TextField();
        xOffsetField.setTextFormatter(createDoubleTextFormatter(previewPaneParams.xOffset));
        xOffsetField.setOnAction(actionEvent -> changeCallback.callback());
        var xOffsetLabel = new Label("X Offset: ");
        var xOffsetHolder = new HBox(xOffsetLabel, xOffsetField);

        yOffsetField = new TextField();
        yOffsetField.setTextFormatter(createDoubleTextFormatter(previewPaneParams.yOffset));
        yOffsetField.setOnAction(actionEvent -> changeCallback.callback());
        var yOffsetLabel = new Label("Y Offset: ");
        var yOffsetHolder = new HBox(yOffsetLabel, yOffsetField);

        var offsetHolder = new VBox(xOffsetHolder, yOffsetHolder);

        tileImageView = new ImageView(previewPaneParams.tileImage);
        var tileChooseButton = new Button("Choose Image");
        tileChooseButton.setOnAction(actionEvent -> {
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Pick Tile Image");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png")
            );
            var selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                try {
                    tileImageView.setImage(new Image(new FileInputStream(selectedFile)));
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }
            }
            changeCallback.callback();
        });

        var tilePropsHolder = new HBox(tileChooseButton, tileImageView);

        minAmpField = new TextField();
        minAmpField.setTextFormatter(createDoubleTextFormatter(previewPaneParams.minAmplitude));
        minAmpField.setOnAction(actionEvent -> changeCallback.callback());
        var minAmpLabel = new Label("Lower Bound: ");
        minStrictBox = new CheckBox("Strict lower bound");
        minStrictBox.setSelected(previewPaneParams.minStrict);
        minStrictBox.setOnAction(actionEvent -> changeCallback.callback());

        var minAmpHolder = new HBox(minAmpLabel, minAmpField, minStrictBox);

        maxAmpField = new TextField();
        maxAmpField.setTextFormatter(createDoubleTextFormatter(previewPaneParams.maxAmplitude));
        maxAmpField.setOnAction(actionEvent -> changeCallback.callback());
        var maxAmpLabel = new Label("Upper Bound: ");
        maxStrictBox = new CheckBox("Strict upper bound");
        maxStrictBox.setSelected(previewPaneParams.maxStrict);
        maxStrictBox.setOnAction(actionEvent -> changeCallback.callback());

        var maxAmpHolder = new HBox(maxAmpLabel, maxAmpField, maxStrictBox);

        var boundsHolder = new VBox(minAmpHolder, maxAmpHolder);

        getChildren().addAll(gridPropsHolder, offsetHolder, tilePropsHolder, boundsHolder);
    }

    public int getNumRows() {
        return (int) rowsField.getTextFormatter().getValue();
    }

    public int getNumColumns() {
        return (int) colsField.getTextFormatter().getValue();
    }

    public Image getTileImage() {
        return tileImageView.getImage();
    }

    public double getXOffset() {
        return (double) xOffsetField.getTextFormatter().getValue();
    }

    public double getYOffset() {
        return (double) yOffsetField.getTextFormatter().getValue();
    }

    public double getMinAmplitude() {
        return (double) minAmpField.getTextFormatter().getValue();
    }

    public double getMaxAmplitude() {
        return (double) maxAmpField.getTextFormatter().getValue();
    }

    public boolean getMinStrict() {
        return minStrictBox.isSelected();
    }

    public boolean getMaxStrict() {
        return maxStrictBox.isSelected();
    }
}
