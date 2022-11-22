package com.example.dentalqmgmtsys.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dentalqmgmtsys.QuizGameActivity;
import com.example.dentalqmgmtsys.QuizRuleActivity;
import com.example.dentalqmgmtsys.databinding.FragmentQuizTitleBinding;


public class QuizTitleFragment extends Fragment {

    //Viewbinding
    private FragmentQuizTitleBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuizTitleBinding.inflate(inflater, container, false);
        binding.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QuizGameActivity.class);
                startActivity(intent);
            }
        });
        binding.ruleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QuizRuleActivity.class);
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }
}