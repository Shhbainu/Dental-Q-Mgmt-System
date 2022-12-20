package com.example.dqms_admin;

import android.app.AlertDialog;
import android.app.Dialog;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.example.dqms_admin.Adapter.MyServicesAdapter;
import com.example.dqms_admin.Interface.IAllDoctorsLoadListener;
import com.example.dqms_admin.Interface.IServicesLoadListener;
import com.example.dqms_admin.Model.Services;
import com.example.dqms_admin.databinding.ActivityDoctorsBinding;
import com.example.dqms_admin.databinding.ActivityServicesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class ServicesActivity extends AppCompatActivity implements IAllDoctorsLoadListener, IServicesLoadListener {

    private static final String TAG = "Service Activity";
    CollectionReference allDoctorsRef;
    CollectionReference serviceRef;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    ActivityServicesBinding binding;
    DocumentReference addServiceRef;

    IAllDoctorsLoadListener iAllDoctorsLoadListener;
    IServicesLoadListener iServicesLoadListener;

    MaterialSpinner spinner;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;

    AlertDialog alertDialog;
    String selectedDoctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        allDoctorsRef = FirebaseFirestore.getInstance().collection("AllDoctors");
        iAllDoctorsLoadListener = this;
        iServicesLoadListener = this;
        spinner = findViewById(R.id.doctorSpinner);

        recyclerView = findViewById(R.id.serviceRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loadAllDoctors();

        alertDialog = new SpotsDialog.Builder().setContext(ServicesActivity.this).build();

        binding.addServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
                viewDialogAdd.showDialog(ServicesActivity.this);
            }
        });






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
        spinner.setItems(doctorNameList);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if( position > 0)
                {
                    binding.deleteServiceInfoBtn.setVisibility(View.VISIBLE);
                    selectedDoctorName = item.toString();
                    loadServicesOfDoctor(selectedDoctorName);

                }
                else
                {
                    binding.deleteServiceInfoBtn.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    floatingActionButton.setVisibility(View.GONE);


                }
            }
        });

    }

    private void loadServicesOfDoctor(String doctorName) {
        alertDialog.show();

        serviceRef = FirebaseFirestore.getInstance().collection("AllDoctors")
                .document(doctorName)
                .collection("Services");

        serviceRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Services> list = new ArrayList<>();
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult())
                                list.add(documentSnapshot.toObject(Services.class));
                            Log.i(TAG, "onComplete: " +list);
                            iServicesLoadListener.onServicesLoadSuccess(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iServicesLoadListener.onServicesLoadFailed(e.getMessage());
                    }
                });


    }

    @Override
    public void onAllDoctorsLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onServicesLoadSuccess(List<Services> servicesList) {
        MyServicesAdapter adapter = new MyServicesAdapter(servicesList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);

        alertDialog.dismiss();

    }

    @Override
    public void onServicesLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();

    }

    public class ViewDialogAdd{

        public void showDialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.layout_dialog_add_service);

            EditText serviceName = dialog.findViewById(R.id.addNewServiceET);

            Button addServiceBtn = dialog.findViewById(R.id.addServiceInfoBtn);
            Button cancelServiceBtn = dialog.findViewById(R.id.cancelServiceInfoBtn);

            cancelServiceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            addServiceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newService = serviceName.getText().toString();

                    Map<String, Object> data = new HashMap<>();
                    data.put("name", newService);

                    if(newService.isEmpty()){
                        Toast.makeText(context, "Please enter a service", Toast.LENGTH_SHORT).show();
                    }else{
                        firebaseFirestore.collection("AllDoctors").document(selectedDoctorName).collection("Services")
                                        .add(data);

                        Toast.makeText(context, "Data added" +data.values(), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onClick: " +data.values());
                        dialog.dismiss();

                    }
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}