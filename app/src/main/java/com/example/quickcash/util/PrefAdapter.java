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
import com.example.quickcash.model.PreferEmployerModel;

import java.util.List;

public class PrefAdapter extends RecyclerView.Adapter<PrefAdapter.JobViewHolder> {
    public List<PreferEmployerModel> jobList;
    ButtonClickListener listener;

    public PrefAdapter(List<PreferEmployerModel> jobList) {
        this.jobList = jobList;
    }


    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recycler_for_pref, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        PreferEmployerModel job = jobList.get(position);
        holder.employerEmail.setText(job.getEmployerEmail());
        holder.company.setText(job.getCompany());
        holder.time.setText(job.getAddedTime());
        holder.prefer.setText("Unprefer");
    }


    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public PreferEmployerModel getItem(int position) {
        return this.jobList.get(position);
    }

    public void updateJobs(List<PreferEmployerModel> jobs) {
        this.jobList = jobs;
        notifyDataSetChanged();
    }

    public class JobViewHolder extends RecyclerView.ViewHolder {
        public TextView employerEmail;
        public TextView company;
        public TextView time;
        public Button prefer;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            employerEmail = itemView.findViewById(R.id.employerEmail);
            company = itemView.findViewById(R.id.companyName);
            time = itemView.findViewById(R.id.time);
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
