package com.example.dqms_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.dqms_admin.Adapter.MyServicesOfferedAdapter;
import com.example.dqms_admin.Model.ServicesOffered;
import com.example.dqms_admin.databinding.ActivityDisplayServiceBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class DisplayServiceActivity extends AppCompatActivity {

    // Viewbinding
    //private ActivityDisplayServiceBinding binding;

    //Firebase auth
    private FirebaseDatabase firebaseDatabase;

    FloatingActionButton addDisplayServiceBtn;
    RecyclerView recyclerView;
    ArrayList<ServicesOffered> recycleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityDisplayServiceBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_display_service);

        //Init Firebase
        firebaseDatabase = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/");

        addDisplayServiceBtn = findViewById(R.id.addDisplayServicesBtn);
        recyclerView = findViewById(R.id.recyclerView);
        recycleList = new ArrayList<>();


        MyServicesOfferedAdapter recyclerAdapter = new MyServicesOfferedAdapter(recycleList, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerAdapter);

        firebaseDatabase.getReference().child("service")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    ServicesOffered servicesOffered = dataSnapshot.getValue(ServicesOffered.class);
                                    recycleList.add(servicesOffered);
                                }
                                recyclerAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        addDisplayServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DisplayServiceActivity.this, InputDisplayedServices.class));
            }
        });

    }
}