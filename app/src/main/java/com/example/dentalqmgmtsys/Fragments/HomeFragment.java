package com.example.dentalqmgmtsys.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dentalqmgmtsys.Adapter.MyUserAppointmentAdapter;
import com.example.dentalqmgmtsys.AddAppointmentActivity;
import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.Models.UserAppointmentModel;
import com.example.dentalqmgmtsys.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    //ViewBinding
    private FragmentHomeBinding binding;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;

    DatabaseReference ref, reference;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Init
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        reference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users" + "/" + firebaseUser.getUid() + "/");

        //Book Appointment Button listener
        //Opens AddAppointmentActivity
        binding.bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddAppointmentActivity.class));
            }
        });

        bookAppointmentAvailability();
        loadAppointment();


    }

    private void bookAppointmentAvailability() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("appointments")){
                    //binding.bookButton.setVisibility(View.GONE);
                    binding.bookButton.setEnabled(false);
                }
                else
                {
                    binding.bookButton.setEnabled(true);
                    //binding.bookButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAppointment() {
        reference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users" + "/" + firebaseUser.getUid() + "/" + "appointments" + "/");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    //String doctor = dataSnapshot.child("doctor").getValue().toString();
                    //String date = dataSnapshot.child("date").getValue().toString();
                    //String time = dataSnapshot.child("time").getValue().toString();
                    //String service = dataSnapshot.child("service").getValue().toString();
                    //Log.i("Firebase", "Reading from.."+ dataSnapshot.getKey()+", value="+dataSnapshot.getValue()+"UserAppointmentModel "+dataSnapshot.getValue(UserAppointmentModel.class));
                    //Log.i("Firebase", "Test Read"+doctor+" "+date+" "+time+" "+service+"");
                    UserAppointmentModel user = dataSnapshot.getValue(UserAppointmentModel.class);
                    Log.i("Firebase", "Reading from.."+ dataSnapshot.child("time").getValue());
                    Log.i("Firebase", "Reading from.."+ dataSnapshot.child("date").getValue());

                    Common.timeNotDone = (String) dataSnapshot.child("time").getValue();
                    Common.appointmentID = (String) dataSnapshot.getKey();
                    Common.dateNotDone = (String) dataSnapshot.child("date").getValue();
                    Common.doctorNotDone = (String) dataSnapshot.child("doctor").getValue();
                    Common.slotNotDone = (String) dataSnapshot.child("slot").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}