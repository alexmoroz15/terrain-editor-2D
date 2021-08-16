package terrain_editor;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
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
import java.lang.reflect.Array;
import java.util.*;

public class Main extends Application {
    private ArrayList<Layer> layers;
    StackPane previewWindow;
    VBox layersWindow;

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
        var settings = new VBox();
        layersWindow = new VBox();
        rightRegion.setContent(settings);

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

        settings.getChildren().addAll(saveButton, addLayerButton, layersWindow);

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

        layers.add(0, layer);

        /*
        var previewChildren = previewWindow.getChildren();
        var previewChildrenList = new LinkedList<>(previewChildren);
        previewChildrenList.addFirst(layer.getPreviewRegion());
        previewChildren.setAll(previewChildrenList);


         */
        previewWindow.getChildren().add(layer.getPreviewRegion());

        var optionsChildren = layersWindow.getChildren();
        var optionsChildrenList = new LinkedList<>(optionsChildren);
        optionsChildrenList.addFirst(layer.getControlPane());
        optionsChildren.setAll(optionsChildrenList);
    }

    public interface RemoveLayerCallback {
        void removeLayer(Layer layer);
    }

    private void removeLayer(Layer layer) {
        var previewDeleted = previewWindow.getChildren().remove(layer.getPreviewRegion());
        var controlDeleted = layersWindow.getChildren().remove(layer.getControlPane());
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

    public enum MoveLayerDirection {
        UP,
        DOWN
    }

    public interface MoveLayerCallback {
        void move(Layer layer, MoveLayerDirection direction);
    }

    private boolean canMoveLayer(Layer layer, MoveLayerDirection direction) {
        var regionList = previewWindow.getChildren();
        var regionArray = regionList.toArray();
        switch (direction) {
            case UP:
                for (int i = 0; i < regionArray.length - 1; i++) {
                    if (regionArray[i] == layer.getPreviewRegion()) {
                        return true;
                    }
                }
                break;
            case DOWN:
                for (int i = 1; i < regionArray.length; i++) {
                    if (regionArray[i] == layer.getPreviewRegion()) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    // Return true if the layer can still move up/down
    private boolean moveLayer(Layer layer, MoveLayerDirection direction) {
        // I don't know how to do stream-fu, so I'll cheat by using arrays.
        var regionList = previewWindow.getChildren();
        var regionArray = regionList.toArray();
        //regionList.clear();

        int lbound = 0;
        int hbound = regionArray.length;
        int swapOffset = 0;

        switch(direction) {
            case UP:
                hbound--;
                swapOffset = 1;
                break;
            case DOWN:
                lbound++;
                swapOffset = -1;
                break;
        }

        for (int i = lbound; i < hbound; i++) {
            if (regionArray[i] == layer.getPreviewRegion()) {
                var temp = regionArray[i];
                regionArray[i] = regionArray[i + swapOffset];
                regionArray[i + swapOffset] = temp;

                regionList.setAll((Node[]) regionArray);
                return canMoveLayer(regionArray, i + swapOffset, direction);
            }
        }

        assert false;
        return false;
    }

    private boolean canMoveLayer(Object[] array, int index, MoveLayerDirection direction) {
        switch(direction) {
            case UP:
                return index > 1;
            case DOWN:
                return index < array.length - 1;
        }
        return false;
    }
}
