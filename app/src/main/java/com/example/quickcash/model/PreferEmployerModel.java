package com.example.quickcash.model;

public class PreferEmployerModel {
    private String employerEmail, company, addedTime;


    public PreferEmployerModel() {
    }

    public PreferEmployerModel(JobModel jobModel, String time) {
        this.employerEmail = jobModel.getEmployerEmail();
        this.company = jobModel.getCompany();
        this.addedTime = time;
    }

    public String getEmployerEmail() {
        return this.employerEmail;
    }

    public String getCompany() {
        return this.company;
    }
    public String getAddedTime() {
        return this.addedTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        PreferEmployerModel that = (PreferEmployerModel) obj;

        return company.equals(that.company) && employerEmail.equals(that.employerEmail);
    }
}