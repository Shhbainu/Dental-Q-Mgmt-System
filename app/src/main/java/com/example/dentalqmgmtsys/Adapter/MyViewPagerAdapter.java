package com.example.dentalqmgmtsys.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dentalqmgmtsys.Fragments.AddAppointmentStep1Fragment;
import com.example.dentalqmgmtsys.Fragments.AddAppointmentStep2Fragment;
import com.example.dentalqmgmtsys.Fragments.AddAppointmentStep3Fragment;

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    public MyViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return AddAppointmentStep1Fragment.getInstance();
            case 1:
                return AddAppointmentStep2Fragment.getInstance();
            case 2:
                return AddAppointmentStep3Fragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
