package com.example.cvbuilder;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class CVData {
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String skills;
    private Image profileImage;
    private List<String> educationList;
    private List<String> experienceList;
    private List<Project> projectList;

    public CVData() {
        this.educationList = new ArrayList<>();
        this.experienceList = new ArrayList<>();
        this.projectList = new ArrayList<>();
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public Image getProfileImage() { return profileImage; }
    public void setProfileImage(Image profileImage) { this.profileImage = profileImage; }

    public List<String> getEducationList() { return educationList; }
    public void addEducation(String education) { this.educationList.add(education); }

    public List<String> getExperienceList() { return experienceList; }
    public void addExperience(String experience) { this.experienceList.add(experience); }

    public List<Project> getProjectList() { return projectList; }
    public void addProject(Project project) { this.projectList.add(project); }

    public static class Project {
        private String title;
        private String description;
        private String link;

        public Project(String title, String description, String link) {
            this.title = title;
            this.description = description;
            this.link = link;
        }

        public String getTitle() { return title; }

        public String getDescription() { return description; }

        public String getLink() { return link; }
    }
}
