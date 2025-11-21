package com.example.cvbuilder;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class CVData {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String skills;
    private Image profileImage;
    private String profileImagePath;
    private List<Education> educationList;
    private List<Experience> experienceList;
    private List<Project> projectList;

    public CVData() {
        this.educationList = new ArrayList<>();
        this.experienceList = new ArrayList<>();
        this.projectList = new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Image getProfileImage() { return profileImage; }
    public void setProfileImage(Image profileImage) { this.profileImage = profileImage; }

    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }

    public List<Education> getEducationList() { return educationList; }
    public void addEducation(Education education) { this.educationList.add(education); }

    public List<Experience> getExperienceList() { return experienceList; }
    public void addExperience(Experience experience) { this.experienceList.add(experience); }

    public List<Project> getProjectList() { return projectList; }
    public void addProject(Project project) { this.projectList.add(project); }

    public static class Education {
        private String school;
        private String degree;
        private String result;

        public Education(String school, String degree, String result) {
            this.school = school;
            this.degree = degree;
            this.result = result;
        }

        public String getSchool() { return school; }
        public String getDegree() { return degree; }
        public String getResult() { return result; }
    }

    public static class Experience {
        private String jobTitle;
        private String company;
        private String startDate;
        private String endDate;
        private boolean currentlyWorking;

        public Experience(String jobTitle, String company, String startDate, String endDate, boolean currentlyWorking) {
            this.jobTitle = jobTitle;
            this.company = company;
            this.startDate = startDate;
            this.endDate = endDate;
            this.currentlyWorking = currentlyWorking;
        }

        public String getJobTitle() { return jobTitle; }
        public String getCompany() { return company; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public boolean isCurrentlyWorking() { return currentlyWorking; }
    }

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
