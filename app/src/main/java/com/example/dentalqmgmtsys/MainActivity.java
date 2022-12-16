package com.example.dentalqmgmtsys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.Fragments.AppointmentFragment;
import com.example.dentalqmgmtsys.Fragments.HomeFragment;
import com.example.dentalqmgmtsys.Fragments.ProfileFragment;
import com.example.dentalqmgmtsys.Fragments.QueueFragment;
import com.example.dentalqmgmtsys.Fragments.QueueFragmentEmpty;
import com.example.dentalqmgmtsys.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //DataBase
    String key;
    //Viewbinding
    ActivityMainBinding binding;

    //Firebase Auth
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(Common.appointmentID);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //Initial function call
        replaceFragment(new HomeFragment());
//        //Database
        String uid = firebaseAuth.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(uid);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.appointment:
                    replaceFragment(new AppointmentFragment());
                    break;
                case R.id.queue:
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("appointments").exists()){
                                replaceFragment(new QueueFragment());
                            }else{
                                replaceFragment(new QueueFragmentEmpty());
                                Toast.makeText(MainActivity.this, "You have make an appointment", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;

            }
            return true;
        });
        checkUser();
        loadUserInfo();
    }


    private void loadUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String fName = ""+snapshot.child("fName").getValue();
                        String lName = ""+snapshot.child("lName").getValue();
                        String phone = ""+snapshot.child("phone").getValue();

                        Common.currentUser = fName + lName;
                        Common.currentPhone = phone;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkUser() {
        // Get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            //logged in, get user info
            String email = firebaseUser.getEmail();
            //set in toast
            Toast.makeText(this, "Logged in as "+email, Toast.LENGTH_SHORT).show();
        }
        else {
            startActivity(new Intent(MainActivity.this, LandingActivity.class));
            finish();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }
}