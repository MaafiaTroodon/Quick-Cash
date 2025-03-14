package com.example.quickcash.model;

public class PreferEmployeeModel {
    private String employeeEmail;
    private String employeeName;
    private String addedTime;

    public PreferEmployeeModel() {
    }

    public PreferEmployeeModel(String employeeEmail, String employeeName, String addedTime) {
        this.employeeEmail = employeeEmail;
        this.employeeName = employeeName;
        this.addedTime = addedTime;
    }

    // Getters
    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getAddedTime() {
        return addedTime;
    }

    // Setters
    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        PreferEmployeeModel that = (PreferEmployeeModel) obj;

        return employeeEmail.equals(that.employeeEmail) && employeeName.equals(that.employeeName);
    }
}