package com.example.dqms_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dqms_admin.Adapter.MyQuizAdapter;
import com.example.dqms_admin.Model.Quiz;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowQuestionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private MyQuizAdapter adapter;
    private List<Quiz> list;

    FloatingActionButton addQuestionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_question);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager((new LinearLayoutManager(this)));

        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        adapter = new MyQuizAdapter(this, list);
        recyclerView.setAdapter(adapter);
        addQuestionBtn= findViewById(R.id.addQuestion);

        showData();

        //add btn
        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowQuestionActivity.this, UpdateQuizActivity.class));
                finish();
            }
        });

    }

    public void showData(){

        db.collection("QuizGame")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        for (DocumentSnapshot snapshot:task.getResult()){

                            Quiz quiz = new Quiz(
                                    snapshot.getString("id"),
                                    snapshot.getString("QUESTION"),
                                    snapshot.getString("A"),
                                    snapshot.getString("B"),
                                    snapshot.getString("C"),
                                    snapshot.getString("D"),
                                    snapshot.getString("ANSWER"));
                            list.add(quiz);

                        }

                        adapter.notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowQuestionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}