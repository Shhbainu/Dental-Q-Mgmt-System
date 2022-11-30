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

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dentalqmgmtsys.R;
import com.example.dentalqmgmtsys.databinding.FragmentQueueBinding;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class QueueFragment extends Fragment {
    //Notifications
    NotificationManager notificationManager;
    NotificationChannel notificationChannel;
    Notification.Builder builder;
    String channelID = "1234";
    String description = "Test Notification";
    //Viewbinding
    private FragmentQueueBinding binding;               //Haven't added Notifs - done
    //Variables                                         //Next connecting database and finishing adding of time
    private Long comTime;
    private Long remainTime;                            //There's a bug when pressing recent apps, the timer bugs out
    private Long timeLeft;                              //If encountered this bug, just close the app
    private Long endTime;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //LocalTime to be read as 24hr format
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String text = time.format(formatter);
        LocalTime parsedTime = LocalTime.parse(text);
        //Initialization of Time and Computation
        //appointment
        int endTime = stringToInt("10:48");
        //Clock
        int startTime = stringToInt("10:47");
        //Computation of Time
        String subAns = String.valueOf(endTime - startTime);
        comTime = timeConversion(subAns);
        remainTime = comTime;

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
        SharedPreferences prefs = this.requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("milliLeft", timeLeft);
        editor.putLong("endTime", System.currentTimeMillis());
        editor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = this.requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        timeLeft = prefs.getLong("milliLeft", comTime);
        endTime = prefs.getLong("endTime", 0);
        if(endTime == 0L){
            remainTime = timeLeft;
        }else{
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