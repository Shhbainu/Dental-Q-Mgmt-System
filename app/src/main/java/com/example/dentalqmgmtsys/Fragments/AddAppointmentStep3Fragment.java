package com.example.dentalqmgmtsys.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.Models.AppointmentInformation;
import com.example.dentalqmgmtsys.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class AddAppointmentStep3Fragment extends Fragment {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    AlertDialog dialog;
    long newTimeStamp;
    long appointTime;
    long clockTime;

    @BindView(R.id.confirmNameTV)
    TextView confirmNameTV;
    @BindView(R.id.confirmPhoneTV)
    TextView confirmPhoneTV;
    @BindView(R.id.confirmDoctorTV)
    TextView confirmDoctorTV;
    @BindView(R.id.confirmDateTimeTV)
    TextView confirmDateTimeTV;
    @BindView(R.id.confirmServiceTV)
    TextView confirmServiceTV;
    @BindView(R.id.finalConfirmButton)
    Button finalConfirmButton;
    @OnClick(R.id.finalConfirmButton)
    void confirmAppointment(){

        dialog.show();

        //Process Timestamp
        //Timestamp to filer all appointments if date is greater today
        //To only display all future appointments
/*        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
        String [] startTimeConvert = startTime.split(":");
        int startHourInt = Integer.parseInt(startTimeConvert[0].trim()); // We will get 8
        int startMinInt = Integer.parseInt(startTimeConvert[1].trim()); // We will get 00

        Calendar appointmentDateWithHour = Calendar.getInstance();
        appointmentDateWithHour.setTimeInMillis(Common.appointmentDate.getTimeInMillis());
        appointmentDateWithHour.set(Calendar.HOUR_OF_DAY, startHourInt);
        appointmentDateWithHour.set(Calendar.MINUTE, startMinInt);


        Timestamp timestamp = new Timestamp(appointmentDateWithHour.getTime());*/

        //Creating appointment information
        AppointmentInformation appointmentInformation = new AppointmentInformation();


        /*appointmentInformation.setDone(false);*/ //False to filter for display in user*/
        appointmentInformation.setDoctor(Common.currentDoctor);
        appointmentInformation.setService(Common.currentService.getName());
        appointmentInformation.setTime(Common.convertTimeSlotToString(Common.currentTimeSlot));
        appointmentInformation.setDate(simpleDateFormat.format(Common.appointmentDate.getTime()).toString());
        appointmentInformation.setSlot((long) Common.currentTimeSlot);
        appointmentInformation.setPatientName(Common.currentUser);
        appointmentInformation.setPatientPhone(Common.currentPhone);
        appointmentInformation.setNewTimeStamp(0L);

        //Submit Firestore
        DocumentReference appointmentDate = FirebaseFirestore.getInstance()
                .collection("AllDoctors")
                .document(Common.currentDoctor)
                .collection(Common.simpleFormatDate.format(Common.appointmentDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));

        //Write data
        appointmentDate.set(appointmentInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(dialog.isShowing())
                            dialog.dismiss();
                        saveToRTD();
                        resetStaticData();
                        getActivity().finish();
                        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();

/*                        //Write function to check
                        //If already exist an appointment, will prevent new appointment
                        addToUserAppointment(appointmentInformation);*/
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

   /* private void addToUserAppointment(AppointmentInformation appointmentInformation) {
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference userAppointment = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users").child(firebaseAuth.getUid()).child("ifAppointmentExist");

        userAppointment.orderByValue().equalTo(false, "done") // If have any document wih field done = false
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.getResult().exists()){
                            if(dialog.isShowing())
                                dialog.dismiss();
                            saveToRTD();
                            resetStaticData();
                            getActivity().finish();
                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Set data
                            userAppointment
                                    .setValue(appointmentInformation)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            if(dialog.isShowing())
                                                dialog.dismiss();
                                            saveToRTD();
                                            resetStaticData();
                                            getActivity().finish();
                                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if(dialog.isShowing())
                                                dialog.dismiss();
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

    }*/
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
    private void saveToRTD() {
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();
        AppointmentInformation appointmentInformation = new AppointmentInformation();
        Common.appointmentID = FirebaseDatabase.getInstance().getReference().push().getKey();
        appointmentInformation.setDoctor(Common.currentDoctor);
        appointmentInformation.setService(Common.currentService.getName());
        appointmentInformation.setTime(Common.convertTimeSlotToString(Common.currentTimeSlot));
        appointmentInformation.setDate(simpleDateFormat.format(Common.appointmentDate.getTime()).toString());
        appointmentInformation.setSlot((long) Common.currentTimeSlot);
        appointmentInformation.setPatientName(Common.currentUser);
        appointmentInformation.setPatientPhone(Common.currentPhone);
        appointmentInformation.setNewTimeStamp(0L);

        databaseReference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");
        databaseReference.child(uid).child("appointments").child(Common.appointmentID)
                .setValue(appointmentInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }


    private void resetStaticData() {
        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.currentService = null;
        Common.currentDoctor = null;
        Common.appointmentDate.add(Calendar.DATE, 0); //Current Date
    }

    Unbinder unbinder;


    BroadcastReceiver confirmAppointingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {
        confirmNameTV.setText(Common.currentUser);
        confirmPhoneTV.setText(Common.currentPhone);
        confirmDoctorTV.setText(Common.currentDoctor);
        confirmServiceTV.setText(Common.currentService.getName());
        confirmDateTimeTV.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" at ")
                .append(simpleDateFormat.format(Common.appointmentDate.getTime())));

    }

    static AddAppointmentStep3Fragment instance;

    public static AddAppointmentStep3Fragment getInstance(){
        if(instance == null)
            instance = new AddAppointmentStep3Fragment();
        return instance;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //format of date display
        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmAppointingReceiver, new IntentFilter(Common.KEY_CONFIRM_APPOINTMENT));

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmAppointingReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_add_appointment_step4,container, false);
        unbinder = ButterKnife.bind(this, itemView);

        return itemView;

    }
}
