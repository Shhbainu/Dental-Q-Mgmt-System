package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    //firebase auth
    private FirebaseAuth firebaseAuth;
    //firebase user
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //check current user
                checkUser();
            }
        }, 2000); // 2 sec
    }

    private void checkUser() {
        // Get current user, if logged in (Session)
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
        else {
            startActivity(new Intent(SplashActivity.this, LandingActivity.class));
        }
    }
}