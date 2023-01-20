package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dentalqmgmtsys.databinding.ActivityLandingBinding;

public class LandingActivity extends AppCompatActivity {

    //Viewbinding
    private ActivityLandingBinding binding;

    static LandingActivity landingActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        landingActivity = this;

        binding = ActivityLandingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //handle login click
        binding.loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                //finish();
            }
        });


        //handle register click
        binding.registerNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingActivity.this, RegisterActivity.class));
                //finish();
            }
        });

    }

    public static LandingActivity getInstance(){
        return landingActivity;
    }
}