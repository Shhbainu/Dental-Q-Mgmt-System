package com.example.dentalqmgmtsys.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dentalqmgmtsys.Adapter.MyUserAppointmentAdapter;
import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.Models.UserAppointmentModel;
import com.example.dentalqmgmtsys.R;
import com.example.dentalqmgmtsys.databinding.FragmentAppointmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AppointmentFragment extends Fragment {

    private FragmentAppointmentBinding binding;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;

    RecyclerView recyclerView;
    ArrayList<UserAppointmentModel> list;
    DatabaseReference reference, removeRef;
    MyUserAppointmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        recyclerView = getView().findViewById(R.id.recyclerView);

        //String uid = firebaseAuth.getUid();
        //databaseReference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(uid).child("appointments");

        reference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users" + "/" + firebaseUser.getUid() + "/");


        binding.cancelAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //supposedly delete firebase realtimedatabase
                removeAppointmentFR();
                loadAppointment();
            }
        });

        //Init function
        cancelAppointmentAvailability();
        loadAppointment();
    }

    private void removeAppointmentFR() {
        removeRef = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users" + "/" + firebaseUser.getUid() + "/" + "appointments" + "/");

        removeRef.removeValue();

        Toast.makeText(getActivity(), "Your Appointment has been canceled", Toast.LENGTH_SHORT).show();
    }

    private void cancelAppointmentAvailability() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("appointments")){
                    //binding.bookButton.setVisibility(View.VISIBLE);
                    binding.cancelAppointmentBtn.setEnabled(true);
                }
                else
                {
                    binding.cancelAppointmentBtn.setEnabled(false);
                    //binding.bookButton.setVisibility(View.GONE);
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

        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyUserAppointmentAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);

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
                    Log.i("Firebase", "Reading from.."+ dataSnapshot.getValue());

                    list.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}