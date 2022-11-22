package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class QuizCorrectActivity extends AppCompatActivity {


    TextView txtScore;
    Button btStartQuizAgain,btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_correct);

        txtScore = findViewById(R.id.txt_score);

        btnBack = findViewById(R.id.btn_back);
        btStartQuizAgain = findViewById(R.id.bt_result_play_again);

        Bundle bundle = getIntent().getExtras();
        ArrayList<String>arrayList=bundle.getStringArrayList("stringArray");
        ListView listView = findViewById(R.id.list_wrong_answer);
        ArrayAdapter<String> items = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(items);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuizCorrectActivity.this, BackToQueueActivity.class));
                finish();
            }
        });


        btStartQuizAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuizCorrectActivity.this, QuizGameActivity.class));
                finish();
            }
        });
        Intent intent = getIntent();
        int score = intent.getIntExtra("score",0);

        txtScore.setText(String.valueOf(score));
    }
}
