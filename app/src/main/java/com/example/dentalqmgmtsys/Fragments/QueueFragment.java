package com.example.dentalqmgmtsys.Fragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.MainActivity;
import com.example.dentalqmgmtsys.R;
import com.example.dentalqmgmtsys.databinding.FragmentQueueBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class QueueFragment extends Fragment {
    //Database
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getUid();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(uid).child("appointments").child(Common.simpleFormatDate.format(Common.appointmentDate.getTime()));
    //Notifications
    NotificationManager notificationManager;
    NotificationChannel notificationChannel;
    Notification.Builder builder;
    String channelID = "1234";
    String description = "Test Notification";
    //Viewbinding
    private FragmentQueueBinding binding;
    //Variables                                         //If there's an error when switching fragments(in app) and the timer resets just close the app
     Long comTime;
     Long remainTime;
     Long timeLeft;
     Long endTime;
     String timeSlot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQueueBinding.inflate(inflater, container, false);
        binding.quizGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new QuizTitleFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTime();

    }

    private void getTime(){
        //LocalTime to be read as 24hr format  //("HH:mm");
        Date clock = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String parsedTime = formatter.format(clock);
        //Database Calling
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    timeSlot = dataSnapshot.child("time").getValue().toString();
                    System.out.println(timeSlot);
                }
                //Initialization of Time and Computation
                //appointment
                int appointTime = stringToInt(timeSlot); //Time on Database
                //Clock
                int clockTime = stringToInt(parsedTime); //parsedTime
                Log.i("QueueFragment", "ClockTime: " + parsedTime);
                Log.i("QueueFragment", "AppointmentTime: " + timeSlot);
                //Computation of Time
                comTime = timeConversion(String.valueOf(appointTime)) - timeConversion(String.valueOf(clockTime));
                remainTime = comTime;
                System.out.println(comTime);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Not yet");
            }
        });
    }

    private Long timeConversion(String time) {
        int answer = 0;
        if (time.length() == 4){
            int hours, minutes, hrsMilli, minMilli;
            hours = Integer.parseInt(time.substring(0,2));
            minutes = Integer.parseInt(time.substring(2));

            hrsMilli = (((hours * 60) * 60) * 1000);
            minMilli = ((minutes * 60) * 1000);

            answer = hrsMilli + minMilli;
        }else if(time.length() == 3){
            int hours, minutes, hrsMilli, minMilli;
            hours = Integer.parseInt(time.substring(0,1));
            minutes = Integer.parseInt(time.substring(1));

            hrsMilli = (((hours * 60) * 60) * 1000);
            minMilli = ((minutes * 60) * 1000);

            answer = hrsMilli + minMilli;
        }else if(time.length() <= 2){
            int timeInt = Integer.parseInt(time);
            answer = (timeInt * 60) * 1000;
        }

        return (long) answer;
    }

    private int stringToInt(String time){
        String conTime = time.replace(":", "");
        return Integer.parseInt(conTime);
    }

    private void startTimer(){                           //Concept: Activating the timer within 24hrs
        CountDownTimer countDownTimer = new CountDownTimer(remainTime, 1000) {

            @Override
            public void onTick(long untilFinish) {
                remainTime = untilFinish;
                timeLeft = untilFinish;
                updateCountdown();

                //30mins
                if ((untilFinish <= 1801000) && (untilFinish >= 1800000)) createNotification30();
                //5mins
                if ((untilFinish <= 301000) && (untilFinish >= 300000)) createNotification5();
            }

            @Override
            public void onFinish() {
                binding.queueTimeTV.setText("00:00:00");
                createNotificationDone();
                binding.imInBtn.setVisibility(View.VISIBLE);
                showButton();
                binding.imInBtn.setVisibility(View.VISIBLE);
                showButton();
                Toast.makeText(getActivity(), "Finished", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void showButton() {

    }

    private void updateCountdown() {
        Long seconds = (remainTime / 1000) % 60;
        Long minutes = ((remainTime / (1000 * 60)) % 60);
        Long hours = ((remainTime / (1000 * 60 * 60)) % 24);
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds);
        binding.queueTimeTV.setText(timeLeftFormatted);
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

            //Not working
//           getParentFragmentManager().beginTransaction().replace(R.id.frame_layout, new QueueFragment()).commit();
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

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isAdded()) {
                    SharedPreferences prefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("milliLeft", timeLeft);
                    editor.putLong("endTime", System.currentTimeMillis());
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Not yet");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isAdded()) {
                    SharedPreferences prefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    timeLeft = prefs.getLong("milliLeft", comTime);
                    Log.e("E", "Error" + comTime);
                    endTime = prefs.getLong("endTime", 0);
                    if (endTime == 0L) {
                        remainTime = timeLeft;
                    } else {
                        Long timeDiff = endTime - System.currentTimeMillis();
                        timeDiff = Math.abs(timeDiff);
                        Long timeDiffInSeconds = timeDiff / 1000 % 60;
                        Long timeDiffInMilli = timeDiffInSeconds * 1000;
                        remainTime = timeLeft - timeDiffInMilli;
                        Long timeDiffInMilliPlusTimerRemaining = remainTime;
                        if (timeDiffInMilliPlusTimerRemaining < 0) {
                            timeDiffInMilliPlusTimerRemaining = Math.abs(timeDiffInMilliPlusTimerRemaining);
                            remainTime = comTime - timeDiffInMilliPlusTimerRemaining;
                        }
                    }
                    updateCountdown();
                    startTimer();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}