package com.example.textfinder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Textfinder.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1120, 720);
        stage.setTitle("Text Finder · Document Search");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
