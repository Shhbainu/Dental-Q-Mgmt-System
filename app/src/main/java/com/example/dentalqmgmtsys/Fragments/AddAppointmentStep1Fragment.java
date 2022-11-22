package com.example.dentalqmgmtsys.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dentalqmgmtsys.R;

public class AddAppointmentStep1Fragment extends Fragment {

    static AddAppointmentStep1Fragment instance;

    public static AddAppointmentStep1Fragment getInstance(){
        if(instance == null)
            instance = new AddAppointmentStep1Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_add_appointment_step1,container, false);
    }
}
