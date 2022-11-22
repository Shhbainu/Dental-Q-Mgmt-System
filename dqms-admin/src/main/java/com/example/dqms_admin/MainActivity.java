package com.example.dqms_admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dqms_admin.databinding.ActivityMainBinding;
import com.example.dqms_admin.fragments.AppointmentFragment;
import com.example.dqms_admin.fragments.DashboardFragment;
import com.example.dqms_admin.fragments.DoctorsFragment;
import com.example.dqms_admin.fragments.QueueFragment;
import com.example.dqms_admin.fragments.ServicesFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //Viewbinding
    private ActivityMainBinding binding;

    //Firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //Calls the function
        replaceFragment(new DashboardFragment());

        binding.bottomNavigationViewAdmin.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.dashboard:
                    replaceFragment(new DashboardFragment());
                    break;
                case R.id.appointments:
                    replaceFragment(new AppointmentFragment());
                    break;
                case R.id.queue:
                    replaceFragment(new QueueFragment());
                    break;
                case R.id.doctor:
                    replaceFragment(new DoctorsFragment());
                    break;
                case R.id.service:
                    replaceFragment(new ServicesFragment());
                    break;
            }
            return true;
        });
        checkUser();
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