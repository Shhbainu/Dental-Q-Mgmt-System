package com.example.dqms_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.dqms_admin.Adapter.MyQueues2Adapter;
import com.example.dqms_admin.Interface.IAllDoctorsLoadListener;
import com.example.dqms_admin.Interface.IQueuesLoadListener;
import com.example.dqms_admin.Model.Queues;
import com.example.dqms_admin.databinding.ActivityQueuesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class QueuesActivity extends AppCompatActivity implements IAllDoctorsLoadListener, IQueuesLoadListener {

    private static final String TAG = "Queue Activity";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    ActivityQueuesBinding binding;
    RecyclerView recyclerView;
    CollectionReference allDoctorsRef;

    MaterialSpinner doctorSpinnerQueue;
    String selectedDoctorName;

    IAllDoctorsLoadListener iAllDoctorsLoadListener;
    IQueuesLoadListener iQueuesLoadListener;

    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQueuesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        allDoctorsRef = FirebaseFirestore.getInstance().collection("AllDoctors");
        iAllDoctorsLoadListener = this;
        iQueuesLoadListener = this;

        doctorSpinnerQueue = findViewById(R.id.doctorSpinnerQueue);

        recyclerView = binding.queuesRV;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

/*        queuesArrayList = new ArrayList<Queues>();
        myQueuesAdapter = new MyQueuesAdapter(QueuesActivity.this, queuesArrayList);
        recyclerView.setAdapter(myQueuesAdapter);*/

        loadAllDoctors();

        alertDialog = new SpotsDialog.Builder().setContext(QueuesActivity.this).build();


}

    private void loadAllDoctors() {
        allDoctorsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            List<String> list = new ArrayList<>();
                            list.add("Please choose doctor");
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                                list.add(documentSnapshot.getId());
                            Log.i(TAG, "onComplete: "+list);
                            iAllDoctorsLoadListener.onAllDoctorsLoadSuccess(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iAllDoctorsLoadListener.onAllDoctorsLoadFailed(e.getMessage());
                    }
                });
    }

    @Override
    public void onAllDoctorsLoadSuccess(List<String> doctorNameList) {
        doctorSpinnerQueue.setItems(doctorNameList);
        doctorSpinnerQueue.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if( position > 0)
                {
                    selectedDoctorName = item.toString();
                    loadQueuesOfDoctor(selectedDoctorName);
                }
                else
                {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

    }

    private void loadQueuesOfDoctor(String doctorName) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_dd_yyyy");
        Date dateNow = Calendar.getInstance().getTime();
        String dateToday = simpleDateFormat.format(dateNow);

        alertDialog.show();

        CollectionReference doctorsRef = firebaseFirestore.collection("AllDoctors").document(doctorName).collection(dateToday);

/*        doctorsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                assert value != null;
                for (DocumentChange documentChange : value.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        queuesArrayList.add(documentChange.getDocument().toObject(Queues.class));
                    }

                    myQueuesAdapter.notifyDataSetChanged();
                }
            }
        });*/
        doctorsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Queues> list = new ArrayList<>();
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult())
                                list.add(documentSnapshot.toObject(Queues.class));
                            iQueuesLoadListener.onQueuesLoadSuccess(list);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iQueuesLoadListener.onQueuesLoadFailed(e.getMessage());
                    }
                });

    }

    @Override
    public void onAllDoctorsLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onQueuesLoadSuccess(List<Queues> queuesList) {
        MyQueues2Adapter adapter = new MyQueues2Adapter(queuesList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        alertDialog.dismiss();


    }

    @Override
    public void onQueuesLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
    }
}
