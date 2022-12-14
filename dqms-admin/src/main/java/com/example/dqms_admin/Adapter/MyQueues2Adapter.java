package com.example.dqms_admin.Adapter;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dqms_admin.Model.Queues;
import com.example.dqms_admin.QueuesActivity;
import com.example.dqms_admin.R;
import com.example.dqms_admin.ServicesActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MyQueues2Adapter extends RecyclerView.Adapter<MyQueues2Adapter.MyViewHolder> {

   List<Queues> queuesList;
   List<CardView> cardViewList;
   QueuesActivity activity;
   FirebaseFirestore firebaseFirestore;

   String date, time, doctor, underScoredDate, service, name;
   long timeStamp, slot;

    public MyQueues2Adapter(List<Queues> queuesList, QueuesActivity activity) {
        this.queuesList = queuesList;
        cardViewList = new ArrayList<>();
        this.activity = activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity)
                .inflate(R.layout.layout_queues, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //saving data globally
        name = queuesList.get(position).getPatientName();
        slot = queuesList.get(position).getSlot();
        time = queuesList.get(position).getTime();
        service = queuesList.get(position).getService();
        doctor = queuesList.get(position).getDoctor();
        date = queuesList.get(position).getDate();
        underScoredDate = date.replace("/", "_");
        timeStamp = queuesList.get(position).getTimeStamp();


        //setting data into holder
        holder.patientName.setText(name);
        holder.patientSlot.setText(String.valueOf(slot));
        holder.patientTime.setText(time);
        holder.patientService.setText(service);
        holder.queuedDoctor.setText(doctor);

        if(!cardViewList.contains(holder.queuesCard))
            cardViewList.add(holder.queuesCard);

        //Inserting input time in the database
        holder.addTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTime(timeConversion(String.valueOf(holder.inputTime.getText())));
                Log.d("THIS IS THE INPUT", ""+ timeConversion(String.valueOf(holder.inputTime.getText())));

                Toast.makeText(activity, ""+(holder.inputTime.getText()) +" Minutes is added ", Toast.LENGTH_SHORT).show();
                holder.inputTime.getText().clear();
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
        return queuesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView patientName, patientSlot, patientTime, patientService, queuedDoctor;
        CardView queuesCard;
        EditText inputTime;
        Button addTimeBtn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            patientName = (TextView) itemView.findViewById(R.id.patientName2TV);
            patientSlot = (TextView) itemView.findViewById(R.id.patientSlot2TV);
            patientTime = (TextView) itemView.findViewById(R.id.patientTime2TV);
            patientService = (TextView) itemView.findViewById(R.id.patientService2TV) ;
            queuedDoctor = (TextView) itemView.findViewById(R.id.queuedDoctor2TV);

            queuesCard = (CardView) itemView.findViewById(R.id.queuesCard);

            inputTime = (EditText) itemView.findViewById(R.id.inputTime2);
            addTimeBtn = (Button) itemView.findViewById(R.id.addTimeButton2);

        }
    }
}
