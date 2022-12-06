package com.example.dqms_admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateQuizActivity extends AppCompatActivity {

    EditText questionET, ansAET, ansBET, ansCET, ansDET, ansET;
    Button saveBtn, showListBtn;

    private String uId, uQuestion, uAnsA, uAnsB, uAnsC, uAnsD,uAnswer;

    //Firestore instance
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_quiz);

        //initialize views with its xml
        questionET = findViewById(R.id.questionET);
        ansAET = findViewById(R.id.ansA);
        ansBET = findViewById(R.id.ansB);
        ansCET = findViewById(R.id.ansC);
        ansDET = findViewById(R.id.ansD);
        ansET = findViewById(R.id.ansET);
        ansET.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "4")});
        saveBtn = findViewById(R.id.saveBtn);
        showListBtn = findViewById(R.id.showListBtn);

        //firestore
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            saveBtn.setText("UPDATE");
            uId = bundle.getString("uId");
            uQuestion = bundle.getString("uQuestion");
            uAnsA = bundle.getString("uAnsA");
            uAnsB = bundle.getString("uAnsB");
            uAnsC = bundle.getString("uAnsC");
            uAnsD = bundle.getString("uAnsD");
            uAnswer = bundle.getString("uAnswer");

            //set test
            questionET.setText(uQuestion);
            ansAET.setText(uAnsA);
            ansBET.setText(uAnsB);
            ansCET.setText(uAnsC);
            ansDET.setText(uAnsD);
            ansET.setText(uAnswer);
        }
        else {
            saveBtn.setText("SAVE");
        }
        //click button to upload data
        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //input data
                String question = questionET.getText().toString().trim();
                String ansA = ansAET.getText().toString().trim();
                String ansB = ansBET.getText().toString().trim();
                String ansC = ansCET.getText().toString().trim();
                String ansD = ansDET.getText().toString().trim();
                String ans = ansET.getText().toString().trim();

                if(questionET.getText().toString().isEmpty()){
                    Toast.makeText(UpdateQuizActivity.this, "Please enter the question", Toast.LENGTH_SHORT).show();
                }
                else if(ansAET.getText().toString().isEmpty()){
                    Toast.makeText(UpdateQuizActivity.this, "Please enter choices A", Toast.LENGTH_SHORT).show();
                }
                else if(ansBET.getText().toString().isEmpty()){
                    Toast.makeText(UpdateQuizActivity.this, "Please enter choices B", Toast.LENGTH_SHORT).show();
                }
                else if(ansCET.getText().toString().isEmpty()){
                    Toast.makeText(UpdateQuizActivity.this, "Please enter choices C", Toast.LENGTH_SHORT).show();
                }
                else if(ansDET.getText().toString().isEmpty()){
                    Toast.makeText(UpdateQuizActivity.this, "Please enter choices D", Toast.LENGTH_SHORT).show();
                }
                else if(ansET.getText().toString().isEmpty()){
                    Toast.makeText(UpdateQuizActivity.this, "Please enter the correct answer", Toast.LENGTH_SHORT).show();
                }
                else{
                    // function call to upload data
                    Bundle bundle1 = getIntent().getExtras();
                    if (bundle1 != null ){
                        String id = uId;
                        updateToFirestore(id, question, ansA, ansB, ansC, ansD, ans);
                    }
                    else {
                        uploadData(question,ansA,ansB,ansC,ansD,ans);
                    }
                }
            }
        });

        showListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateQuizActivity.this, ShowQuestionActivity.class));
                finish();
            }
        });

    }

     private void updateToFirestore(String id, String question, String ansA,String ansB,String ansC,String ansD,String ans){
         //add this data
         db.collection("QuizGame").document(id)
                 .update("QUESTION", question, "A", ansA, "B", ansB, "C", ansC, "D", ansD, "ANSWER", ans)
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         //this will be called when data is added successfully
                         if (task.isSuccessful()){
                             Toast.makeText(UpdateQuizActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
                         }
                         else {
                             Toast.makeText(UpdateQuizActivity.this, "Error:" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     }

                 })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         //this will be called if there is any error while uploading
                         Toast.makeText(UpdateQuizActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 });
    }


    private void uploadData(String question, String ansA,String ansB,String ansC,String ansD,String ans) {
        //set title of progress bar
        Toast.makeText(UpdateQuizActivity.this, "Adding Data to Firestore", Toast.LENGTH_SHORT).show();
        //random id for each data to be stored
        String id = UUID.randomUUID().toString();

        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("QUESTION", question);
        doc.put("ANSWER", ans);
        doc.put("A", ansA);
        doc.put("B", ansB);
        doc.put("C", ansC);
        doc.put("D", ansD);

        //add this data
        db.collection("QuizGame").document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //this will be called when data is added successfully
                        Toast.makeText(UpdateQuizActivity.this, "Uploaded successfully...", Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //this will be called if there is any error while uploading
                        Toast.makeText(UpdateQuizActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}