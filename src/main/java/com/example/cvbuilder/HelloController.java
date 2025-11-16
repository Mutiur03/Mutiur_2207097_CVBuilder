package com.example.cvbuilder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class HelloController {
    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextArea addressField;

    @FXML
    private TextArea skillsField;

    @FXML
    private ImageView imageSelect;

    @FXML
    private VBox eduBox;

    @FXML
    private VBox expBox;

    @FXML
    private VBox projBox;

    @FXML
    private void imageChange() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageSelect.setImage(image);
        }
    }

    @FXML
    private void addEducation() {
        eduBox.getChildren().add(createRemovableTextArea(eduBox, "Enter education details (e.g., Degree, Institution, Year)"));
    }

    @FXML
    private void addExperience() {
        expBox.getChildren().add(createRemovableTextArea(expBox, "Enter experience details (e.g., Position, Company, Duration)"));
    }

    @FXML
    private void addProject() {
        projBox.getChildren().add(createRemovableTextArea(projBox, "Enter project details (e.g., Project Name, Description, Technologies)"));
    }

    private HBox createRemovableTextArea(VBox parent, String promptText) {
        TextArea ta = new TextArea();
        ta.setPrefRowCount(3);
        ta.setWrapText(true);
        ta.setPromptText(promptText);
        ta.setStyle("-fx-border-color: #ddd; -fx-border-width: 1;");
        HBox.setHgrow(ta, javafx.scene.layout.Priority.ALWAYS);

        Button removeBtn = new Button("Remove");
        removeBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        removeBtn.setOnAction(e -> parent.getChildren().remove(((Node) e.getSource()).getParent()));

        HBox container = new HBox(10, ta, removeBtn);
        container.setPadding(new Insets(5));
        container.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        return container;
    }

    @FXML
    private void saveInformation(ActionEvent event) {
        try {
            CVData cvData = new CVData();
            cvData.setFullName(fullNameField.getText());
            cvData.setEmail(emailField.getText());
            cvData.setPhone(phoneField.getText());
            cvData.setAddress(addressField.getText());
            cvData.setSkills(skillsField.getText());

            if (imageSelect.getImage() != null) {
                cvData.setProfileImage(imageSelect.getImage());
            }

            for (Node node : eduBox.getChildren()) {
                if (node instanceof HBox) {
                    TextArea ta = (TextArea) ((HBox) node).getChildren().getFirst();
                    if (!ta.getText().trim().isEmpty()) {
                        cvData.addEducation(ta.getText());
                    }
                }
            }

            for (Node node : expBox.getChildren()) {
                if (node instanceof HBox) {
                    TextArea ta = (TextArea) ((HBox) node).getChildren().getFirst();
                    if (!ta.getText().trim().isEmpty()) {
                        cvData.addExperience(ta.getText());
                    }
                }
            }
            for (Node node : projBox.getChildren()) {
                if (node instanceof HBox) {
                    TextArea ta = (TextArea) ((HBox) node).getChildren().getFirst();
                    if (!ta.getText().trim().isEmpty()) {
                        cvData.addProject(ta.getText());
                    }
                }
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CV.fxml"));
            Parent root = loader.load();
            showCV cvController = loader.getController();
            cvController.displayCV(cvData);
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            SceneUtils.switchScene(stage, root, "CV Preview");

        } catch (Exception e) {
            System.err.println("Error while saving information and displaying CV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
