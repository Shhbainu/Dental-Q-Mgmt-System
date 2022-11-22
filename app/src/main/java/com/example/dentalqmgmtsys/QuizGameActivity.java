package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class QuizGameActivity extends AppCompatActivity implements View.OnClickListener{

    TextView totalQuestionsTextView;
    TextView questionTextView;
    Button ansA, ansB, ansC, ansD;
    int score=0;
    int totalQuestion = QuestionAnswer.question.length;
    int currentQuestionIndex = 0;
    private ArrayList<String> wrongAnswerList = new ArrayList<>();
    String selectedAnswer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_game);

        questionTextView = findViewById(R.id.question);
        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);

        loadNewQuestion();

    }

    @Override
    public void onClick(View view) {

        Button clickedButton = (Button) view;

        selectedAnswer  = clickedButton.getText().toString();
        if(selectedAnswer.equals(QuestionAnswer.correctAnswers[currentQuestionIndex])){
            score+=10;
        }
        else{
            wrongAnswerList.add(QuestionAnswer.question[currentQuestionIndex]+"\n"+"ANSWER: "+QuestionAnswer.correctAnswers[currentQuestionIndex]);
        }
        currentQuestionIndex++;
        loadNewQuestion();

    }

    void loadNewQuestion(){

        if(currentQuestionIndex == totalQuestion ){
            finishQuiz();
            return;
        }

        questionTextView.setText(QuestionAnswer.question[currentQuestionIndex]);
        ansA.setText(QuestionAnswer.choices[currentQuestionIndex][0]);
        ansB.setText(QuestionAnswer.choices[currentQuestionIndex][1]);
        ansC.setText(QuestionAnswer.choices[currentQuestionIndex][2]);
        ansD.setText(QuestionAnswer.choices[currentQuestionIndex][3]);

    }

    void finishQuiz() {
        if(score > totalQuestion * 6){
            Intent intent = new Intent(this,QuizCorrectActivity.class);
            intent.putExtra("score",score);
            intent.putExtra("stringArray", wrongAnswerList);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(this,QuizWrongActivity.class);
            intent.putExtra("score",score);
            intent.putExtra("stringArray", wrongAnswerList);
            startActivity(intent);
            finish();
        }
    }
}