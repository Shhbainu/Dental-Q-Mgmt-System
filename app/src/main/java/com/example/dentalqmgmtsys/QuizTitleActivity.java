package com.example.dentalqmgmtsys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class QuizTitleActivity extends AppCompatActivity {

    private Button playBtn, rulesBtn;
    private ImageView backToQueueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_title);

        backToQueueBtn = findViewById(R.id.backToQueueBtn);
        playBtn = findViewById(R.id.play_btn);
        rulesBtn = findViewById(R.id.rule_btn);

        backToQueueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(QuizTitleActivity.this, QuestionActivity.class);
                startActivity(intent);
            }
        });

        rulesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizTitleActivity.this, QuizRuleActivity.class);
                startActivity(intent);
            }
        });
    }
}