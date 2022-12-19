package com.example.dentalqmgmtsys.Fragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.QueueFinishedDialog;
import com.example.dentalqmgmtsys.QuizTitleActivity;
import com.example.dentalqmgmtsys.R;
import com.example.dentalqmgmtsys.ReferralActivity;
import com.example.dentalqmgmtsys.databinding.FragmentQueueBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QueueFragment extends Fragment {
    //Database
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users" + "/" + uid + "/" + "appointments" + "/" + Common.appointmentID);
    DocumentReference timeStampRef;
    //Notifications
    NotificationManager notificationManager;
    NotificationChannel notificationChannel;
    Notification.Builder builder;
    String channelID = "1234";
    String description = "Test Notification";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    //Viewbinding
    private FragmentQueueBinding binding;
    //Variables
    String userTime, TAG = "QueueFragment", doctor, patientName, date, underScoredDate, patientPhone, service;
    long newTimeStamp, appointTime, clockTime, slot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println(Common.appointmentID);
        //time
        Date clock = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String parsedTime = formatter.format(clock);
        //date
        Date localDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String parsedDate = dateFormat.format(localDate);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userTime = snapshot.child("time").getValue().toString();

                ///AllDoctors/doctor/date/slot
                date = snapshot.child("date").getValue().toString();
                doctor = snapshot.child("doctor").getValue().toString();
                patientName = snapshot.child("patientName").getValue().toString();
                patientPhone = snapshot.child("patientPhone").getValue().toString();
                service = snapshot.child("service").getValue().toString();
                slot = Long.parseLong(snapshot.child("slot").getValue().toString());


                underScoredDate = date.replace("/", "_");

                appointTime = timeConversion(userTime);
                Log.i(TAG, "AppointTime: "  + appointTime);
                clockTime = timeConversion(parsedTime);
                Log.i(TAG, "ClockTime: "  + clockTime);

                Log.i(TAG, "Date: " + dateConversion(date));
                Log.i(TAG, "LocalDate: " + dateConversion(parsedDate));

                Log.i(TAG, "Answer: " + (dateConversion(date) - dateConversion(parsedDate)));

                long followingDay = (dateConversion(date) - dateConversion(parsedDate)) * 86400000L;

                Log.i(TAG, "Following Day: " + followingDay);

                timeStampRef = firebaseFirestore.collection("AllDoctors")
                        .document(doctor)
                        .collection(underScoredDate)
                        .document(String.valueOf(slot));

                timeStampRef.get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists()){
                                    newTimeStamp = Long.parseLong(documentSnapshot.get("newTimeStamp").toString());
                                    Log.i(TAG, "onComplete: " + newTimeStamp);

                                    if(clockTime >= appointTime){
                                        Log.i(TAG, "onDataChange: Something");
                                        if(newTimeStamp != 0){
                                            createNotificationTimeAdded();
                                            long ans = ((appointTime + followingDay) + newTimeStamp) - clockTime;
                                            ans = Math.abs(ans);

                                            //Into Firestore
                                            Map<String, Object> answerTimeStamp = new HashMap<>();
                                            answerTimeStamp.put("date", date);
                                            answerTimeStamp.put("doctor", doctor);
                                            answerTimeStamp.put("patientName", patientName);
                                            answerTimeStamp.put("patientPhone", patientPhone);
                                            answerTimeStamp.put("service", service);
                                            answerTimeStamp.put("slot", slot);
                                            answerTimeStamp.put("time", userTime);
                                            answerTimeStamp.put("timeStamp", ans);
                                            answerTimeStamp.put("newTimeStamp", newTimeStamp);
                                            firebaseFirestore.collection("AllDoctors").document(doctor).collection(underScoredDate).document(String.valueOf(slot))
                                                    .set(answerTimeStamp);

                                            updateCountdown(ans);
                                            Log.i(TAG, "different day " + ans);
                                        }else{
                                            long ans = (appointTime + followingDay) - clockTime;
                                            ans = Math.abs(ans);

                                            //Into Firestore
                                            Map<String, Object> answerTimeStamp = new HashMap<>();
                                            answerTimeStamp.put("date", date);
                                            answerTimeStamp.put("doctor", doctor);
                                            answerTimeStamp.put("patientName", patientName);
                                            answerTimeStamp.put("patientPhone", patientPhone);
                                            answerTimeStamp.put("service", service);
                                            answerTimeStamp.put("slot", slot);
                                            answerTimeStamp.put("time", userTime);
                                            answerTimeStamp.put("timeStamp", ans);
                                            answerTimeStamp.put("newTimeStamp", newTimeStamp);
                                            firebaseFirestore.collection("AllDoctors").document(doctor).collection(underScoredDate).document(String.valueOf(slot))
                                                    .set(answerTimeStamp);

                                            updateCountdown(ans);
                                             Log.i(TAG, "different day but no change " + ans);
                                        }
                                    }else{
                                        if(newTimeStamp != 0){
                                            createNotificationTimeAdded();
                                            long ans = (appointTime + newTimeStamp) - clockTime;
                                            ans = Math.abs(ans);

                                            //Into Firestore
                                            Map<String, Object> answerTimeStamp = new HashMap<>();
                                            answerTimeStamp.put("date", date);
                                            answerTimeStamp.put("doctor", doctor);
                                            answerTimeStamp.put("patientName", patientName);
                                            answerTimeStamp.put("patientPhone", patientPhone);
                                            answerTimeStamp.put("service", service);
                                            answerTimeStamp.put("slot", slot);
                                            answerTimeStamp.put("time", userTime);
                                            answerTimeStamp.put("timeStamp", ans);
                                            answerTimeStamp.put("newTimeStamp", newTimeStamp);
                                            firebaseFirestore.collection("AllDoctors").document(doctor).collection(underScoredDate).document(String.valueOf(slot))
                                                    .set(answerTimeStamp);

                                            updateCountdown(ans);
                                            Log.i(TAG, "Same day " + ans);
                                        }else {
                                            long ans = appointTime - clockTime;

                                            //Into Firestore
                                            Map<String, Object> answerTimeStamp = new HashMap<>();
                                            answerTimeStamp.put("date", date);
                                            answerTimeStamp.put("doctor", doctor);
                                            answerTimeStamp.put("patientName", patientName);
                                            answerTimeStamp.put("patientPhone", patientPhone);
                                            answerTimeStamp.put("service", service);
                                            answerTimeStamp.put("slot", slot);
                                            answerTimeStamp.put("time", userTime);
                                            answerTimeStamp.put("timeStamp", ans);
                                            answerTimeStamp.put("newTimeStamp", newTimeStamp);
                                            firebaseFirestore.collection("AllDoctors").document(doctor).collection(underScoredDate).document(String.valueOf(slot))
                                                    .set(answerTimeStamp);

                                            updateCountdown(ans);
                                            Log.i(TAG, "same day but no change: " + ans);
                                        }
                                    }
                                }
                            }
                        });





