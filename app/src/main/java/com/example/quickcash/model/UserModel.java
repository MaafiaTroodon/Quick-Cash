package com.example.quickcash.model;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private String username;
    private String email;
    private String password;
    private String role;
    private SecurityModel securityAns;
    private List<PreferEmployerModel> preferredJob = new ArrayList<>();

    public UserModel() {
    }

    public UserModel(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.preferredJob.add(createdTempModel());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SecurityModel getSecurityAns() {
        return securityAns;
    }

    public void setSecurityAns(SecurityModel securityAns) {
        this.securityAns = securityAns;
    }
    public List<PreferEmployerModel> getPreferredJob() { return preferredJob; }

    public void setPreferredJob(List<PreferEmployerModel> jobModelList) {
        preferredJob = jobModelList;
    }

    protected PreferEmployerModel createdTempModel() {
        return new PreferEmployerModel();
    }
}
