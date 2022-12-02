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

public class MainActivity extends AppCompatActivity {

    //Viewbinding
    private ActivityMainBinding binding;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //Initial function call
        replaceFragment(new HomeFragment());

        //Date
        String localDate = "12_02_2022";
        String appointDate = "12_02_2022";

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.appointment:
                    replaceFragment(new AppointmentFragment());
                    break;
                case R.id.queue:
                    if(localDate != appointDate){
                        System.out.println("not yet");
                        replaceFragment(new QueueFragmentEmpty());
                    }else {
                        replaceFragment(new QueueFragment());
                    }
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
        fragmentTransaction.commit();
    }
}