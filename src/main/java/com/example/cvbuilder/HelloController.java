package com.example.cvbuilder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addEducation();
    }

    @FXML
    private void imageChange() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            if (imageSelect != null) imageSelect.setImage(image);
        }
    }

    @FXML
    private void addEducation() {
        if (eduBox != null) {
            eduBox.getChildren().add(createRemovableTextArea(eduBox, "Enter education details (e.g., Degree, Institution, Year)"));
        }
    }

    @FXML
    private void addExperience() {
        if (expBox != null) {
            expBox.getChildren().add(createRemovableTextArea(expBox, "Enter experience details (e.g., Position, Company, Duration)"));
        }
    }

    @FXML
    private void addProject() {
        if (projBox != null) {
            projBox.getChildren().add(createRemovableTextArea(projBox, "Enter project details (e.g., Project Name, Description, Technologies)"));
        }
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
        removeBtn.setOnAction(e -> {
            Node src = (Node) e.getSource();
            Node p = src.getParent();
            if (parent != null && p != null) {
                parent.getChildren().remove(p);
            }
        });

        HBox container = new HBox(10, ta, removeBtn);
        container.setPadding(new Insets(5));
        container.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        return container;
    }

    @FXML
    private void saveInformation(ActionEvent event) {
        try {
            if (!validateInputs()) {
                return;
            }

            CVData cvData = new CVData();
            cvData.setFullName(getTextOrNull(fullNameField));
            cvData.setEmail(getTextOrNull(emailField));
            cvData.setPhone(getTextOrNull(phoneField));
            cvData.setAddress(getTextOrNull(addressField));
            cvData.setSkills(getTextOrNull(skillsField));

            if (imageSelect != null && imageSelect.getImage() != null) {
                cvData.setProfileImage(imageSelect.getImage());
            }

            collectTextAreasFromBox(eduBox, cvData::addEducation);
            collectTextAreasFromBox(expBox, cvData::addExperience);
            collectTextAreasFromBox(projBox, cvData::addProject);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CV.fxml"));
            Parent root = loader.load();
            showCV cvController = loader.getController();
            cvController.displayCV(cvData);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneUtils.switchScene(stage, root, "CV Preview");

        } catch (Exception e) {
            System.err.println("Error while saving information and displaying CV: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "An unexpected error occurred while saving your CV. " + e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Save Error");
            alert.showAndWait();
        }
    }

    private String getTextOrNull(TextField tf) {
        if (tf == null) return null;
        String t = tf.getText();
        return (t != null && !t.trim().isEmpty()) ? t.trim() : null;
    }

    private String getTextOrNull(TextArea ta) {
        if (ta == null) return null;
        String t = ta.getText();
        return (t != null && !t.trim().isEmpty()) ? t.trim() : null;
    }

    private void collectTextAreasFromBox(VBox box, java.util.function.Consumer<String> consumer) {
        if (box == null || consumer == null) return;
        List<Node> children = box.getChildren();
        for (Node n : children) {
            if (n instanceof HBox) {
                HBox h = (HBox) n;
                for (Node c : h.getChildren()) {
                    if (c instanceof TextArea) {
                        TextArea ta = (TextArea) c;
                        if (ta.getText() != null && !ta.getText().trim().isEmpty()) {
                            consumer.accept(ta.getText().trim());
                        }
                    }
                }
            } else if (n instanceof TextArea) {
                TextArea ta = (TextArea) n;
                if (ta.getText() != null && !ta.getText().trim().isEmpty()) {
                    consumer.accept(ta.getText().trim());
                }
            }
        }
    }

    private boolean validateInputs() {
        String name = (fullNameField != null) ? fullNameField.getText() : null;
        String email = (emailField != null) ? emailField.getText() : null;
        String phone = (phoneField != null) ? phoneField.getText() : null;
        String address=(addressField != null) ? addressField.getText() : null;
        Image image = (imageSelect != null) ? imageSelect.getImage() : null;

        if(image == null)
        {
            showWarning("Please select an image");
            return false;
        }
        if (name == null || name.trim().isEmpty()) {
            showWarning("Full name is required.");
            return false;
        }
        if (email == null || email.trim().isEmpty() ) {
            showWarning("Please enter a email address.");
            return false;
        }
        if (phone == null || phone.trim().isEmpty()) {
            showWarning("Phone number is required.");
            return false;
        }
        if(address == null || address.trim().isEmpty()){
            showWarning("Please enter a valid address.");
            return false;
        }
        if (!hasAtLeastOneEntry(eduBox)) {
            showWarning("At least one education entry is required.");
            return false;
        }
        return true;
    }

    private boolean hasAtLeastOneEntry(VBox box) {
        if (box == null) return false;
        List<Node> children = box.getChildren();
        for (Node n : children) {
            if (n instanceof HBox) {
                HBox h = (HBox) n;
                for (Node c : h.getChildren()) {
                    if (c instanceof TextArea) {
                        TextArea ta = (TextArea) c;
                        if (ta.getText() != null && !ta.getText().trim().isEmpty()) {
                            return true;
                        }
                    }
                }
            } else if (n instanceof TextArea) {
                TextArea ta = (TextArea) n;
                if (ta.getText() != null && !ta.getText().trim().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText("Invalid input");
        alert.showAndWait();
    }
}
