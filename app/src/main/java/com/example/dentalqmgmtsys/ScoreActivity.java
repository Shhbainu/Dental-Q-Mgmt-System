package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dentalqmgmtsys.Fragments.QuizTitleFragment;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    private TextView score;
    private Button playAgain, backQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score = findViewById(R.id.txt_score);
        playAgain = findViewById(R.id.bt_result_play_again);
        backQueue = findViewById(R.id.btn_back);

        String score_str = getIntent().getStringExtra("SCORE");
        score.setText(score_str);

        backQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScoreActivity.this, BackToQueueActivity.class));
                finish();
            }
        });


        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScoreActivity.this, QuestionActivity.class));
                finish();
            }
        });
    }
}