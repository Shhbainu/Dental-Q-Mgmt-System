package com.example.dqms_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.dqms_admin.databinding.ActivityMain2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity2 extends AppCompatActivity {

    //Viewbinding
    private ActivityMain2Binding binding;

    //Firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Init Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //call a function
        checkUser();


        binding.appointmentsTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity2.this, AppointmentsActivity.class));
            }
        });

        binding.doctorsTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity2.this , DoctorsActivity.class));
            }
        });

        binding.queuesTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity2.this, QueuesActivity.class));
            }
        });

        binding.servicesTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity2.this, ServicesActivity.class));
            }
        });

        binding.exitTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity2.this, LandingActivity.class));
                finish();
            }
        });
    }

    private void checkUser() {
        //Get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            //logged in, get user info
            String email = firebaseUser.getEmail();
            //Inform user
            Toast.makeText(this, "Logged in as "+email, Toast.LENGTH_SHORT).show();
        }
        else {
            startActivity(new Intent(MainActivity2.this, LandingActivity.class));
            finish();
        }
    }
}