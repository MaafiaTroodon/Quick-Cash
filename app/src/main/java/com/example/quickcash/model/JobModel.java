package com.example.quickcash.model;

public class JobModel {
    private String title;
    private String description;
    private String location;
    private String type;
    private String salaryText;
    private String company;
    private String EmployerEmail;

    // 🔹 No-argument constructor (Required by Firebase)
    public JobModel() {
        this.title = "title";
        this.description = "description";
        this.location = "location";
        this.type = "type";
        this.salaryText = "salaryText";
        this.company = "company";
        this.EmployerEmail = "employerEmail";
    }

    public JobModel(String title, String description, String location, String type, String salaryText, String company, String employerEmail) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.salaryText = salaryText;
        this.company = company;
        this.EmployerEmail = employerEmail;
    }

    public JobModel(String title, String location, String salaryText,String company) {
        this.title = title;
        this.location = location;
        this.salaryText = salaryText;
        this.company = company;
    }

    // 🔹 Getters for Firebase serialization
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getSalaryText() {
        return salaryText;
    }

    public String getCompany() {
        return company;
    }

    public String getEmployerEmail() {
        return EmployerEmail;
    }
}
