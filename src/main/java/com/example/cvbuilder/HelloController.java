package com.example.cvbuilder;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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

    private String selectedImagePath;

    private CVData editingCV;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (eduBox != null && eduBox.getChildren().isEmpty()) {
            eduBox.getChildren().add(createRemovableEducationFields(eduBox, false));
        }
    }

    @FXML
    private void imageChange() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            if (imageSelect != null) imageSelect.setImage(image);
            selectedImagePath = file.getAbsolutePath();
        }
    }

    @FXML
    private void addEducation() {
        if (eduBox != null) {
            eduBox.getChildren().add(createRemovableEducationFields(eduBox,true));
        }
    }

    @FXML
    private void addExperience() {
        if (expBox != null) {
            expBox.getChildren().add(createRemovableExperienceFields(expBox));
        }
    }

    @FXML
    private void addProject() {
        if (projBox != null) {
            projBox.getChildren().add(createRemovableProjectFields(projBox));
        }
    }

    private VBox createRemovableEducationFields(VBox parent, boolean rmbtn) {
        VBox educationContainer = new VBox(8);
        educationContainer.getStyleClass().add("removable-container");

        TextField schoolField = new TextField();
        schoolField.setPromptText("School/College/University");
        schoolField.setUserData("school");

        TextField degreeField = new TextField();
        degreeField.setPromptText("Degree");
        degreeField.setUserData("degree");

        TextField resultField = new TextField();
        resultField.setPromptText("Result");
        resultField.setUserData("result");

       if(rmbtn){
           Button removeBtn = new Button("Remove Education");
           removeBtn.getStyleClass().add("danger-button");
           removeBtn.setOnAction(e -> {
               e.consume();
               if (parent != null) {
                   parent.getChildren().remove(educationContainer);
               }
           });

           HBox buttonBox = new HBox(removeBtn);
           buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

           educationContainer.getChildren().addAll(schoolField, degreeField, resultField, buttonBox);
           return educationContainer;
       }
        educationContainer.getChildren().addAll(schoolField, degreeField, resultField);
        return educationContainer;

    }

    private VBox createRemovableExperienceFields(VBox parent) {
        VBox experienceContainer = new VBox(8);
        experienceContainer.getStyleClass().add("removable-container");
        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Job Title");
        jobTitleField.setUserData("jobTitle");
        TextField companyField = new TextField();
        companyField.setPromptText("Company Name");
        companyField.setUserData("company");
        TextField startDateField = new TextField();
        startDateField.setPromptText("Start Date (e.g., Jan 2022)");
        startDateField.setUserData("startDate");
        TextField endDateField = new TextField();
        endDateField.setPromptText("End Date (e.g., Nov 2025)");
        endDateField.setUserData("endDate");
        javafx.scene.control.CheckBox currentlyWorkingCheckBox = new javafx.scene.control.CheckBox("Currently working here");
        currentlyWorkingCheckBox.setUserData("currentlyWorking");

        currentlyWorkingCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal) {
                endDateField.setDisable(true);
                endDateField.clear();
            } else {
                endDateField.setDisable(false);
            }
        });
        Button removeBtn = new Button("Remove Experience");
        removeBtn.getStyleClass().add("danger-button");
        removeBtn.setOnAction(e -> {
            e.consume();
            if (parent != null) {
                parent.getChildren().remove(experienceContainer);
            }
        });
        HBox buttonBox = new HBox(removeBtn);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        experienceContainer.getChildren().addAll(jobTitleField, companyField, startDateField, endDateField, currentlyWorkingCheckBox, buttonBox);
        return experienceContainer;
    }

    private VBox createRemovableProjectFields(VBox parent) {
        VBox projectContainer = new VBox(8);
        projectContainer.getStyleClass().add("removable-container");

        TextField titleField = new TextField();
        titleField.setPromptText("Project Title");
        titleField.setUserData("title");

        TextArea descArea = new TextArea();
        descArea.setPrefRowCount(3);
        descArea.setPromptText("Project Description");
        descArea.setUserData("description");
        VBox.setVgrow(descArea, javafx.scene.layout.Priority.ALWAYS);

        TextField linkField = new TextField();
        linkField.setPromptText("Project Link (Optional)");
        linkField.setUserData("link");

        Button removeBtn = new Button("Remove Project");
        removeBtn.getStyleClass().add("danger-button");
        removeBtn.setOnAction(e -> {
            e.consume();
            if (parent != null) {
                parent.getChildren().remove(projectContainer);
            }
        });

        HBox buttonBox = new HBox(removeBtn);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        projectContainer.getChildren().addAll(titleField, descArea, linkField, buttonBox);
        return projectContainer;
    }

    @FXML
    private void saveInformation(ActionEvent event) {
        try {
            if (!validateInputs()) {
                return;
            }

            CVData cvData = new CVData();
            if (editingCV != null && editingCV.getId() != null) {
                cvData.setId(editingCV.getId());
            }

            cvData.setFullName(getTextOrNull(fullNameField));
            cvData.setEmail(getTextOrNull(emailField));
            cvData.setPhone(getTextOrNull(phoneField));
            cvData.setAddress(getTextOrNull(addressField));
            cvData.setSkills(getTextOrNull(skillsField));

            if (imageSelect != null && imageSelect.getImage() != null) {
                cvData.setProfileImage(imageSelect.getImage());
            }
            if (selectedImagePath != null) {
                String copiedPath = copyImageToProject(selectedImagePath);
                if (copiedPath != null) {
                    cvData.setProfileImagePath(copiedPath);
                } else {
                    Alert warn = new Alert(Alert.AlertType.WARNING, "Failed to copy selected image into project resources. The image will not be saved with this CV.", ButtonType.OK);
                    warn.setHeaderText(null);
                    warn.showAndWait();
                }
            } else if (editingCV != null && editingCV.getProfileImagePath() != null) {
                cvData.setProfileImagePath(editingCV.getProfileImagePath());
            }

            collectEducationsFromBox(eduBox, cvData::addEducation);
            collectExperiencesFromBox(expBox, cvData::addExperience);
            collectProjectsFromBox(projBox, cvData::addProject);

            CVDao dao = new CVDao();
            long cvId;
            try {
                if (cvData.getId() != null) {
                    boolean updated = dao.updateCV(cvData);
                    if (!updated) {
                        throw new SQLException("No rows updated for CV id " + cvData.getId());
                    }
                    cvId = cvData.getId();
                } else {
                    cvId = dao.createCV(cvData);
                }
                cvData.setId(cvId);
            } catch (SQLException sqe) {
                System.out.println(sqe.getMessage());
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CV.fxml"));
            Parent root = loader.load();
            showCV cvController = loader.getController();
            cvController.displayCV(cvData);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneUtils.switchScene(stage, root, "CV Preview");
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("CV Ready");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Your CV has been successfully generated!");
            successAlert.showAndWait();
        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "An unexpected error occurred while saving your CV. " + e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Save Error");
            alert.showAndWait();
        }
    }

    /**
     * Copy the selected image file into the project's resource images folder:
     * src/main/resources/com/example/cvbuilder/images
     * Returns the classpath-relative path (e.g. "images/123_filename.png") that can be used
     * with getResource("/com/example/cvbuilder/" + relativePath), or null if the copy failed.
     */
    private String copyImageToProject(String sourceAbsolutePath) {
        if (sourceAbsolutePath == null) return null;
        try {
             Path src = Paths.get(sourceAbsolutePath);
             if (!Files.exists(src)) return null;

            Path projectDir = Paths.get(System.getProperty("user.dir"));
            Path imagesPath = projectDir.resolve("src").resolve("main").resolve("resources").resolve("com").resolve("example").resolve("cvbuilder").resolve("images");
            if (Files.notExists(imagesPath)) {
                Files.createDirectories(imagesPath);
            }

            String originalFileName = src.getFileName().toString();
            String destFileName = System.currentTimeMillis() + "_" + originalFileName;
            Path dest = imagesPath.resolve(destFileName);

            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
            String rel = "images/" + destFileName;
            System.out.println("Copied image to resources: " + dest.toAbsolutePath());
            return rel.replace('\\', '/');
        } catch (IOException ioe) {

            return null;
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

    private void collectEducationsFromBox(VBox box, java.util.function.Consumer<CVData.Education> consumer) {
        if (box == null || consumer == null) return;
        List<Node> children = box.getChildren();
        for (Node n : children) {
            if (n instanceof VBox educationVBox) {
                String school = null;
                String degree = null;
                String result = null;

                for (Node child : educationVBox.getChildren()) {
                    if (child instanceof TextField tf) {
                        if ("school".equals(tf.getUserData())) {
                            school = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        } else if ("degree".equals(tf.getUserData())) {
                            degree = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        } else if ("result".equals(tf.getUserData())) {
                            result = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        }
                    }
                }

                if (school != null || degree != null || result != null) {
                    consumer.accept(new CVData.Education(school, degree, result));
                }
            }
        }
    }

    private void collectExperiencesFromBox(VBox box, java.util.function.Consumer<CVData.Experience> consumer) {
        if (box == null || consumer == null) return;
        List<Node> children = box.getChildren();
        for (Node n : children) {
            if (n instanceof VBox experienceVBox) {
                String jobTitle = null;
                String company = null;
                String startDate = null;
                String endDate = null;
                boolean currentlyWorking = false;

                for (Node child : experienceVBox.getChildren()) {
                    if (child instanceof TextField tf) {
                        if ("jobTitle".equals(tf.getUserData())) {
                            jobTitle = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        } else if ("company".equals(tf.getUserData())) {
                            company = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        } else if ("startDate".equals(tf.getUserData())) {
                            startDate = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        } else if ("endDate".equals(tf.getUserData())) {
                            endDate = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        }
                    } else if (child instanceof javafx.scene.control.CheckBox cb) {
                        if ("currentlyWorking".equals(cb.getUserData())) {
                            currentlyWorking = cb.isSelected();
                        }
                    }
                }

                if (jobTitle != null || company != null || startDate != null) {
                    consumer.accept(new CVData.Experience(jobTitle, company, startDate, endDate, currentlyWorking));
                }
            }
        }
    }

    private void collectProjectsFromBox(VBox box, java.util.function.Consumer<CVData.Project> consumer) {
        if (box == null || consumer == null) return;
        List<Node> children = box.getChildren();
        for (Node n : children) {
            if (n instanceof VBox projectVBox) {
                String title = null;
                String description = null;
                String link = null;
                for (Node child : projectVBox.getChildren()) {
                    if (child instanceof TextField tf) {
                        if ("title".equals(tf.getUserData())) {
                            title = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        } else if ("link".equals(tf.getUserData())) {
                            link = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        }
                    } else if (child instanceof TextArea ta) {
                        if ("description".equals(ta.getUserData())) {
                            description = ta.getText() != null && !ta.getText().trim().isEmpty() ? ta.getText().trim() : null;
                        }
                    }
                }
                if (title != null || description != null) {
                    consumer.accept(new CVData.Project(title, description, link));
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

        if (hasEmptyFields(eduBox, "education")) {
            showWarning("Please fill in all education fields or remove empty ones.");
            return false;
        }

        if (hasEmptyFields(expBox, "experience")) {
            showWarning("Please fill in all experience fields or remove empty ones.");
            return false;
        }

        if (hasEmptyProjectFields(projBox)) {
            showWarning("Please fill in all project fields or remove empty ones.");
            return false;
        }

        return true;
    }

    private boolean hasAtLeastOneEntry(VBox box) {
        if (box == null) return false;
        List<Node> children = box.getChildren();
        for (Node n : children) {
            if (n instanceof VBox educationVBox) {
                for (Node child : educationVBox.getChildren()) {
                    if (child instanceof TextField tf) {
                        if (tf.getText() != null && !tf.getText().trim().isEmpty()) {
                            return true;
                        }
                    }
                }
            } else if (n instanceof HBox h) {
                for (Node c : h.getChildren()) {
                    if (c instanceof TextArea ta) {
                        if (ta.getText() != null && !ta.getText().trim().isEmpty()) {
                            return true;
                        }
                    }
                }
            } else if (n instanceof TextArea ta) {
                if (ta.getText() != null && !ta.getText().trim().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasEmptyFields(VBox box, String fieldType) {
        if (box == null) return false;
        List<Node> children = box.getChildren();

        for (Node n : children) {
            if ("education".equals(fieldType) && n instanceof VBox educationVBox) {
                String school = null;
                String degree = null;
                String result = null;

                for (Node child : educationVBox.getChildren()) {
                    if (child instanceof TextField tf) {
                        if ("school".equals(tf.getUserData())) {
                            school = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        } else if ("degree".equals(tf.getUserData())) {
                            degree = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        } else if ("result".equals(tf.getUserData())) {
                            result = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        }
                    }
                }

                if (school == null && degree == null && result == null) {
                    return true;
                }
            } else if ("experience".equals(fieldType) && n instanceof VBox experienceVBox) {
                String jobTitle = null;
                String company = null;
                String startDate = null;

                for (Node child : experienceVBox.getChildren()) {
                    if (child instanceof TextField tf) {
                        if ("jobTitle".equals(tf.getUserData())) {
                            jobTitle = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        } else if ("company".equals(tf.getUserData())) {
                            company = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        } else if ("startDate".equals(tf.getUserData())) {
                            startDate = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        }
                    }
                }

                if (jobTitle == null || company == null || startDate == null) {
                    return true;
                }
            } else if (n instanceof HBox h) {
                for (Node c : h.getChildren()) {
                    if (c instanceof TextArea ta) {
                        if (ta.getText() == null || ta.getText().trim().isEmpty()) {
                            return true;
                        }
                    }
                }
            } else if (n instanceof TextArea ta) {
                if (ta.getText() == null || ta.getText().trim().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean hasEmptyProjectFields(VBox box) {
        if (box == null) return false;
        List<Node> children = box.getChildren();

        for (Node n : children) {
            if (n instanceof VBox projectVBox) {
                String title = null;
                String description = null;
                boolean titleFieldExists = false;
                boolean descFieldExists = false;

                for (Node child : projectVBox.getChildren()) {
                    if (child instanceof TextField tf) {
                        if ("title".equals(tf.getUserData())) {
                            titleFieldExists = true;
                            title = tf.getText() != null && !tf.getText().trim().isEmpty() ? tf.getText().trim() : null;
                        }
                    } else if (child instanceof TextArea ta) {
                        if ("description".equals(ta.getUserData())) {
                            descFieldExists = true;
                            description = ta.getText() != null && !ta.getText().trim().isEmpty() ? ta.getText().trim() : null;
                        }
                    }
                }

                if ((titleFieldExists && title == null) || (descFieldExists && description == null)) {
                    return true;
                }
            }
        }
        return false;
    }
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setTitle("Invalid input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadCVData(CVData cv) {
        if (cv == null) return;
        this.editingCV = cv;

        if (fullNameField != null) fullNameField.setText(cv.getFullName());
        if (emailField != null) emailField.setText(cv.getEmail());
        if (phoneField != null) phoneField.setText(cv.getPhone());
        if (addressField != null) addressField.setText(cv.getAddress());
        if (skillsField != null) skillsField.setText(cv.getSkills());

        // load image if path present
        if (cv.getProfileImagePath() != null && imageSelect != null) {
            try {
                File f = new File(cv.getProfileImagePath());
                if (f.exists()) {
                    imageSelect.setImage(new Image(f.toURI().toString()));
                } else {
                    var is = getClass().getResourceAsStream((cv.getProfileImagePath().startsWith("/") ? cv.getProfileImagePath() : "/com/example/cvbuilder/" + cv.getProfileImagePath()));
                    if (is != null) imageSelect.setImage(new Image(is));
                }
            } catch (Exception e) {
                // ignore loading errors
            }
        }

        if (eduBox != null) {
            eduBox.getChildren().clear();
            if (cv.getEducationList() != null && !cv.getEducationList().isEmpty()) {
                for (CVData.Education e : cv.getEducationList()) {
                     VBox box = createRemovableEducationFields(eduBox, true);
                     // set values
                     for (Node n : box.getChildren()) {
                         if (n instanceof TextField tf) {
                             if ("school".equals(tf.getUserData())) tf.setText(e.getSchool());
                             if ("degree".equals(tf.getUserData())) tf.setText(e.getDegree());
                             if ("result".equals(tf.getUserData())) tf.setText(e.getResult());
                         }
                     }
                     eduBox.getChildren().add(box);
                 }
             } else {
                 eduBox.getChildren().add(createRemovableEducationFields(eduBox, false));
             }
         }

         if (expBox != null) {
             expBox.getChildren().clear();
             if (cv.getExperienceList() != null && !cv.getExperienceList().isEmpty()) {
                 for (CVData.Experience ex : cv.getExperienceList()) {
                     VBox box = createRemovableExperienceFields(expBox);
                     for (Node n : box.getChildren()) {
                         if (n instanceof TextField tf) {
                             if ("jobTitle".equals(tf.getUserData())) tf.setText(ex.getJobTitle());
                             if ("company".equals(tf.getUserData())) tf.setText(ex.getCompany());
                             if ("startDate".equals(tf.getUserData())) tf.setText(ex.getStartDate());
                             if ("endDate".equals(tf.getUserData())) tf.setText(ex.getEndDate());
                         } else if (n instanceof javafx.scene.control.CheckBox cb) {
                             if ("currentlyWorking".equals(cb.getUserData())) cb.setSelected(ex.isCurrentlyWorking());
                         }
                     }
                     expBox.getChildren().add(box);
                 }
             }
         }

         if (projBox != null) {
             projBox.getChildren().clear();
             if (cv.getProjectList() != null && !cv.getProjectList().isEmpty()) {
                 for (CVData.Project p : cv.getProjectList()) {
                     VBox box = createRemovableProjectFields(projBox);
                     for (Node n : box.getChildren()) {
                         if (n instanceof TextField tf) {
                             if ("title".equals(tf.getUserData())) tf.setText(p.getTitle());
                             if ("link".equals(tf.getUserData())) tf.setText(p.getLink());
                         } else if (n instanceof TextArea ta) {
                             if ("description".equals(ta.getUserData())) ta.setText(p.getDescription());
                         }
                     }
                     projBox.getChildren().add(box);
                 }
             }
         }
     }

 }
