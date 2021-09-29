package terrain_editor_exp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(CreateContent()));
        stage.setTitle("Terrain Generator App");
        stage.show();
    }

    private Parent CreateContent() {
        var borderPane = new BorderPane();
        borderPane.setTop(CreateMenuBar());
        borderPane.setBottom(CreateStatusBar());
        /*
        borderPane.setCenter(CreatePreviewPane());
        borderPane.setRight(CreateOptionsPane());

         */
        borderPane.setCenter(new SplitPane(CreatePreviewPane(), CreateOptionsPane()));
        return borderPane;
    }

    private Node CreateMenuBar() {
        final var menu1 = new Menu("File");
        final var menu2 = new Menu("Options");
        final var menu3 = new Menu("Help");
        var menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu1, menu2, menu3);
        return menuBar;
    }

    private Node CreateStatusBar() {
        final var statusLabel = new Label("Status");
        var hBox = new HBox(statusLabel);
        return hBox;
    }

    private Node CreatePreviewPane() {
        var backgroundImage = new Image("/images/swamp_ground.png");
        var backgroundRegion = new Region();
        backgroundRegion.setBackground(new Background(new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(0, 0, false, false, true, false)
        )));
        return backgroundRegion;
    }

    private Node CreateOptionsPane() {
        var scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        var layersHolder = new VBox();

        var upButton = new Button("up");
        var downButton = new Button("down");
        var layerHeightControls = new VBox(upButton, downButton);

        var visibilityButton = new Button("toggle visible");

        var layerPreview = new ImageView("/images/swamp_ground.png");

        var layerName = new Label("Layer 1");

        var duplicateLayer = new Button("duplicate");
        var deleteLayer = new Button("delete");
        var layerControls = new HBox(layerHeightControls,
                visibilityButton,
                layerPreview,
                layerName,
                duplicateLayer,
                deleteLayer);

        layerControls.setAlignment(Pos.CENTER);
        var layerOptions = CreateLayerOptions();

        var patterns = new TitledPane("Patterns", new Label("Hello World!"));
        var layerExample = new VBox(layerControls, layerOptions, patterns);
        layerExample.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                null,
                null
        )));

        layersHolder.getChildren().addAll(layerExample, new Button("Add Layer"));
        layersHolder.setAlignment(Pos.TOP_CENTER);
        scrollPane.setContent(layersHolder);
        return scrollPane;
    }

    private Node CreateLayerOptions() {
        // TODO: Create Label-Input combo structs
        var numRowsLabel = new Label("Rows");
        var numRowsField = new TextField();
        var numRowsHolder = new HBox(numRowsLabel, numRowsField);
        var numColsLabel = new Label("Columns");
        var numColsField = new TextField();
        var numColsHolder = new HBox(numColsLabel, numColsField);
        var gridPropsHolder = new VBox(numRowsHolder, numColsHolder);

        var posXLabel = new Label("PosX");
        var posYLabel = new Label("PosY");
        var scaleXLabel = new Label("ScaleX");
        var scaleYLabel = new Label("ScaleY");


        var layerOptions = new VBox(gridPropsHolder,
                posXLabel,
                posYLabel,
                scaleXLabel,
                scaleYLabel);
        return new TitledPane("Layer Options", layerOptions);
    }
}
