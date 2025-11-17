package com.example.cvbuilder;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class SceneUtils {

    public static void switchScene(Stage stage, Parent root, String title) {
        if (stage == null || root == null) return;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);
        if (title != null && !title.isEmpty()) {
            stage.setTitle(title);
        }
        stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
    }
}
