package com.example.dentalqmgmtsys.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dentalqmgmtsys.Adapter.MyTimeSlotAdapter;
import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.Common.SpacesItemDecoration;
import com.example.dentalqmgmtsys.Interface.ITimeSlotLoadListener;
import com.example.dentalqmgmtsys.Models.TimeSlot;
import com.example.dentalqmgmtsys.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

public class AddAppointmentStep2Fragment extends Fragment implements ITimeSlotLoadListener {

    //Variable
    DocumentReference slotRef;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    AlertDialog dialog;

    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.timeSlotRV)
    RecyclerView timeSlotRV;
    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;

    SimpleDateFormat simpleDateFormat;

    BroadcastReceiver displayTimeSlot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, 0);//Add current date
            loadAvailableTimeSlotOfDentist(Common.currentDoctor,
                    simpleDateFormat.format(date.getTime()));
        }
    };

    private void loadAvailableTimeSlotOfDentist(String name, final String bookDate) {

        ///AllDoctors/Maria Clara/TimeSlot
        slotRef = FirebaseFirestore.getInstance()
                .collection("AllDoctors")
                .document(name);

        slotRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()) //If dentist is available
                    {
                        //Get information of appointments and load in cardView
                        //If not exist, return empty
                        CollectionReference date = FirebaseFirestore.getInstance()
                                .collection("AllDoctors")
                                .document(name)
                                .collection(bookDate);

                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if(querySnapshot.isEmpty())//If doesn't have any appointment
                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                    else
                                    {
                                        //If appointments exists
                                        List<TimeSlot> timeSlots= new ArrayList<>();
                                        for(QueryDocumentSnapshot document: task.getResult())
                                            timeSlots.add(document.toObject(TimeSlot.class));
                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                            }
                        });
                    }
                }
            }
        });



/*        slotRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    //Get information of appointment
                    CollectionReference dateAppoint = FirebaseFirestore.getInstance()
                            .collection("AllDoctors")
                            .document(Common.currentDoctor)
                            .collection(date);

                    dateAppoint.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot querySnapshot = task.getResult();
                                if(querySnapshot.isEmpty()) // If dont have any appointment
                                    iTimeSlotLoadListener.onTimeSlotLoadEmpty();
                                else
                                {
                                    List<TimeSlot> timeSlots = new ArrayList<>();
                                    for(QueryDocumentSnapshot documentSnapshot1 : task.getResult())
                                        timeSlots.add(documentSnapshot1.toObject(TimeSlot.class));
                                    iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                        }
                    });
                }
            }
        });*/

    }

    static AddAppointmentStep2Fragment instance;

    public static AddAppointmentStep2Fragment getInstance(){
        if(instance == null)
            instance = new AddAppointmentStep2Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iTimeSlotLoadListener = this;

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(displayTimeSlot, new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));

        simpleDateFormat = new SimpleDateFormat("MM_dd_yyyy"); // 11_22_2022 (primary key)

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(displayTimeSlot);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_add_appointment_step3,container, false);
        unbinder = ButterKnife.bind(this, itemView);

        init(itemView);
        return itemView;

    }

    private void init(View itemView) {
        timeSlotRV.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        timeSlotRV.setLayoutManager(gridLayoutManager);
        timeSlotRV.addItemDecoration(new SpacesItemDecoration(8));

        //Calendar
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 31); // 31 day left

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(itemView, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if(Common.appointmentDate.getTimeInMillis() != date.getTimeInMillis())
                {
                    Common.appointmentDate = date; // This will not load again if selected
                    loadAvailableTimeSlotOfDentist(Common.currentDoctor,
                            simpleDateFormat.format(date.getTime()));
                }
            }
        });

    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext(), timeSlotList);
        timeSlotRV.setAdapter(adapter);

        dialog.dismiss();

    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter = new MyTimeSlotAdapter(getContext());
        timeSlotRV.setAdapter(adapter);

        dialog.dismiss();

    }
}
