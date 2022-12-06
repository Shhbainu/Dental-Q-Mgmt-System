package com.example.dqms_admin.Adapter;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dqms_admin.Model.Quiz;
import com.example.dqms_admin.R;
import com.example.dqms_admin.ShowQuestionActivity;
import com.example.dqms_admin.UpdateQuizActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyQuizAdapter extends RecyclerView.Adapter<MyQuizAdapter.MyViewHolder>{

    private ShowQuestionActivity activity;

    private List<Quiz> mList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyQuizAdapter(ShowQuestionActivity activity, List<Quiz> mList){
        this.activity = activity;
        this.mList = mList;
    }

    public void updateData(int position){
        Quiz item = mList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("uId", item.getId());
        bundle.putString("uQuestion", item.getQuestion());
        bundle.putString("uAnsA", item.getAnsA());
        bundle.putString("uAnsB", item.getAnsB());
        bundle.putString("uAnsC", item.getAnsC());
        bundle.putString("uAnsD", item.getAnsD());
        bundle.putString("uAnswer", item.getAnswer());
        Intent intent = new Intent(activity, UpdateQuizActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void deleteData(int position){
        Quiz item = mList.get(position);
        db.collection("QuizGame").document(item.getId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            notifyRemoved(position);
                            Toast.makeText(activity, "Data deleted", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(activity, "Error:" +task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void notifyRemoved(int position){
        mList.remove(position);
        notifyItemRemoved(position);
        activity.showData();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.quiz_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.question.setText(mList.get(position).getQuestion());

        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData(position);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                deleteData(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView question, ansA;
        Button updateBtn, deleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question_text);
            updateBtn = itemView.findViewById(R.id.updateQuestionBtn);
            deleteBtn = itemView.findViewById(R.id.deleteQuestionBtn);

        }
    }
}

