package com.example.dqms_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {

    private static final String TAG = "Appointments Activity";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    List<String> doctorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

/*        doctorsLoad();*/
        doctorsAppointmentLoad();
    }

    private void doctorsLoad() {
        CollectionReference doctorRef = firebaseFirestore.collection("AllDoctors");

        doctorRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    doctorList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()){
                        doctorList.add(document.get("name").toString());

                    }
                }
                else {
                    Log.d(TAG, "Error getting documents : ", task.getException());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void doctorsAppointmentLoad() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_dd_yyyy");
        Date dateNow = Calendar.getInstance().getTime();
        String dateToday = simpleDateFormat.format(dateNow);

        CollectionReference appointmentRef = firebaseFirestore.collection("AllDoctors").document("Juan Dela Cruz").collection(dateToday);

        appointmentRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        Log.d(TAG, documentSnapshot.getId() + "=>" + documentSnapshot.getData());
                        Log.d(TAG, documentSnapshot.getId() + "=>" + documentSnapshot.get("patientName").toString());
                    }
                }else
                {
                    Log.d(TAG, "ERROR", task.getException());
                }
            }
        });
    }
}