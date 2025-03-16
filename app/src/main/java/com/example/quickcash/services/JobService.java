// JobService.java
package com.example.quickcash.services;

import com.example.quickcash.model.JobModel;
import java.util.List;

public interface JobService {
    void createJob(JobModel job, JobCallback callback);
    void loadAllJobs(JobDataCallback callback);

    interface JobCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    interface JobDataCallback {
        void onJobsLoaded(List<JobModel> jobList);
        void onError(String error);
    }
}
