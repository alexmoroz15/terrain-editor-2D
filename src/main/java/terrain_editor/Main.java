package main.java.terrain_editor;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        var root = new Pane();
        stage.setScene(new Scene(root, 640, 480));
        stage.setTitle("Tiled Image App 2");
        stage.show();
    }
}
