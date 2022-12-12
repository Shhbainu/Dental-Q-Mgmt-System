package com.example.dqms_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.example.dqms_admin.Adapter.MyQueuesAdapter;
import com.example.dqms_admin.Model.Doctors;
import com.example.dqms_admin.Model.Queues;
import com.example.dqms_admin.databinding.ActivityDoctorsBinding;
import com.example.dqms_admin.databinding.ActivityQueuesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class QueuesActivity extends AppCompatActivity {

    private static final String TAG = "Queue Activity";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    ActivityQueuesBinding binding;
    RecyclerView recyclerView;
    ArrayList<Queues> queuesArrayList;
    MyQueuesAdapter myQueuesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQueuesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.queueListRV;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        queuesArrayList = new ArrayList<Queues>();
        myQueuesAdapter = new MyQueuesAdapter(QueuesActivity.this, queuesArrayList);
        recyclerView.setAdapter(myQueuesAdapter);

        showData();
}

    private void showData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_dd_yyyy");
        Date dateNow = Calendar.getInstance().getTime();
        String dateToday = simpleDateFormat.format(dateNow);
        Queues queues = new Queues();

        CollectionReference doctorsRef = firebaseFirestore.collection("AllDoctors").document("Juan Dela Cruz").collection(dateToday);

       doctorsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
           @SuppressLint("NotifyDataSetChanged")
           @Override
           public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               if (error != null) {
                   Log.e("Firestore error", error.getMessage());
                   return;
               }
               for (DocumentChange documentChange : value.getDocumentChanges()) {

                   if (documentChange.getType() == DocumentChange.Type.ADDED) {
                       queuesArrayList.add(documentChange.getDocument().toObject(Queues.class));
                   }

                   myQueuesAdapter.notifyDataSetChanged();
               }
           }
       });

    }
}
