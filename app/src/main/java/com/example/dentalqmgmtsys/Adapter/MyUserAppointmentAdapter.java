package com.example.dentalqmgmtsys.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dentalqmgmtsys.Models.UserAppointmentModel;
import com.example.dentalqmgmtsys.R;

import java.util.ArrayList;

public class MyUserAppointmentAdapter extends RecyclerView.Adapter<MyUserAppointmentAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserAppointmentModel> list;

    public MyUserAppointmentAdapter(Context context, ArrayList<UserAppointmentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.appointments_list,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserAppointmentModel user = list.get(position);
        holder.doctor.setText(user.getDoctor());
        holder.date.setText(user.getDate());
        holder.time.setText(user.getTime());
        holder.service.setText(user.getService());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView doctor, date, time, service;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            doctor = itemView.findViewById(R.id.doctorNameTV);
            date = itemView.findViewById(R.id.dateTV);
            time = itemView.findViewById(R.id.timeTV);
            service = itemView.findViewById(R.id.serviceTV);
        }
    }
}
