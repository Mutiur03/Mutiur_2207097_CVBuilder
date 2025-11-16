package com.example.cvbuilder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class showCV {
    @FXML
    private ImageView profileImageView;

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private VBox educationVBox;

    @FXML
    private VBox experienceVBox;

    @FXML
    private VBox projectVBox;

    @FXML
    private Label skillsLabel;

    public void displayCV(CVData cvData) {
        nameLabel.setText(cvData.getFullName());
        emailLabel.setText(cvData.getEmail());
        phoneLabel.setText(cvData.getPhone());
        addressLabel.setText(cvData.getAddress());
        skillsLabel.setText(cvData.getSkills());

        if (cvData.getProfileImage() != null) {
            profileImageView.setImage(cvData.getProfileImage());
        }

        educationVBox.getChildren().clear();
        for (String edu : cvData.getEducationList()) {
            Text text = new Text(edu);
            text.setWrappingWidth(650);
            text.setStyle("-fx-font-size: 13px; -fx-fill: #333;");
            educationVBox.getChildren().add(text);
        }

        experienceVBox.getChildren().clear();
        for (String exp : cvData.getExperienceList()) {
            Text text = new Text(exp);
            text.setWrappingWidth(650);
            text.setStyle("-fx-font-size: 13px; -fx-fill: #333;");
            experienceVBox.getChildren().add(text);
        }

        projectVBox.getChildren().clear();
        for (String proj : cvData.getProjectList()) {
            Text text = new Text(proj);
            text.setWrappingWidth(650);
            text.setStyle("-fx-font-size: 13px; -fx-fill: #333;");
            projectVBox.getChildren().add(text);
        }
    }
    @FXML
    public void goBack(ActionEvent event) throws IOException {
        System.out.println("Going back");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("update-info.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        SceneUtils.switchScene(stage, root, "CV Builder");
    }

}
