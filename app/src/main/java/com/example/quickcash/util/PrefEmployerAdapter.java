package com.example.quickcash.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quickcash.R;
import com.example.quickcash.model.PreferEmployeeModel;
import java.util.List;

public class PrefEmployerAdapter extends RecyclerView.Adapter<PrefEmployerAdapter.PreferredViewHolder> {
    private List<PreferEmployeeModel> preferredList;

    public PrefEmployerAdapter(List<PreferEmployeeModel> preferredList) {
        this.preferredList = preferredList;
    }

    @NonNull
    @Override
    public PreferredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_preferred_employee, parent, false);
        return new PreferredViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferredViewHolder holder, int position) {
        PreferEmployeeModel employee = preferredList.get(position);
        holder.textEmployeeName.setText(employee.getEmployeeName());
        holder.textEmployeeEmail.setText(employee.getEmployeeEmail());
        holder.textAddedTime.setText(employee.getAddedTime());
    }

    @Override
    public int getItemCount() {
        return preferredList.size();
    }

    public static class PreferredViewHolder extends RecyclerView.ViewHolder {
        TextView textEmployeeName, textEmployeeEmail, textAddedTime;

        public PreferredViewHolder(@NonNull View itemView) {
            super(itemView);
            textEmployeeName = itemView.findViewById(R.id.textEmployeeName);
            textEmployeeEmail = itemView.findViewById(R.id.textEmployeeEmail);
            textAddedTime = itemView.findViewById(R.id.textAddedTime);
        }
    }
}
