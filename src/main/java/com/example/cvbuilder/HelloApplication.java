package com.example.cvbuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            Database.init();
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 750);
        stage.setTitle("CV Builder");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();
    }
}
