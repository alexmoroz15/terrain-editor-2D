package terrain_editor;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.LongStringConverter;
import terrain_editor.noisemap.FractalMap2D;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    private ArrayList<Canvas> layers;
    private ArrayList<WritableImage> layerImages;
    private ArrayList<FractalMap2D> noiseMaps;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(CreateContent()));
        stage.setTitle("Terrain Generator App");
        stage.show();
    }

    private Parent CreateLeftRegion() {
        var leftRegion = new BorderPane();

        // Create the preview region which displays all layers
        var preview = new StackPane();
        preview.setPrefSize(320.0, 320.0);
        leftRegion.setCenter(preview);

        // Populate the preview window with layer snapshots
        for (var im : layerImages) {
            var layer = new Region();
            layer.setBackground(new Background(new BackgroundImage(
                    im,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            BackgroundSize.AUTO,
                            BackgroundSize.AUTO,
                            false,
                            false,
                            true, // Make the image fit to the size of the region
                            false
                    )
            )));
            layer.setPrefSize(240.0, 240.0);
            preview.getChildren().add(layer);
        }

        // Ensure 1:1 aspect ratio for the preview window.
        var previewParent = (Region) preview.getParent();
        previewParent.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            preview.setMaxSize(Math.min(newWidth.doubleValue(), previewParent.getHeight()),
                    Math.min(newWidth.doubleValue(), previewParent.getHeight()));
        });
        previewParent.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            preview.setMaxSize(Math.min(previewParent.getWidth(), newHeight.doubleValue()),
                    Math.min(previewParent.getWidth(), newHeight.doubleValue()));
        });

        return leftRegion;
    }

    private Parent CreateRightRegion() {
        var rightRegion = new ScrollPane();
        var options = new VBox();
        rightRegion.setContent(options);

        // Populate the options pane
        Button saveButton = new Button("Save png");
        saveButton.setOnAction(actionEvent -> {
            System.out.println("Saving...");

            // Save the preview layer as a png. For testing.
            var canvasComposite = new Canvas(320.0, 320.0);
            var gc = canvasComposite.getGraphicsContext2D();
            for (var im : layerImages) {
                gc.drawImage(im, 0.0, 0.0, 320.0, 320.0);
            }
            var wi = canvasComposite.snapshot(null, null);
            var file = new File("snapshot.png");
            var bi = SwingFXUtils.fromFXImage(wi, null);
            try {
                ImageIO.write(bi, "png", file);
                System.out.println("Image saved successfully.");
            } catch (IOException e) {
                System.out.println(e);
            }
        });

        var seedChanger = CreateSeedChanger(0);
        var frequencyChanger = CreateFrequencyChanger(0);

        options.getChildren().addAll(
                saveButton,
                seedChanger,
                frequencyChanger,
                new ControlPane(new FractalMap2D(25, 1.0))
        );

        return rightRegion;
    }

    private TextFormatter<Double> createDoubleTF(double defaultVal) {
        return new TextFormatter<>(
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

    private TextFormatter<Long> createLongTF(long defaultValue) {
        return new TextFormatter<>(
                new LongStringConverter(),
                defaultValue,
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

    private Parent CreateSeedChanger(int layerNum) {
        var seedChanger = new HBox();
        seedChanger.setBorder(new Border(new BorderStroke(
                Color.GREEN,
                BorderStrokeStyle.SOLID,
                null,
                new BorderWidths(4)
        )));

        var numBox = new TextField();
        numBox.setTextFormatter(createLongTF(noiseMaps.get(layerNum).getSeed()));

        numBox.setOnAction(actionEvent -> {
            noiseMaps.get(layerNum).setSeed((long) numBox.getTextFormatter().getValue());
        });

        var label = new Label("Seed: ");

        seedChanger.getChildren().addAll(label, numBox);
        return seedChanger;
    }

    private Parent CreateFrequencyChanger(int layerNum) {
        var frequencyChanger = new HBox();
        frequencyChanger.setBorder(new Border(new BorderStroke(
                Color.RED,
                BorderStrokeStyle.SOLID,
                null,
                new BorderWidths(4)
        )));

        var numBox = new TextField();
        numBox.setTextFormatter(createDoubleTF(noiseMaps.get(layerNum).getFrequency()));

        numBox.setOnAction(actionEvent -> {
            noiseMaps.get(layerNum).setFrequency((double) numBox.getTextFormatter().getValue());
        });

        var label = new Label("Frequency: ");

        frequencyChanger.getChildren().addAll(label, numBox);
        return frequencyChanger;
    }

    private Parent CreateContent() {
        layers = new ArrayList<>();
        layerImages = new ArrayList<>();
        noiseMaps = new ArrayList<>();

        // Retrieve values from Fractal map
        var fm2d = new FractalMap2D(25, 0.005, 0.5, 0.5, 5);
        var vals = new double[20][20];
        double maxVal = -Double.MAX_VALUE;
        for (int row = 0; row < vals.length; row++) {
            for (int col = 0; col < vals[row].length; col++) {
                vals[row][col] = fm2d.get(col, row);
                if (maxVal < vals[row][col]) {
                    maxVal = vals[row][col];
                }
            }
        }
        noiseMaps.add(fm2d);

        var canvas = new Canvas(320.0, 320.0);
        var gc = canvas.getGraphicsContext2D();
        for (int row = 0; row < vals.length; row++) {
            for (int col = 0; col < vals[row].length; col++) {
                // Normalize vals as we draw.
                vals[row][col] /= maxVal;
                gc.setFill(new Color(vals[row][col], 0.0, 0.0, 1.0));
                gc.fillRect(col * 16.0, row * 16.0, 16.0, 16.0);
            }
        }

        var sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        var wi = canvas.snapshot(sp, null);

        layers.add(canvas);
        layerImages.add(wi);

        noiseMaps.get(0).addChangeListener(newValue -> {
            var vals1 = new double[20][20];
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
        });

        var leftRegion = CreateLeftRegion();
        var rightRegion = CreateRightRegion();

        var root = new BorderPane();
        root.setCenter(leftRegion);
        root.setRight(rightRegion);

        return root;
    }
}
