package com.example.cvbuilder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.example.cvbuilder.SceneUtils.switchScene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;

public class HomeController implements Initializable {

    @FXML
    private TilePane thumbnailsPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            CVDao dao = new CVDao();
            List<CVDao.CVSummary> summaries = dao.listCVSummaries();
            thumbnailsPane.getChildren().clear();
            for (CVDao.CVSummary s : summaries) {
                VBox card = createThumbnailCard(s);
                thumbnailsPane.getChildren().add(card);
            }
        } catch (Exception e) {
            System.err.println("Failed to load saved CVs: " + e.getMessage());
        }
    }

    private VBox createThumbnailCard(CVDao.CVSummary s) {
        Image img = null;
            String p = s.getProfileImagePath();
            if (p != null && !p.isBlank()) {
                File f = new File(p);
                if (f.exists()) {
                    img = new Image(f.toURI().toString());
                } else {
                    var is = getClass().getResourceAsStream((p.startsWith("/") ? p : "/com/example/cvbuilder/" + p));
                    if (is != null) {
                        img = new Image(is);
                    }
                }
            }
        if (img == null) {
            var is = getClass().getResourceAsStream("/com/example/cvbuilder/person.png");
            if (is != null) {
                img = new Image(is);
            }
        }
        ImageView iv = new ImageView();
        if (img != null) iv.setImage(img);
        iv.setFitWidth(120);
        iv.setFitHeight(120);
        iv.setPreserveRatio(true);
        Label name = new Label(s.getFullName() != null ? s.getFullName() : "(Unnamed)");
        name.setWrapText(true);
        name.setMaxWidth(120);
        name.setAlignment(Pos.CENTER);
        StackPane imageContainer = new StackPane(iv);
        imageContainer.setPadding(new Insets(8, 8, 4, 8));
        StackPane.setMargin(iv, new Insets(10, 8, 4, 8));
        MenuButton menu = new MenuButton();
        menu.getStyleClass().add("three-dot-menu");
        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");
        menu.getItems().addAll(editItem, deleteItem);
        StackPane.setAlignment(menu, Pos.TOP_RIGHT);
        StackPane.setMargin(menu, new Insets(6, 6, 0, 0));
        imageContainer.getChildren().add(menu);
        VBox box = new VBox(6);
        box.getChildren().addAll(imageContainer, name);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(140);
        box.getStyleClass().add("thumbnail-card");

        editItem.setOnAction(evt -> {
            evt.consume();
            try {
                CVDao dao = new CVDao();
                var opt = dao.getCV(s.getId());
                if (opt.isPresent()) {
                    CVData cv = opt.get();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("update-info.fxml"));
                    Parent root = loader.load();
                    HelloController controller = loader.getController();
                    controller.loadCVData(cv);
                    Stage stage = (Stage) thumbnailsPane.getScene().getWindow();
                    switchScene(stage, root, "Edit CV");
                } else {
                    Alert a = new Alert(Alert.AlertType.WARNING, "CV not found.", ButtonType.OK);
                    a.setHeaderText(null);
                    a.showAndWait();
                }
            } catch (Exception ex) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Failed to open edit form: " + ex.getMessage(), ButtonType.OK);
                a.setHeaderText("Error");
                a.showAndWait();
            }
        });

        deleteItem.setOnAction(evt -> {
            evt.consume();
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this CV? This cannot be undone.", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.YES) {
                try {
                    CVDao dao = new CVDao();
                    boolean deleted = dao.deleteCV(s.getId());
                    if (deleted) {
                        thumbnailsPane.getChildren().remove(box);
                        Alert info = new Alert(Alert.AlertType.INFORMATION, "CV deleted.", ButtonType.OK);
                        info.setHeaderText(null);
                        info.showAndWait();
                    } else {
                        Alert warn = new Alert(Alert.AlertType.WARNING, "Failed to delete CV.", ButtonType.OK);
                        warn.setHeaderText(null);
                        warn.showAndWait();
                    }
                } catch (Exception ex) {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Failed to delete CV: " + ex.getMessage(), ButtonType.OK);
                    a.setHeaderText("Error");
                    a.showAndWait();
                }
            }
        });
        box.setOnMouseClicked(evt -> {
            evt.consume();
            try {
                CVDao dao = new CVDao();
                var opt = dao.getCV(s.getId());
                if (opt.isPresent()) {
                    CVData cv = opt.get();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("CV.fxml"));
                    Parent root = loader.load();
                    showCV controller = loader.getController();
                    controller.displayCV(cv);
                    Stage stage = (Stage) thumbnailsPane.getScene().getWindow();
                    switchScene(stage, root, "CV Preview");
                }
            } catch (Exception ex) {
                System.err.println("Failed to open CV preview: " + ex.getMessage());
            }
        });

        return box;
    }

    @FXML
    public void gotoForm(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("update-info.fxml")));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            switchScene(stage,root,"UpdateInfo.fxml");
        } catch (IOException e) {
            System.err.println("Failed to open update form: " + e.getMessage());
        }
    }

}
