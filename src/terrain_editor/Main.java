package terrain_editor;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import terrain_editor.layer.Layer;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    private ArrayList<Layer> layers;
    StackPane previewWindow;
    VBox optionsWindow;

    final double canvasWidth = 320.0;
    final double canvasHeight = 320.0;

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
        previewWindow = new StackPane();
        previewWindow.setPrefSize(320.0, 320.0);
        leftRegion.setCenter(previewWindow);

        // Ensure 1:1 aspect ratio for the preview window.
        var previewParent = (Region) previewWindow.getParent();
        previewParent.widthProperty().addListener((observableValue, oldWidth, newWidth) -> {
            previewWindow.setMaxSize(Math.min(newWidth.doubleValue(), previewParent.getHeight()),
                    Math.min(newWidth.doubleValue(), previewParent.getHeight()));
        });
        previewParent.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
            previewWindow.setMaxSize(Math.min(previewParent.getWidth(), newHeight.doubleValue()),
                    Math.min(previewParent.getWidth(), newHeight.doubleValue()));
        });

        return leftRegion;
    }

    private Parent CreateRightRegion() {
        var rightRegion = new ScrollPane();
        //var options = new VBox();
        optionsWindow = new VBox();
        rightRegion.setContent(optionsWindow);

        // Populate the options pane
        Button saveButton = new Button("Save png");
        saveButton.setOnAction(actionEvent -> {
            System.out.println("Saving...");

            // Save the preview layer as a png. For testing.
            var canvasComposite = new Canvas(canvasWidth, canvasHeight);
            var gc = canvasComposite.getGraphicsContext2D();
            for (var layer : layers) {
                var imageRegion = layer.getPreviewRegion();
                if (imageRegion.isVisible()) {
                    var image = layer.getPreviewImage();
                    gc.drawImage(image, 0.0, 0.0, 320.0, 320.0);
                }
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

        var addLayerButton = new Button("Add Layer");
        addLayerButton.setOnAction(actionEvent -> addLayer());

        optionsWindow.getChildren().addAll(saveButton, addLayerButton);

        return rightRegion;
    }

    private Parent CreateContent() {
        layers = new ArrayList<>();

        var leftRegion = CreateLeftRegion();
        var rightRegion = CreateRightRegion();

        var root = new BorderPane();
        root.setCenter(leftRegion);
        root.setRight(rightRegion);

        addLayer();

        return root;
    }

    static int layerCounter = 0;

    private void addLayer() {
        var fmp = new FractalMapParams();
        var ppp = new PreviewPaneParams();
        var layer = new Layer(layerCounter, fmp, ppp, this::removeLayer, this::setVisible);
        layerCounter++;

        layers.add(layer);
        previewWindow.getChildren().add(layer.getPreviewRegion());
        optionsWindow.getChildren().add(layer.getControlPane());
    }

    public interface RemoveLayerCallback {
        void removeLayer(Layer layer);
    }

    private void removeLayer(Layer layer) {
        var previewDeleted = previewWindow.getChildren().remove(layer.getPreviewRegion());
        var controlDeleted = optionsWindow.getChildren().remove(layer.getControlPane());
        var layerDeleted = layers.remove(layer);

        assert previewDeleted;
        assert controlDeleted;
        assert layerDeleted;
    }

    public interface SetVisibleCallback {
        void setVisible(Layer layer, boolean visible);
    }

    private void setVisible(Layer layer, boolean visible) {
        layer.getPreviewRegion().setVisible(visible);
    }
}
