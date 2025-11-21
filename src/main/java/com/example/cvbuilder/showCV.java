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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
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
        System.out.println("Name: " + cvData.getFullName());
        System.out.println("Email: " + cvData.getEmail());
        System.out.println("Phone: " + cvData.getPhone());
        System.out.println("Address: " + cvData.getAddress());
        System.out.println("Skills: " + cvData.getSkills());
        System.out.println("Image: " + cvData.getProfileImagePath());
        if (cvData.getProfileImage() != null && profileImageView != null) {
            profileImageView.setImage(cvData.getProfileImage());
        }
//        else if (profileImageView != null) {
//            String path = cvData.getProfileImagePath();
//            if (path != null && !path.trim().isEmpty()) {
//                try {
//                    Image img = null;
//                    File f = new File(path);
//                    if (f.exists()) {
//                        img = new Image(f.toURI().toString());
//                    } else {File rel = Paths.get(System.getProperty("user.dir")).resolve(path).toFile();
//                        if (rel.exists()) {
//                            img = new Image(rel.toURI().toString());
//                        } else {
//                            URL res = getClass().getResource(path.startsWith("/") ? path : "/com/example/cvbuilder/" + path);
//                            if (res != null) img = new Image(res.toString());
//                        }
//                    }
//
//                    if (img != null) {
//                        profileImageView.setImage(img);
//                        cvData.setProfileImage(img);
//                    } else {
//                        URL person = getClass().getResource("person.png");
//                        if (person != null) {
//                            profileImageView.setImage(new Image(person.toString()));
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        if (educationVBox != null) {
            educationVBox.getChildren().clear();
            if (cvData.getEducationList() != null && !cvData.getEducationList().isEmpty()) {
                for (int i = 0; i < cvData.getEducationList().size(); i++) {
                    CVData.Education edu = cvData.getEducationList().get(i);

                    GridPane eduGrid = new GridPane();
                    eduGrid.setHgap(15);
                    eduGrid.setVgap(8);
                    eduGrid.getStyleClass().add("content-grid");

                    if (i < cvData.getEducationList().size() - 1) {
                        VBox.setMargin(eduGrid, new javafx.geometry.Insets(0, 0, 12, 0));
                    }
                    int row = 0;

                    if (edu.getDegree() != null && !edu.getDegree().trim().isEmpty()) {
                        Label degreeLabel = new Label("Degree:");
                        degreeLabel.getStyleClass().add("field-label");

                        Label degreeValue = new Label(edu.getDegree());
                        degreeValue.getStyleClass().add("value-primary");
                        degreeValue.setWrapText(true);
                        degreeValue.setMaxWidth(600);

                        eduGrid.add(degreeLabel, 0, row);
                        eduGrid.add(degreeValue, 1, row);
                        row++;
                    }

                    if (edu.getSchool() != null && !edu.getSchool().trim().isEmpty()) {
                        Label schoolLabel = new Label("Institution:");
                        schoolLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #555; -fx-min-width: 100;");

                        Label schoolValue = new Label(edu.getSchool());
                        schoolValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #3498db; -fx-font-style: italic;");
                        schoolValue.setWrapText(true);
                        schoolValue.setMaxWidth(600);

                        eduGrid.add(schoolLabel, 0, row);
                        eduGrid.add(schoolValue, 1, row);
                        row++;
                    }

                    if (edu.getResult() != null && !edu.getResult().trim().isEmpty()) {
                        Label resultLabel = new Label("Result:");
                        resultLabel.getStyleClass().add("field-label");

                        Label resultValue = new Label(edu.getResult());
                        resultValue.getStyleClass().add("value-success");

                        eduGrid.add(resultLabel, 0, row);
                        eduGrid.add(resultValue, 1, row);
                    }

                    educationVBox.getChildren().add(eduGrid);
                }
            } else {
                Label emptyLabel = new Label("No education information provided.");
                emptyLabel.getStyleClass().add("empty-label");
                educationVBox.getChildren().add(emptyLabel);
            }
        }

        if (experienceVBox != null) {
            experienceVBox.getChildren().clear();
            if (cvData.getExperienceList() != null && !cvData.getExperienceList().isEmpty()) {
                for (int i = 0; i < cvData.getExperienceList().size(); i++) {
                    CVData.Experience exp = cvData.getExperienceList().get(i);

                    // Create a table-like structure using GridPane
                    GridPane expGrid = new GridPane();
                    expGrid.setHgap(15);
                    expGrid.setVgap(8);
                    expGrid.getStyleClass().add("content-grid");

                    if (i < cvData.getExperienceList().size() - 1) {
                        VBox.setMargin(expGrid, new javafx.geometry.Insets(0, 0, 12, 0));
                    }

                    int row = 0;

                    if (exp.getJobTitle() != null && !exp.getJobTitle().trim().isEmpty()) {
                        Label titleLabel = new Label("Position:");
                        titleLabel.getStyleClass().add("field-label");

                        Label titleValue = new Label(exp.getJobTitle());
                        titleValue.getStyleClass().add("value-primary");
                        titleValue.setWrapText(true);
                        titleValue.setMaxWidth(600);

                        expGrid.add(titleLabel, 0, row);
                        expGrid.add(titleValue, 1, row);
                        row++;
                    }

                    if (exp.getCompany() != null && !exp.getCompany().trim().isEmpty()) {
                        Label companyLabel = new Label("Company:");
                        companyLabel.getStyleClass().add("field-label");

                        Label companyValue = new Label(exp.getCompany());
                        companyValue.getStyleClass().add("value-secondary");
                        companyValue.setWrapText(true);
                        companyValue.setMaxWidth(600);

                        expGrid.add(companyLabel, 0, row);
                        expGrid.add(companyValue, 1, row);
                        row++;
                    }

                    StringBuilder dateRangeBuilder = new StringBuilder();
                    if (exp.getStartDate() != null && !exp.getStartDate().trim().isEmpty()) {
                        dateRangeBuilder.append(exp.getStartDate());
                    }

                    if (exp.isCurrentlyWorking()) {
                        if (dateRangeBuilder.length() > 0) {
                            dateRangeBuilder.append(" - Present");
                        } else {
                            dateRangeBuilder.append("Present");
                        }
                    } else if (exp.getEndDate() != null && !exp.getEndDate().trim().isEmpty()) {
                        if (dateRangeBuilder.length() > 0) {
                            dateRangeBuilder.append(" - ");
                        }
                        dateRangeBuilder.append(exp.getEndDate());
                    }

                    if (dateRangeBuilder.length() > 0) {
                        Label durationLabel = new Label("Duration:");
                        durationLabel.getStyleClass().add("field-label");

                        Label durationValue = new Label(dateRangeBuilder.toString());
                        durationValue.getStyleClass().add("value-normal");

                        if (exp.isCurrentlyWorking()) {
                            durationValue.getStyleClass().clear();
                            durationValue.getStyleClass().add("value-success");
                        }

                        expGrid.add(durationLabel, 0, row);
                        expGrid.add(durationValue, 1, row);
                    }

                    experienceVBox.getChildren().add(expGrid);
                }
            } else {
                Label emptyLabel = new Label("No experience information provided.");
                emptyLabel.getStyleClass().add("empty-label");
                experienceVBox.getChildren().add(emptyLabel);
            }
        }

        if (projectVBox != null) {
            projectVBox.getChildren().clear();
            if (cvData.getProjectList() != null && !cvData.getProjectList().isEmpty()) {
                for (int i = 0; i < cvData.getProjectList().size(); i++) {
                    CVData.Project proj = cvData.getProjectList().get(i);

                    GridPane projGrid = new GridPane();
                    projGrid.setHgap(15);
                    projGrid.setVgap(8);
                    projGrid.getStyleClass().add("content-grid");

                    if (i < cvData.getProjectList().size() - 1) {
                        VBox.setMargin(projGrid, new javafx.geometry.Insets(0, 0, 12, 0));
                    }

                    int row = 0;

                    if (proj.getTitle() != null && !proj.getTitle().trim().isEmpty()) {
                        Label titleLabel = new Label("Project:");
                        titleLabel.getStyleClass().add("field-label");

                        Label titleValue = new Label(proj.getTitle());
                        titleValue.getStyleClass().add("value-primary");
                        titleValue.setWrapText(true);
                        titleValue.setMaxWidth(600);

                        projGrid.add(titleLabel, 0, row);
                        projGrid.add(titleValue, 1, row);
                        row++;
                    }
                    if (proj.getDescription() != null && !proj.getDescription().trim().isEmpty()) {
                        Label descLabel = new Label("Description:");
                        descLabel.getStyleClass().add("field-label-top");
                        GridPane.setValignment(descLabel, javafx.geometry.VPos.TOP);

                        Label descValue = new Label(proj.getDescription());
                        descValue.setWrapText(true);
                        descValue.setMaxWidth(600);
                        descValue.getStyleClass().add("value-description");

                        projGrid.add(descLabel, 0, row);
                        projGrid.add(descValue, 1, row);
                        row++;
                    }

                    if (proj.getLink() != null && !proj.getLink().trim().isEmpty()) {
                        Label linkLabel = new Label("Link:");
                        linkLabel.getStyleClass().add("field-label");

                        Hyperlink hyperlink = new Hyperlink(proj.getLink());
                        hyperlink.getStyleClass().add("project-link");
                        hyperlink.setMaxWidth(600);
                        hyperlink.setWrapText(true);

                        hyperlink.setOnAction(event -> {
                            try {
                                Desktop.getDesktop().browse(new URI(proj.getLink()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        projGrid.add(linkLabel, 0, row);
                        projGrid.add(hyperlink, 1, row);
                    }

                    projectVBox.getChildren().add(projGrid);
                }
            } else {
                Label emptyLabel = new Label("No project information provided.");
                emptyLabel.getStyleClass().add("empty-label");
                projectVBox.getChildren().add(emptyLabel);
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
