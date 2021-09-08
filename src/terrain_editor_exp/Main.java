package terrain_editor_exp;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
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

        var mainControls = new HBox(new Label("Layer 1"));
        var layerOptions = new TitledPane("Layer Options", new Label("Hello World"));
        var patterns = new TitledPane("Patterns", new Label("Hello World!"));
        var layerExample = new VBox(mainControls, layerOptions, patterns);

        layersHolder.getChildren().addAll(layerExample);
        scrollPane.setContent(layersHolder);
        return scrollPane;
    }
}
