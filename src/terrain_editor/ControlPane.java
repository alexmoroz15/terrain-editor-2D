package terrain_editor;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.function.UnaryOperator;

public class ControlPane extends VBox {
    // Properties of the noise map
    TextField seedField;
    TextField freqField;
    TextField lacField;
    TextField gainField;

    Label seedLabel;
    Label freqLabel;
    Label lacLabel;
    Label gainLabel;

    // Properties of the final image
    TextField rowsField;
    TextField colsField;

    Label rowsLabel;
    Label colsLabel;

    // Tiled image
    Button tileChooseButton;

    Image tileImage;

    // Define a range of amplitudes in [0.0, 1.0] where the tile image will be painted
    TextField minAmpField;
    TextField maxAmpField;

    // Determine whether the lower and upper bounds are strictly less than or
    // greater than acceptable values
    CheckBox minStrictBox;
    CheckBox maxStrictBox;

    public ControlPane() {
        seedField = new TextField();
        seedField.setTextFormatter(createLongTextFormatter(25));
        seedLabel = new Label("Seed: ");
        var seedHolder = new HBox(seedLabel, seedField);

        freqField = new TextField();
        freqField.setTextFormatter(createDoubleTextFormatter(1.0));
        freqLabel = new Label("Frequency: ");
        var freqHolder = new HBox(freqLabel, freqField);

        lacField = new TextField();
        lacField.setTextFormatter(createDoubleTextFormatter(0.5));
        lacLabel = new Label("Lacunarity: ");
        var lacHolder = new HBox(lacLabel, lacField);

        gainField = new TextField();
        gainField.setTextFormatter(createDoubleTextFormatter(0.5));
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
                    tileImage = new Image(new FileInputStream(selectedFile));
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }
            }
        });
        tileImage = new Image("/images/swamp_ground.png");

        var tilePropsHolder = new HBox(tileChooseButton, new ImageView(tileImage));

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
    }

    private TextFormatter<Double> createDoubleTextFormatter(double defaultVal) {
        return new TextFormatter<Double>(
                new DoubleStringConverter(),
                defaultVal,
                (UnaryOperator<TextFormatter.Change>) change -> {
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
