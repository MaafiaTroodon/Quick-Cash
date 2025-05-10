// JobFilterManager.java
package com.example.quickcash.manager;

import com.example.quickcash.model.JobModel;
import java.util.ArrayList;
import java.util.List;

public class JobFilterManager {

    public List<JobModel> filterJobs(List<JobModel> fullJobList, String query) {
        List<JobModel> filteredJobs = new ArrayList<>();
        for (JobModel job : fullJobList) {
            if (job.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    job.getCompany().toLowerCase().contains(query.toLowerCase()) ||
                    job.getLocation().toLowerCase().contains(query.toLowerCase())) {
                filteredJobs.add(job);
            }
        }
        return filteredJobs;
    }

    public List<JobModel> applyFilters(List<JobModel> fullJobList, String jobTitle, String minSalary, String maxSalary, String company, String location) {
        List<JobModel> filteredJobs = new ArrayList<>();
        for (JobModel job : fullJobList) {
            boolean matches = true;

            if (!jobTitle.isEmpty() && !job.getTitle().toLowerCase().contains(jobTitle.toLowerCase())) {
                matches = false;
            }

            if (!company.isEmpty() && !job.getCompany().toLowerCase().contains(company.toLowerCase())) {
                matches = false;
            }

            if (!location.isEmpty() && !job.getLocation().toLowerCase().contains(location.toLowerCase())) {
                matches = false;
            }

            if (!minSalary.isEmpty() || !maxSalary.isEmpty()) {
                try {
                    double jobSalary = Double.parseDouble(job.getSalaryText());
                    double min = minSalary.isEmpty() ? 0 : Double.parseDouble(minSalary);
                    double max = maxSalary.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxSalary);

                    if (jobSalary < min || jobSalary > max) {
                        matches = false;
                    }
                } catch (NumberFormatException e) {
                    throw new NumberFormatException();
                }
            }

            if (matches) {
                filteredJobs.add(job);
            }
        }
        return filteredJobs;
    }
}
