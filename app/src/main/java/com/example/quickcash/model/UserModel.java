package com.example.quickcash.model;

public class UserModel {
    private String username;
    private String email;
    private String password;
    private SecurityModel securityAns;

    public UserModel() {
    }

    public UserModel(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SecurityModel getSecurityAns() {
        return securityAns;
    }

    public void setSecurityAns(String q1ans, String q2ans, String q3ans) {
        this.securityAns = new SecurityModel(q1ans, q2ans, q3ans);
    }
}
