package com.example.quickcash.util;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quickcash.R;
import com.example.quickcash.model.JobModel;
import com.example.quickcash.ui.JobDetailsActivity;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private List<JobModel> jobList;
    public List<String> jobIds;
    private ButtonClickListener listener;
    private String currentUserEmail;

    public JobAdapter(List<JobModel> jobList, String currentUserEmail) {
        this.jobList = jobList;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobModel job = jobList.get(position);
        holder.title.setText(job.getTitle());
        holder.company.setText(job.getCompany());
        holder.location.setText(job.getLocation());
        holder.salary.setText("$" + job.getSalaryText());
        holder.type.setText(job.getType());

        // Set apply button state
        boolean hasApplied = job.getApplicants() != null &&
                job.getApplicants().contains(currentUserEmail);
        holder.applyButton.setEnabled(!hasApplied);
        holder.applyButton.setText(hasApplied ? "Applied" : "Apply");

        holder.descriptionButton.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDescriptionClick(view, position);
            }
            Intent intent = new Intent(view.getContext(), JobDetailsActivity.class);
            intent.putExtra("job", job);
            view.getContext().startActivity(intent);
        });

        holder.prefer.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(view, position);
            }
        });

        holder.applyButton.setOnClickListener(view -> {
            if (listener != null) {
                listener.onApplyClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public JobModel getItem(int position) {
        return jobList.get(position);
    }

    public String getJobId(int position) {
        return jobIds != null && position < jobIds.size() ? jobIds.get(position) : null;
    }

    public void updateJobs(List<JobModel> jobs, List<String> ids) {
        this.jobList = jobs;
        this.jobIds = ids;
        notifyDataSetChanged();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView title, company, location, salary, type;
        Button prefer, descriptionButton, applyButton;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.jobTitle);
            company = itemView.findViewById(R.id.companyName);
            location = itemView.findViewById(R.id.jobLocation);
            salary = itemView.findViewById(R.id.salaryText);
            type = itemView.findViewById(R.id.jobType);
            prefer = itemView.findViewById(R.id.preferredListButton);
            descriptionButton = itemView.findViewById(R.id.descriptionButton);
            applyButton = itemView.findViewById(R.id.applyButton);
        }
    }

    public void setItemClickListener(ButtonClickListener listener) {
        this.listener = listener;
    }

    public interface ButtonClickListener {
        void onItemClick(View view, int position);
        void onDescriptionClick(View view, int position);
        void onApplyClick(View view, int position);
    }
}

