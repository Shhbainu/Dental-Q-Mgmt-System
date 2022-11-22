package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ImageView;

import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class QuizRuleActivity extends AppCompatActivity {

    public static ViewPager viewPager;
    QuizRuleViewPagerActivity adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_rule);

        SpringDotsIndicator springDotsIndicator = findViewById(R.id.spring_dots_indicator);
        ImageView back= findViewById(R.id.imageRule);

        //Event onClick for back button
        back.setOnClickListener(v -> finish());

        //Init dotIndicator and PagerAdapter
        viewPager=findViewById(R.id.viewpager);
        adapter = new QuizRuleViewPagerActivity(this);
        viewPager.setAdapter(adapter);
        springDotsIndicator.setViewPager(viewPager);
    }
}