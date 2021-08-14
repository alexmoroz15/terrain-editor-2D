package terrain_editor;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;
import terrain_editor.noisemap.FractalMap2D;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ControlPane extends VBox {
    // Properties of the noise map
    TextField seedField;
    TextField freqField;
    TextField lacField;
    TextField gainField;
    TextField numLayersField;

    Label seedLabel;
    Label freqLabel;
    Label lacLabel;
    Label gainLabel;
    Label numLayersLabel;

    // Properties of the final image
    TextField rowsField;
    TextField colsField;

    Label rowsLabel;
    Label colsLabel;

    TextField xOffsetField;
    TextField yOffsetField;

    Label xOffsetLabel;
    Label yOffsetLabel;

    // Tiled image
    Button tileChooseButton;

    ImageView tileImageView;

    // Define a range of amplitudes in [0.0, 1.0] where the tile image will be painted
    TextField minAmpField;
    TextField maxAmpField;

    // Determine whether the lower and upper bounds are strictly less than or
    // greater than acceptable values
    CheckBox minStrictBox;
    CheckBox maxStrictBox;

    public ControlPane(FractalMap2D noiseMap) {
        seedField = new TextField();
        seedField.setTextFormatter(createLongTextFormatter(noiseMap.getSeed()));
        seedField.setOnAction(actionEvent -> noiseMap.setSeed((long) seedField.getTextFormatter().getValue()));
        seedField.setPrefColumnCount(20);
        seedLabel = new Label("Seed: ");
        var seedHolder = new HBox(seedLabel, seedField);

        freqField = new TextField();
        freqField.setTextFormatter(createDoubleTextFormatter(noiseMap.getFrequency()));
        freqField.setOnAction(actionEvent -> noiseMap.setFrequency((double) freqField.getTextFormatter().getValue()));
        freqLabel = new Label("Frequency: ");
        var freqHolder = new HBox(freqLabel, freqField);

        lacField = new TextField();
        lacField.setTextFormatter(createDoubleTextFormatter(noiseMap.getLacunarity()));
        lacField.setOnAction(actionEvent -> noiseMap.setLacunarity((double) lacField.getTextFormatter().getValue()));
        lacLabel = new Label("Lacunarity: ");
        var lacHolder = new HBox(lacLabel, lacField);

        gainField = new TextField();
        gainField.setTextFormatter(createDoubleTextFormatter(noiseMap.getGain()));
        gainField.setOnAction(actionEvent -> noiseMap.setGain((double) gainField.getTextFormatter().getValue()));
        gainLabel = new Label("Gain: ");
        var gainHolder = new HBox(gainLabel, gainField);

        var mapPropsHolder = new VBox(seedHolder, freqHolder, lacHolder, gainHolder);

        rowsField = new TextField();
        rowsField.setTextFormatter(createIntTextFormatter(20));
        rowsLabel = new Label("Number of Rows: ");
        var rowsHolder = new HBox(rowsLabel, rowsField);

        colsField = new TextField();
        colsField.setTextFormatter(createIntTextFormatter(20));
        colsLabel = new Label("Number of Columns: ");
        var colsHolder = new HBox(colsLabel, colsField);

        var gridPropsHolder = new VBox(rowsHolder, colsHolder);

        tileChooseButton = new Button("Choose Image");
        tileChooseButton.setOnAction(actionEvent -> {
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Pick Tile Image");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png")
            );
            var selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                try {
                    //tileImage = new Image(new FileInputStream(selectedFile));
                    tileImageView.setImage(new Image(new FileInputStream(selectedFile)));
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }
            }
        });

        var tileImage = new Image("/images/swamp_ground.png");
        tileImageView = new ImageView(tileImage);

        var tilePropsHolder = new HBox(tileChooseButton, tileImageView);

        minAmpField = new TextField();
        minAmpField.setTextFormatter(createDoubleTextFormatter(0.0));
        var minAmpLabel = new Label("Lower Bound: ");
        minStrictBox = new CheckBox("Strict lower bound");
        minStrictBox.setSelected(false);

        var minAmpHolder = new HBox(minAmpLabel, minAmpField, minStrictBox);

        maxAmpField = new TextField();
        maxAmpField.setTextFormatter(createDoubleTextFormatter(1.0));
        var maxAmpLabel = new Label("Upper Bound: ");
        maxStrictBox = new CheckBox("Strict upper bound");
        maxStrictBox.setSelected(false);

        var maxAmpHolder = new HBox(maxAmpLabel, maxAmpField, maxStrictBox);

        var boundsHolder = new VBox(minAmpHolder, maxAmpHolder);

        getChildren().addAll(
                new Label("Layer 1"),
                mapPropsHolder,
                gridPropsHolder,
                tilePropsHolder,
                boundsHolder
        );
        setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                new CornerRadii(4.0),
                new BorderWidths(2.0))));
        setPadding(new Insets(4.0));
    }

    private TextFormatter<Double> createDoubleTextFormatter(double defaultVal) {
        return new TextFormatter<Double>(
                new DoubleStringConverter(),
                defaultVal,
                change -> {
                    if (change.getText().isEmpty()) {
                        return change;
                    }
                    if (change.getText().equals(".")) {
                        return change;
                    }
                    try {
                        Double.parseDouble(change.getText());
                        return change;
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                });
    }

    private TextFormatter<Long> createLongTextFormatter(long defaultVal) {
        return new TextFormatter<>(
                new LongStringConverter(),
                defaultVal,
                change -> {
                    if (change.getText().isEmpty()) {
                        return change;
                    }
                    try {
                        Long.parseLong(change.getText());
                        return change;
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                });
    }

    private TextFormatter<Integer> createIntTextFormatter(int defaultVal) {
        return new TextFormatter<>(
                new IntegerStringConverter(),
                defaultVal,
                change -> {
                    if (change.isDeleted()) {
                        return change;
                    }
                    try {
                        Long.parseLong(change.getText());
                        return change;
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                });
    }
}
