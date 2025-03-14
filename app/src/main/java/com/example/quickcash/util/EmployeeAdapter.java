package com.example.quickcash.util;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<UserModel> employeeList;
    private ButtonClickListener itemClickListener;

    public EmployeeAdapter(List<UserModel> employeeList) {
        this.employeeList = new ArrayList<>(employeeList); // Initialize with a copy of the list
    }

    public void setItemClickListener(ButtonClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        UserModel employee = employeeList.get(position);
        Log.d("EmployeeAdapter", "Binding employee: " + employee.getUsername() + ", " + employee.getEmail());
        holder.employeeName.setText(employee.getUsername());
        holder.employeeEmail.setText(employee.getEmail());

        holder.addButton.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public UserModel getItem(int position) {
        return employeeList.get(position);
    }

    public void updateEmployees(List<UserModel> newEmployeeList) {
        employeeList.clear();
        employeeList.addAll(newEmployeeList);
        Log.d("EmployeeAdapter", "Updating employees: " + employeeList.size());
        notifyDataSetChanged(); // Notify the adapter of data changes
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView employeeName, employeeEmail;
        Button addButton;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.employeeName);
            employeeEmail = itemView.findViewById(R.id.employeeEmail);
            addButton = itemView.findViewById(R.id.addButton);
        }
    }

    public interface ButtonClickListener {
        void onItemClick(View view, int position);
    }
}