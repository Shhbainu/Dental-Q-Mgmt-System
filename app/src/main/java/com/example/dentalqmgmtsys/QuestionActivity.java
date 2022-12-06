package com.example.dentalqmgmtsys;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView question, qCount, timer;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int questionNum;
    private CountDownTimer countDown;
    public int score;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        question = findViewById(R.id.question);
        qCount = findViewById(R.id.quest_num);
        timer = findViewById(R.id.countdown);

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();

        getQuestionList();
        score = 0;

    }

    private void getQuestionList()
    {
        questionList = new ArrayList<>();

        db.collection("QuizGame")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            QuerySnapshot questions = task.getResult();

                            for(QueryDocumentSnapshot doc : questions)
                            {
                                questionList.add(new Question(doc.getString("QUESTION"),
                                        doc.getString("A"),
                                        doc.getString("B"),
                                        doc.getString("C"),
                                        doc.getString("D"),
                                        Integer.valueOf(doc.getString("ANSWER"))
                                        ));
                            }

                            setQuestion();

                        }
                        else
                        {
                            Toast.makeText(QuestionActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    private void setQuestion(){
        timer.setText(String.valueOf(10));

        question.setText(questionList.get(0).getQuestion());
        option1.setText(questionList.get(0).getOptionA());
        option2.setText(questionList.get(0).getOptionB());
        option3.setText(questionList.get(0).getOptionC());
        option4.setText(questionList.get(0).getOptionD());

        qCount.setText(String.valueOf(1)+ "/" + String.valueOf(questionList.size()));

        startTimer();

        questionNum = 0;
    }

    private void startTimer(){
        countDown = new CountDownTimer(11000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished < 10000)
                    timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                changeQuestion();
            }
        };

        countDown.start();

    }

    @Override
    public void onClick(View v) {

        int selectedOption = 0;
        switch(v.getId())
        {
            case R.id.option1:
                selectedOption = 1;
                break;

            case R.id.option2:
                selectedOption = 2;
                break;

            case R.id.option3:
                selectedOption = 3;
                break;

            case R.id.option4:
                selectedOption = 4;
                break;

            default:
        }
        countDown.cancel();
        checkAnswer(selectedOption, v);

    }
    private void checkAnswer(int selectedOption, View view){

        if(selectedOption == questionList.get(questionNum).correctAns){
            //right
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score++;
        }
        else{
            //wrong

            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.RED));

            switch (questionList.get(questionNum).getCorrectAns())
            {
                case 1:
                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
            }
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        },1000);
    }

    private void changeQuestion(){
        if(questionNum < questionList.size()-1)
        {
            questionNum++;

            playAnim(question, 0, 0);
            playAnim(option1, 0,1);
            playAnim(option2, 0,2);
            playAnim(option3, 0,3);
            playAnim(option4, 0,4);

            qCount.setText(String.valueOf(questionNum+1) + "/" + String.valueOf(questionList.size()));

            timer.setText(String.valueOf(10));
            startTimer();

        }
        else{
            //finish quiz
            Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);
            intent.putExtra("SCORE", String.valueOf(score)+ "/" + String.valueOf(questionList.size()));
            startActivity(intent);
            QuestionActivity.this.finish();
        }
    }

    private void playAnim(View view, final int value, int viewNum){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(300)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if(value == 0){
                            switch (viewNum)
                            {
                                case 0:
                                    ((TextView)view).setText(questionList.get(questionNum).getQuestion());
                                    break;
                                case 1:
                                    ((Button)view).setText(questionList.get(questionNum).getOptionA());
                                    break;
                                case 2:
                                    ((Button)view).setText(questionList.get(questionNum).getOptionB());
                                    break;
                                case 3:
                                    ((Button)view).setText(questionList.get(questionNum).getOptionC());
                                    break;
                                case 4:
                                    ((Button)view).setText(questionList.get(questionNum).getOptionD());
                                    break;
                            }

                            if(viewNum != 0)
                                ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF018786")));

                            playAnim(view, 1, viewNum);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }
}
