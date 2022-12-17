package com.example.dqms_admin.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dqms_admin.Model.Queues;
import com.example.dqms_admin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyQueuesAdapter extends RecyclerView.Adapter<MyQueuesAdapter.ViewHolder> {

    Context context;
    ArrayList<Queues> queuesArrayList;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    DatabaseReference databaseReference;
    long newTimeStamp, appointTime, clockTime, timeStamp, slot;
    String name;
    String time;
    String date;
    String doctor;
    String underScoredDate;

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
        holder.patientSlot.setText(String.valueOf(queues.getSlot()));
        holder.patientTime.setText(queues.getTime());
        holder.patientService.setText(queues.getService());
        holder.patientDoctor.setText(queues.getDoctor());
        holder.inputTime.getText();

        time = queues.getTime();
        doctor = queues.getDoctor();
        slot = queues.getSlot();
        date = queues.getDate();
        underScoredDate = date.replace("/", "_");
        timeStamp = queues.getNewTimeStamp();

        holder.addTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTime(timeConversion(String.valueOf(holder.inputTime.getText())));
                Log.d("THIS IS THE INPUT", ""+ timeConversion(String.valueOf(holder.inputTime.getText())));
            }
        });

    }

    private long timeConversion(String time) {
        int answer = 0;
        String replacedTime = time.replace(":", "");
        if (replacedTime.length() == 6){
            int hours, minutes, seconds, hrsMilli, minMilli, secMilli;
            hours = Integer.parseInt(replacedTime.substring(0,2));
            minutes = Integer.parseInt(replacedTime.substring(2,4));
            seconds = Integer.parseInt(replacedTime.substring(4));

            hrsMilli = (((hours * 60) * 60) * 1000);
            minMilli = ((minutes * 60) * 1000);
            secMilli = (seconds * 1000);

            answer = hrsMilli + minMilli + secMilli;
        } else if (replacedTime.length() == 5){
            int hours, minutes, seconds, hrsMilli, minMilli, secMilli;
            hours = Integer.parseInt(replacedTime.substring(0,2));
            minutes = Integer.parseInt(replacedTime.substring(2,4));
            seconds = Integer.parseInt(replacedTime.substring(4));

            hrsMilli = (((hours * 60) * 60) * 1000);
            minMilli = ((minutes * 60) * 1000);
            secMilli = (seconds * 1000);

            answer = hrsMilli + minMilli + secMilli;
        } else if (replacedTime.length() == 4){
            int hours, minutes, hrsMilli, minMilli;
            hours = Integer.parseInt(replacedTime.substring(0,2));
            minutes = Integer.parseInt(replacedTime.substring(2));

            hrsMilli = (((hours * 60) * 60) * 1000);
            minMilli = ((minutes * 60) * 1000);

            answer = hrsMilli + minMilli;
        }else if(replacedTime.length() == 3){
            int hours, minutes, hrsMilli, minMilli;
            hours = Integer.parseInt(replacedTime.substring(0,1));
            minutes = Integer.parseInt(replacedTime.substring(1));

            hrsMilli = (((hours * 60) * 60) * 1000);
            minMilli = ((minutes * 60) * 1000);

            answer = hrsMilli + minMilli;
        }else if(replacedTime.length() <= 2){
            int timeInt = Integer.parseInt(replacedTime);
            answer = (timeInt * 60) * 1000;
        }

        return answer;
    }

    private void getTime(long time){
        Map<String, Object> newTimeStamp = new HashMap<>();
        newTimeStamp.put("newTimeStamp", time);
        firebaseFirestore.collection("AllDoctors")
                .document(doctor)
                .collection(underScoredDate)
                .document(String.valueOf(slot))
                .set(newTimeStamp, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SUCCESS", "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR", "Error writing document", e);
                    }
                });

    }
    @Override
    public int getItemCount() {
        return queuesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView patientName, patientSlot, patientTime, patientService, patientDoctor;
        EditText inputTime;
        Button addTimeButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.patientNameTV);
            patientSlot = itemView.findViewById(R.id.patientSlotTV);
            patientService = itemView.findViewById(R.id.patientServiceTV);
            patientTime = itemView.findViewById(R.id.patientTimeTV);
            patientDoctor= itemView.findViewById(R.id.queuedDoctorTV);
            inputTime = itemView.findViewById(R.id.inputTime);
            addTimeButton = itemView.findViewById(R.id.addTimeButton);


        }
    }
}
