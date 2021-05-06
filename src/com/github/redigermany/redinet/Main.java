package com.github.redigermany.redinet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;
    private Scene scene;
    private Pane root;
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        root = new Pane();
        scene = new Scene(root);
        stage.setTitle("ReDiNet");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
