package com.example.dqms_admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dqms_admin.Model.Queues;
import com.example.dqms_admin.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyQueuesAdapter extends RecyclerView.Adapter<MyQueuesAdapter.ViewHolder> {

    Context context;
    ArrayList<Queues> queuesArrayList;
    FirebaseFirestore firebaseFirestore;

    public MyQueuesAdapter(Context context, ArrayList<Queues> queuesArrayList) {
        this.context = context;
        this.queuesArrayList = queuesArrayList;
    }

    @NonNull
    @Override
    public MyQueuesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_queue_lists, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyQueuesAdapter.ViewHolder holder, int position) {

        Queues queues = queuesArrayList.get(position);

        holder.patientName.setText(queues.getPatientName());
        holder.patientSlot.setText(Integer.toString(queues.getSlot()));
        holder.patientTime.setText(queues.getTime());
        holder.patientService.setText(queues.getService());
        holder.patientDoctor.setText(queues.getDoctor());

    }

    @Override
    public int getItemCount() {
        return queuesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView patientName, patientSlot, patientTime, patientService, patientDoctor;
        Button tenButton, thirtyButton, fortyFiveButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.patientNameTV);
            patientSlot = itemView.findViewById(R.id.patientSlotTV);
            patientService = itemView.findViewById(R.id.patientServiceTV);
            patientTime = itemView.findViewById(R.id.patientTimeTV);
            patientDoctor= itemView.findViewById(R.id.queuedDoctorTV);

            tenButton = itemView.findViewById(R.id.tenButton);
            thirtyButton = itemView.findViewById(R.id.thirtyButton);
            fortyFiveButton = itemView.findViewById(R.id.fortyFiveButton);
        }
    }
}
