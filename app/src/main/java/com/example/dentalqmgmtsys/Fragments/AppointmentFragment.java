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

import com.example.dentalqmgmtsys.Adapter.MyUserAppointmentAdapter;
import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.Models.UserAppointmentModel;
import com.example.dentalqmgmtsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AppointmentFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    RecyclerView recyclerView;
    ArrayList<UserAppointmentModel> list;
    DatabaseReference databaseReference;
    MyUserAppointmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();

//        databaseReference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                .getReference("Users").child(uid).child("appointments");


        recyclerView = getView().findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users" + "/" + firebaseUser.getUid() + "/" + "appointments" + "/");

        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyUserAppointmentAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
//                    String doctor = dataSnapshot.child("doctor").getValue().toString();
//                    String date = dataSnapshot.child("date").getValue().toString();
//                    String time = dataSnapshot.child("time").getValue().toString();
//                    String service = dataSnapshot.child("service").getValue().toString();
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