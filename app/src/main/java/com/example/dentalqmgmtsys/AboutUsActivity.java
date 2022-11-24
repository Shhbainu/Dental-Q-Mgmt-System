package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.dentalqmgmtsys.databinding.ActivityAboutUsBinding;

public class AboutUsActivity extends AppCompatActivity {

    private ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //handle back button, click
        ImageView back= findViewById(R.id.backBtn);

        //Event onClick for back button
        back.setOnClickListener(v -> finish());
    }
}