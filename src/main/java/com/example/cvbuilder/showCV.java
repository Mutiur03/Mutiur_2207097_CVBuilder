package com.example.cvbuilder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

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
        if (cvData == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No CV data to display.", javafx.scene.control.ButtonType.OK);
            alert.setHeaderText("No data");
            alert.showAndWait();
            return;
        }

        nameLabel.setText(defaultIfNull(cvData.getFullName()));
        emailLabel.setText(defaultIfNull(cvData.getEmail()));
        phoneLabel.setText(defaultIfNull(cvData.getPhone()));
        addressLabel.setText(defaultIfNull(cvData.getAddress()));
        skillsLabel.setText(defaultIfNull(cvData.getSkills()));

        if (cvData.getProfileImage() != null && profileImageView != null) {
            profileImageView.setImage(cvData.getProfileImage());
        }

        if (educationVBox != null) {
            educationVBox.getChildren().clear();
            if (cvData.getEducationList() != null && !cvData.getEducationList().isEmpty()) {
                for (String edu : cvData.getEducationList()) {
                    Text text = new Text(edu == null ? "" : edu);
                    text.setWrappingWidth(650);
                    text.setStyle("-fx-font-size: 13px; -fx-fill: #333;");
                    educationVBox.getChildren().add(text);
                }
            } else {
                Text text = new Text("No education information provided.");
                text.setStyle("-fx-font-size: 13px; -fx-fill: #666;");
                educationVBox.getChildren().add(text);
            }
        }

        if (experienceVBox != null) {
            experienceVBox.getChildren().clear();
            if (cvData.getExperienceList() != null && !cvData.getExperienceList().isEmpty()) {
                for (String exp : cvData.getExperienceList()) {
                    Text text = new Text(exp == null ? "" : exp);
                    text.setWrappingWidth(650);
                    text.setStyle("-fx-font-size: 13px; -fx-fill: #333;");
                    experienceVBox.getChildren().add(text);
                }
            } else {
                Text text = new Text("No experience information provided.");
                text.setStyle("-fx-font-size: 13px; -fx-fill: #666;");
                experienceVBox.getChildren().add(text);
            }
        }

        if (projectVBox != null) {
            projectVBox.getChildren().clear();
            if (cvData.getProjectList() != null && !cvData.getProjectList().isEmpty()) {
                for (CVData.Project proj : cvData.getProjectList()) {
                    VBox projectContainer = new VBox(5);
                    projectContainer.setStyle("-fx-padding: 8; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-color: #fafafa;");

                    if (proj.getTitle() != null && !proj.getTitle().trim().isEmpty()) {
                        Text titleText = new Text(proj.getTitle());
                        titleText.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
                        projectContainer.getChildren().add(titleText);
                    }

                    if (proj.getDescription() != null && !proj.getDescription().trim().isEmpty()) {
                        Text descText = new Text(proj.getDescription());
                        descText.setWrappingWidth(650);
                        descText.setStyle("-fx-font-size: 13px; -fx-fill: #333;");
                        projectContainer.getChildren().add(descText);
                    }

                    if (proj.getLink() != null && !proj.getLink().trim().isEmpty()) {
                        Hyperlink hyperlink = new Hyperlink(proj.getLink());
                        hyperlink.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498db;");

                        hyperlink.setOnAction(event -> {
                            try {
                                Desktop.getDesktop().browse(new URI(proj.getLink()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        projectContainer.getChildren().add(hyperlink);
                    }

                    projectVBox.getChildren().add(projectContainer);
                }
            } else {
                Text text = new Text("No project information provided.");
                text.setStyle("-fx-font-size: 13px; -fx-fill: #666;");
                projectVBox.getChildren().add(text);
            }
        }
    }

    private String defaultIfNull(String s) {
        return (s == null || s.trim().isEmpty()) ? "Not provided" : s;
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("update-info.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        SceneUtils.switchScene(stage, root, "CV Builder");
    }

}
