package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.dentalqmgmtsys.Fragments.AppointmentFragment;
import com.example.dentalqmgmtsys.Fragments.HomeFragment;
import com.example.dentalqmgmtsys.Fragments.ProfileFragment;
import com.example.dentalqmgmtsys.Fragments.QueueFragment;
import com.example.dentalqmgmtsys.databinding.ActivityMainBinding;

public class BackToQueueActivity extends AppCompatActivity {

    //Viewbinding
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Initial function call
        replaceFragment(new QueueFragment());

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
        if (savedInstanceState == null) {
            binding.bottomNavigationView.setSelectedItemId(R.id.queue); // change to whichever id should be default
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}