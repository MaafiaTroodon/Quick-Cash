package com.example.quickcash.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class JobModel implements Parcelable {
    private String title, description, location, type, salaryText, company, employerEmail;
    private List<String> applicants;

    public JobModel() {
        applicants = new ArrayList<>();
    }

    public JobModel(String title, String description, String location, String type,
                    String salaryText, String company, String employerEmail) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.salaryText = salaryText;
        this.company = company;
        this.employerEmail = employerEmail;
        this.applicants = new ArrayList<>();
    }

    protected JobModel(Parcel in) {
        title = in.readString();
        description = in.readString();
        location = in.readString();
        type = in.readString();
        salaryText = in.readString();
        company = in.readString();
        employerEmail = in.readString();
        applicants = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeString(type);
        dest.writeString(salaryText);
        dest.writeString(company);
        dest.writeString(employerEmail);
        dest.writeStringList(applicants);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<JobModel> CREATOR = new Creator<JobModel>() {
        @Override
        public JobModel createFromParcel(Parcel in) {
            return new JobModel(in);
        }

        @Override
        public JobModel[] newArray(int size) {
            return new JobModel[size];
        }
    };

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getSalaryText() { return salaryText; }
    public String getCompany() { return company; }
    public String getEmployerEmail() { return employerEmail; }
    public List<String> getApplicants() { return applicants; }
    public void setApplicants(List<String> applicants) { this.applicants = applicants; }

    public void addApplicant(String email) {
        if (applicants == null) {
            applicants = new ArrayList<>();
        }
        if (!applicants.contains(email)) {
            applicants.add(email);
        }
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}