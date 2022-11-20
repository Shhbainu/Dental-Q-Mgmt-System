package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dentalqmgmtsys.Fragments.AppointmentFragment;
import com.example.dentalqmgmtsys.Fragments.HomeFragment;
import com.example.dentalqmgmtsys.Fragments.ProfileFragment;
import com.example.dentalqmgmtsys.Fragments.QueueFragment;
import com.example.dentalqmgmtsys.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

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

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.appointment:
                    replaceFragment(new AppointmentFragment());
                    break;
                case R.id.queue:
                    replaceFragment(new QueueFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
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