//                if(clockTime >= appointTime){
//                    if(newTimeStamp != 0){
//                        long ans = ((appointTime + 86400000) + newTimeStamp) - clockTime;
//                        ans = Math.abs(ans);
//
//                        //Into Firestore
//                        Map<String, Object> answerTimeStamp = new HashMap<>();
//                        answerTimeStamp.put("date", date);
//                        answerTimeStamp.put("doctor", doctor);
//                        answerTimeStamp.put("patientName", patientName);
//                        answerTimeStamp.put("patientPhone", patientPhone);
//                        answerTimeStamp.put("service", service);
//                        answerTimeStamp.put("slot", slot);
//                        answerTimeStamp.put("time", userTime);
//                        answerTimeStamp.put("timeStamp", ans);
//                        answerTimeStamp.put("newTimeStamp", newTimeStamp);
//                        firebaseFirestore.collection("AllDoctors").document(doctor).collection(underScoredDate).document(String.valueOf(slot))
//                                .set(answerTimeStamp);
//
//                        //Into RTD
//                        databaseReference.child("timeStamp").setValue(ans);
//                        databaseReference.child("newTimeStamp").setValue(newTimeStamp);
//
//                        if ((ans <= 1801000) && (ans >= 1800000)) Log.i(TAG, "30minsleft: Activate");
//                        updateCountdown(ans);
//                        Log.i(TAG, "newTimeStamp " + ans);
//                        //another if for im in button
//                    }else {
//                        long ans = (appointTime + 86400000) - clockTime;
//
//                        //Into Firestore
//                        Map<String, Object> answerTimeStamp = new HashMap<>();
//                        answerTimeStamp.put("date", date);
//                        answerTimeStamp.put("doctor", doctor);
//                        answerTimeStamp.put("patientName", patientName);
//                        answerTimeStamp.put("patientPhone", patientPhone);
//                        answerTimeStamp.put("service", service);
//                        answerTimeStamp.put("slot", slot);
//                        answerTimeStamp.put("time", userTime);
//                        answerTimeStamp.put("timeStamp", ans);
//                        answerTimeStamp.put("newTimeStamp", newTimeStamp);
//                        firebaseFirestore.collection("AllDoctors").document(doctor).collection(underScoredDate).document(String.valueOf(slot))
//                                .set(answerTimeStamp);
//
//                        databaseReference.child("timeStamp").setValue(ans);
//                        updateCountdown(ans);
//                        Log.i(TAG, "plus24: " + ans);
//                    }
//                }else{
//                    if(newTimeStamp != 0){
//                        long ans = (appointTime + newTimeStamp) - clockTime;
//                        ans = Math.abs(ans);
//
//                        //Into Firestore
//                        Map<String, Object> answerTimeStamp = new HashMap<>();
//                        answerTimeStamp.put("date", date);
//                        answerTimeStamp.put("doctor", doctor);
//                        answerTimeStamp.put("patientName", patientName);
//                        answerTimeStamp.put("patientPhone", patientPhone);
//                        answerTimeStamp.put("service", service);
//                        answerTimeStamp.put("slot", slot);
//                        answerTimeStamp.put("time", userTime);
//                        answerTimeStamp.put("timeStamp", ans);
//                        answerTimeStamp.put("newTimeStamp", newTimeStamp);
//                        firebaseFirestore.collection("AllDoctors").document(doctor).collection(underScoredDate).document(String.valueOf(slot))
//                                .set(answerTimeStamp);
//
//                        databaseReference.child("timeStamp").setValue(ans);
//                        databaseReference.child("newTimeStamp").setValue(newTimeStamp);
//                        updateCountdown(ans);
//                        Log.i(TAG, "newTimeStamp " + ans);
//                    }else {
//                        long ans = appointTime - clockTime;
//
//                        //Into Firestore
//                        Map<String, Object> answerTimeStamp = new HashMap<>();
//                        answerTimeStamp.put("date", date);
//                        answerTimeStamp.put("doctor", doctor);
//                        answerTimeStamp.put("patientName", patientName);
//                        answerTimeStamp.put("patientPhone", patientPhone);
//                        answerTimeStamp.put("service", service);
//                        answerTimeStamp.put("slot", slot);
//                        answerTimeStamp.put("time", userTime);
//                        answerTimeStamp.put("timeStamp", ans);
//                        answerTimeStamp.put("newTimeStamp", newTimeStamp);
//                        firebaseFirestore.collection("AllDoctors").document(doctor).collection(underScoredDate).document(String.valueOf(slot))
//                                .set(answerTimeStamp);
//
//                        databaseReference.child("timeStamp").setValue(ans);
//                        updateCountdown(ans);
//                        Log.i(TAG, "same: " + ans);
//                    }
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }


        });

        // Inflate the layout for this fragment
        binding = FragmentQueueBinding.inflate(inflater, container, false);

        binding.quizGameBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), QuizTitleActivity.class);
            startActivity(intent);
        });

