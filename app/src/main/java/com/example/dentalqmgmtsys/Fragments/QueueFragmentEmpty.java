package com.example.dentalqmgmtsys.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.dentalqmgmtsys.QueueFinishedDialog;
import com.example.dentalqmgmtsys.QuizTitleActivity;
import com.example.dentalqmgmtsys.databinding.FragmentQueueBinding;

public class QueueFragmentEmpty extends Fragment {
    //Viewbinding
    private FragmentQueueBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQueueBinding.inflate(inflater, container, false);
        binding.quizGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QuizTitleActivity.class);
                startActivity(intent);
            }
        });

        binding.imInBtn.setOnClickListener(view -> {
            openQueueDialog();
        });

        return binding.getRoot();
    }
    private void openQueueDialog() {
        QueueFinishedDialog queueFinishedDialog = new QueueFinishedDialog();
        queueFinishedDialog.show(getParentFragmentManager(), "Queue Info Dialog");
    }
}