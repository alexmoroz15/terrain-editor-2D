package terrain_editor;

import javafx.application.Application;
import javafx.collections.ObservableList;
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
import java.util.stream.Collectors;

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

        layers.add(layer);

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

    // Return true if the layer can still move up/down after moving
    private boolean moveLayer(Layer layer, MoveLayerDirection direction) {
        // So the layer needs to be moved in 3 places:
        // - previewWindow (ObservableList<Node>)
        // - layersWindow (ObservableList<Node>)
        // - layers (ArrayList<Layer>)

        LinearMoveDirection previewDirection = LinearMoveDirection.FORWARD;
        LinearMoveDirection optionsDirection = LinearMoveDirection.BACKWARD;
        if (direction == MoveLayerDirection.DOWN) {
                previewDirection = LinearMoveDirection.BACKWARD;
                optionsDirection = LinearMoveDirection.FORWARD;
        }

        var result1 = listSwap(previewWindow.getChildren().stream().map(node -> (Object) node).collect(Collectors.toList()),
                layer.getPreviewRegion(),
                previewDirection);
        var result2 = listSwap(layers.stream().map(node -> (Object) node).collect(Collectors.toList()),
                layer,
                previewDirection);
        var result3 = listSwap(layersWindow.getChildren().stream().map(node -> (Object) node).collect(Collectors.toList()),
                layer.getControlPane(),
                optionsDirection);
        assert result1 == result2 && result2 == result3;
        return result1 && result2 && result3;
    }

    private enum LinearMoveDirection {
        FORWARD,
        BACKWARD
    }

    // returns true if the object can be swapped with the next element in the given direction again.
    private boolean listSwap(List<Object> l, Object o, LinearMoveDirection direction) {
        if (l.size() <= 1) {
            return false;
        }
        if ((l.get(0) == o && direction == LinearMoveDirection.BACKWARD) ||
                (l.get(l.size() - 1) == o && direction == LinearMoveDirection.FORWARD)) {
            return false;
        }
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i) == o) {
                int swapOffset = 0;
                switch (direction) {
                    case FORWARD:
                        swapOffset = 1;
                        break;
                    case BACKWARD:
                        swapOffset = -1;
                        break;
                }
                var temp = l.get(i);
                l.set(i, l.get(i + swapOffset));
                l.set(i + swapOffset, temp);
                switch (direction) {
                    case FORWARD:
                        return i + swapOffset < l.size() - 1;
                    case BACKWARD:
                        return i + swapOffset > 1;
                }
                return false;
            }
        }
        return false;
    }
}
