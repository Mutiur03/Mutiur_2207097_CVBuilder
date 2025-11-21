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
import java.util.ResourceBundle;

import static com.example.cvbuilder.SceneUtils.switchScene;

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

        VBox box = new VBox(6, iv, name);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(140);
        box.setStyle("-fx-background-color: white; -fx-padding: 8; -fx-border-radius: 6; -fx-background-radius: 6; -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.08), 6, 0, 0, 2);");

        box.setOnMouseClicked(evt -> {
            try {
                CVDao dao = new CVDao();
                var opt = dao.getCV(s.getId());
                if (opt.isPresent()) {
                    CVData cv = opt.get();
                    URL res = getClass().getResource("CV.fxml");
                    if (res == null) {
                        System.err.println("Resource CV.fxml not found on classpath.");
                        return;
                    }
                    FXMLLoader loader = new FXMLLoader(res);
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
            URL res = getClass().getResource("update-info.fxml");
            if (res == null) {
                System.err.println("Resource update-info.fxml not found on classpath.");
                return;
            }
            Parent root = FXMLLoader.load(res);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            switchScene(stage,root,"UpdateInfo.fxml");
        } catch (IOException e) {
            System.err.println("Failed to open update form: " + e.getMessage());
        }
    }

}
