package com.example.dqms_admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dqms_admin.Adapter.MyDoctorsAdapter;
import com.example.dqms_admin.Model.Doctors;
import com.example.dqms_admin.databinding.ActivityDoctorsBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DoctorsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    ActivityDoctorsBinding binding;
    ArrayList<Doctors> doctorsArrayList;
    MyDoctorsAdapter myDoctorsAdapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

            progressDialog = new ProgressDialog(this);

            firebaseFirestore = FirebaseFirestore.getInstance();
            recyclerView = binding.doctorListRV;
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            doctorsArrayList = new ArrayList<Doctors>();
            myDoctorsAdapter = new MyDoctorsAdapter(DoctorsActivity.this, doctorsArrayList);
            recyclerView.setAdapter(myDoctorsAdapter);

            //Add Doctor Button
            binding.addDoctorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
                    viewDialogAdd.showDialog(DoctorsActivity.this);
                }
            });

            showData();

    }

    private void showData() {
        progressDialog.setTitle("Loading data");
        progressDialog.show();
        firebaseFirestore.collection("AllDoctors")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for(DocumentChange documentChange : value.getDocumentChanges()){

                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    if(documentChange.getType()== DocumentChange.Type.ADDED){
                        doctorsArrayList.add(documentChange.getDocument().toObject(Doctors.class));
                    }

                    myDoctorsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public class ViewDialogAdd{
        public void showDialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.layout_dialog_add_doctor);

            EditText doctorName = dialog.findViewById(R.id.addNewDoctorET);

            Button addDoctorBtn = dialog.findViewById(R.id.addDoctorInfoBtn);
            Button cancelDoctorBtn = dialog.findViewById(R.id.cancelDoctorInfoBtn);

            cancelDoctorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            addDoctorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = doctorName.getText().toString();

                    if(name.isEmpty()){
                        Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show();
                    }else{

                        firebaseFirestore.collection("AllDoctors").document(name).set(new Doctors(name));
                        Toast.makeText(context, "Data added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

}