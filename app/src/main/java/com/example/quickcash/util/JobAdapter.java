package com.example.quickcash.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.model.JobModel;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    public List<JobModel> jobList;
    ButtonClickListener listener;

    public JobAdapter(List<JobModel> jobList) {
        this.jobList = jobList;
    }


    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recycler, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobModel job = jobList.get(position);
        holder.title.setText(job.getTitle());
        holder.company.setText(job.getCompany());
        holder.location.setText(job.getLocation());
        holder.salary.setText("$" + job.getSalaryText());
        holder.prefer.setText("Prefer this employer");
    }


    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public JobModel getItem(int position) {
        return this.jobList.get(position);
    }

    public void updateJobs(List<JobModel> jobs) {
        this.jobList = jobs;
        notifyDataSetChanged();
    }

    public class JobViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView company;
        public TextView location;
        public TextView salary;
        public Button prefer;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.jobTitle);
            company = itemView.findViewById(R.id.companyName);
            location = itemView.findViewById(R.id.jobLocation);
            salary = itemView.findViewById(R.id.salaryText);
            prefer = itemView.findViewById(R.id.preferredListButton);
            prefer.setOnClickListener(view -> listener.onItemClick(view, getAdapterPosition()));
        }
    }
    /*onClickListener*/
    public void setItemClickListener(ButtonClickListener listener){
        this.listener=listener;
    }

    public interface ButtonClickListener {
        public void onItemClick(View view, int position);
    }
}
