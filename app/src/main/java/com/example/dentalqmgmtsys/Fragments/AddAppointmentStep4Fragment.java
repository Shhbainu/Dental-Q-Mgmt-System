package com.example.dentalqmgmtsys.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddAppointmentStep4Fragment extends Fragment {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

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

        //Creating appointment information
        AppointmentInformation appointmentInformation = new AppointmentInformation();

        appointmentInformation.setDoctor(Common.currentDoctor);
        appointmentInformation.setService(Common.currentService.getName());
        appointmentInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot) +
                " at " +
                simpleDateFormat.format(Common.currentDate.getTime())).toString());
        appointmentInformation.setSlot((long) Common.currentTimeSlot);
        appointmentInformation.setPatientName(Common.currentUser);
        appointmentInformation.setPatientPhone(Common.currentPhone);

        //Submit Firestore
        DocumentReference appointmentDate = FirebaseFirestore.getInstance()
                .collection("AllDoctors")
                .document(Common.currentDoctor)
                .collection(Common.simpleFormatDate.format(Common.currentDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));

        //Write data
        appointmentDate.set(appointmentInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        resetStaticData();
                        getActivity().finish();//Close activity
                        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
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
        Common.currentDate.add(Calendar.DATE, 0); //Current Date
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
                .append(simpleDateFormat.format(Common.currentDate.getTime())));

    }

    static AddAppointmentStep4Fragment instance;

    public static AddAppointmentStep4Fragment getInstance(){
        if(instance == null)
            instance = new AddAppointmentStep4Fragment();
        return instance;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //format of date display
        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmAppointingReceiver, new IntentFilter(Common.KEY_CONFIRM_APPOINTMENT));
        
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
