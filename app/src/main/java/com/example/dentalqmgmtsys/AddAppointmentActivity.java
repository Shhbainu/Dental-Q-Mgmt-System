package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;

import com.example.dentalqmgmtsys.Adapter.MyViewPagerAdapter;
import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.Common.NonSwipeViewPager;
import com.google.firebase.firestore.CollectionReference;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class AddAppointmentActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    AlertDialog dialog;
    CollectionReference serviceRef;

    @BindView(R.id.stepView)
    StepView stepView;
    @BindView(R.id.prevBtn)
    Button prevBtn;
    @BindView(R.id.nextBtn)
    Button nextBtn;
    @BindView(R.id.viewPager)
    NonSwipeViewPager viewPager;

    //Event
    @OnClick(R.id.prevBtn)
    void prevBtnStep(){
        if(Common.step == 2 || Common.step > 0)
        {
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if(Common.step < 2)// Always enable NEXT when step < 2
            {
                nextBtn.setEnabled(true);
                setColorButton();
            }
        }
    }
    @OnClick(R.id.nextBtn)
    void nextClick(){
        if(Common.step < 2 || Common.step == 0){
            Common.step++; //Increase
            if(Common.step == 1)// After the Doctor and Services
            {
                if(Common.currentService != null)
                {
                    loadServiceByDoctor(Common.currentService.getServiceId());
                    dialog.dismiss();
                }
            }
            else if(Common.step == 2) {
                if (Common.currentTimeSlot != -1)
                    confirmBooking();
            }
            viewPager.setCurrentItem(Common.step);
        }

    }

    private void confirmBooking() {
        //Send broadcast to fragment to last step
        Intent intent = new Intent(Common.KEY_CONFIRM_APPOINTMENT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadServiceByDoctor(String serviceId) {
        dialog.show();
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    //Broadcast Receiver
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int step = intent.getIntExtra(Common.KEY_STEP, 0);
            if(step == 1)
                Common.currentService = intent.getParcelableExtra(Common.KEY_SERVICE);
            else if (step == 2)
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);
            nextBtn.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);
        ButterKnife.bind(AddAppointmentActivity.this);

        dialog = new SpotsDialog.Builder().setContext(this).build();


        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver,new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        //Step view
        setUpStepView();

        //Changing button color
        setColorButton();

        //View
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3); //For 3 Fragments: Steps 1 to 3
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                stepView.go(i, true);
                //If first page is selected prevBtn will be enabled
                if(i == 0)
                    prevBtn.setEnabled(false);
                else
                    prevBtn.setEnabled(true);

                nextBtn.setEnabled(false);

                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
    }

    private void setColorButton() {
        //Color will appear if button is enable
        //Button will only enable if requirements are met
        if(nextBtn.isEnabled()){
            nextBtn.setBackgroundResource(R.color.turquoise);
        }
        else{
            nextBtn.setBackgroundResource(android.R.color.darker_gray);
        }

        //Color will appear if button is enable
        //Button will only enable if requirements are met
        if(prevBtn.isEnabled()){
            prevBtn.setBackgroundResource(R.color.turquoise);
        }
        else{
            prevBtn.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setUpStepView() {
        //Adding array list in steps
        List<String> stepList = new ArrayList<>();
        stepList.add("Doctor & Service");
        stepList.add("Date & Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);

    }
}