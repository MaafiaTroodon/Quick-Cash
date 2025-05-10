package com.example.quickcash.validator;

public class JobValidator {

    public boolean isValidTitle(String title) {
        return title != null && !title.isEmpty();
    }

    public boolean isValidDescription(String description) {
        return description != null && description.length() <= 150;
    }

    public boolean isValidLocation(String location) {
        return location != null && !location.isEmpty();
    }

    public boolean isValidType(String type) {
        return type != null && !type.isEmpty()
                && (type.equals("Full-Time") || type.equals("Part-Time")
                || type.equals("Contract") || type.equals("Freelance")
                || type.equals("Internship") || type.equals("Temporary")
                || type.equals("Volunteer"));
    }

    public boolean isValidSalary(String salaryText) {
        if (salaryText == null || salaryText.isEmpty()) {
            return false;
        }
        try {
            double salary = Double.parseDouble(salaryText);
            return salary >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isValidCompany(String company) {
        return company != null && !company.isEmpty();
    }

    public boolean isValidEmployerPhone(String employerPhone) {
        return employerPhone != null && !employerPhone.isEmpty() && employerPhone.matches("\\+?[0-9]{10,13}");
    }

    public boolean isValidEmployerName(String employerName) {
        return employerName != null && !employerName.isEmpty();
    }


}