//        binding.imInBtn.setOnClickListener(view -> {
//            Intent intent = new Intent(getActivity(), QuizTitleActivity.class);
//            startActivity(intent);
//        });
        return binding.getRoot();
    }

    private void openQueueDialog() {
        QueueFinishedDialog queueFinishedDialog = new QueueFinishedDialog();
        queueFinishedDialog.show(getParentFragmentManager(), "Queue Info Dialog");
    }

    private int dateConversion(String date){
        String replacedDate = date.replace("/", "");
        return Integer.parseInt(replacedDate.substring(2,4));
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

    private void updateCountdown(long timer){
        Long minutes = ((timer / (1000 * 60)) % 60);
        Long hours = ((timer / (1000 * 60 * 60)) % 24);
        Long days = ((timer / (1000 * 60 * 60 * 24)) % 7);
        Long weeks = (timer / (1000 * 60 * 60 * 24 * 7));

        if(timer >= 604800000){
            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d Weeks:%02d Days:%02d Hours:%02d mins remaining", weeks, days, hours, minutes);
            binding.queueTimeTV.setText(timeLeftFormatted);
        }else if(timer >= 86400000){
            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d Days:%02d Hours:%02d mins remaining", days, hours, minutes);
            binding.queueTimeTV.setText(timeLeftFormatted);
        }else{
            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d Hours:%02d mins remaining", hours, minutes);
            binding.queueTimeTV.setText(timeLeftFormatted);
        }
    }

    private void createNotificationTimeAdded(){
//        Fragment newFragment = new Fragment();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_layout, newFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
        notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationChannel = new NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);

            builder = new Notification.Builder(getActivity(), channelID)
                    .setContentTitle("Attention")
                    .setContentText("Your current time has been changed")
                    .setSmallIcon(R.drawable.toothlogo)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.toothlogo))
                    .setAutoCancel(true);

        }else{
            builder = new Notification.Builder(getActivity())
                    .setContentTitle("Attention")
                    .setContentText("Your current time has been changed")
                    .setSmallIcon(R.drawable.toothlogo)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.toothlogo))
                    .setAutoCancel(true);
        }
        notificationManager.notify(1234, builder.build());
    }

    private void createNotification30(){
        notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationChannel = new NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);

            builder = new Notification.Builder(getActivity(), channelID)
                    .setContentTitle("Attention")
                    .setContentText("30 minutes remaining!")
                    .setSmallIcon(R.drawable.toothlogo)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.toothlogo))
                    .setAutoCancel(true);

        }else{
            builder = new Notification.Builder(getActivity())
                    .setContentTitle("Attention")
                    .setContentText("30 minutes remaining!")
                    .setSmallIcon(R.drawable.toothlogo)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.toothlogo))
                    .setAutoCancel(true);
        }
        notificationManager.notify(1234, builder.build());
    }

    private void createNotification5(){
        notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationChannel = new NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);

            builder = new Notification.Builder(getActivity(), channelID)
                    .setContentTitle("Attention")
                    .setContentText("5 minutes remaining!")
                    .setSmallIcon(R.drawable.toothlogo)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.toothlogo))
                    .setAutoCancel(true);

        }else{
            builder = new Notification.Builder(getActivity())
                    .setContentTitle("Attention")
                    .setContentText("5 minutes remaining!")
                    .setSmallIcon(R.drawable.toothlogo)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.toothlogo))
                    .setAutoCancel(true);
        }
        notificationManager.notify(1234, builder.build());
    }

    private void createNotificationDone() {

        Intent intent = new Intent(getActivity(), QueueFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = (NotificationManager) requireActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationChannel = new NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);

            builder = new Notification.Builder(getActivity(), channelID)
                    .setContentTitle("Attention")
                    .setContentText("It is your turn")
                    .setSmallIcon(R.drawable.toothlogo)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.toothlogo))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

        }else{
            builder = new Notification.Builder(getActivity())
                    .setContentTitle("Attention")
                    .setContentText("It is your turn")
                    .setSmallIcon(R.drawable.toothlogo)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.toothlogo))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
        }
        notificationManager.notify(1234, builder.build());
    }
}