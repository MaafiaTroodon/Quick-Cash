package com.example.quickcash;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.quickcash.model.JobModel;

public class JobModelTest {
    private JobModel jobModel;
    String title = "software engineer", description = "this is a great job!",
            location = "Halifax", type = "Full-Time", salaryText = "23.50",
            company = "software inc", employerEmail = "taiki@gmail.com",
            employerPhone = "+1234567890", employerName = "John Doe";

    @Test
    public void checkIsModelSet() {
        jobModel = new JobModel(title,description,location,type,salaryText,company,employerEmail, employerPhone, employerName);
        assertEquals(jobModel.getTitle(), title);
        assertEquals(jobModel.getDescription(), description);
        assertEquals(jobModel.getLocation(), location);
        assertEquals(jobModel.getType(), type);
        assertEquals(jobModel.getSalaryText(), salaryText);
        assertEquals(jobModel.getCompany(), company);
        assertEquals(jobModel.getEmployerEmail(), employerEmail);
        assertEquals(jobModel.getEmployerPhone(), employerPhone);
        assertEquals(jobModel.getEmployerName(), employerName);

    }
    @Test
    public void checkIfModelIsAppliable() {
        jobModel = new JobModel(title, description, location, type, salaryText, company, employerEmail, employerPhone, employerName);
        String testEmail = "test12345@gmail.com";
        jobModel.addApplicant(testEmail);
        assertTrue(jobModel.isAppliable(testEmail));
    }



}
