package com.example.dentalqmgmtsys.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dentalqmgmtsys.Adapter.MyServiceAdapter;
import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.Common.SpacesItemDecoration;
import com.example.dentalqmgmtsys.Interface.IAllDoctorsLoadListener;
import com.example.dentalqmgmtsys.Interface.IServicesLoadListener;
import com.example.dentalqmgmtsys.Models.Service;
import com.example.dentalqmgmtsys.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class AddAppointmentStep1Fragment extends Fragment implements IAllDoctorsLoadListener, IServicesLoadListener {

    //Variable
    CollectionReference allDoctorsRef;
    CollectionReference allServiceRef;
    IAllDoctorsLoadListener iAllDoctorsLoadListener;
    IServicesLoadListener iServicesLoadListener;

    //Spinner for doctor list
    @BindView(R.id.doctorSpinner)
    MaterialSpinner doctorSpinner;

    //Spinner for services list
    @BindView(R.id.serviceRV)
    RecyclerView serviceRV;

    Unbinder unbinder;

    AlertDialog dialog;

    static AddAppointmentStep1Fragment instance;

    public static AddAppointmentStep1Fragment getInstance(){
        if(instance == null)
            instance = new AddAppointmentStep1Fragment();
        return instance;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allDoctorsRef = FirebaseFirestore.getInstance().collection("AllDoctors");
        iAllDoctorsLoadListener = this;
        iServicesLoadListener = this;

        dialog = new SpotsDialog.Builder().setContext(getActivity()).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_add_appointment_step2,container, false);
        unbinder = ButterKnife.bind(this, itemView);

        initView();
        loadAllDoctors();
        return itemView;

    }

    private void initView() {
        serviceRV.setHasFixedSize(true);
        serviceRV.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        serviceRV.addItemDecoration(new SpacesItemDecoration(4));
    }

    private void loadAllDoctors() {
        allDoctorsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<String> list = new ArrayList<>();
                            list.add("Select a doctor");
                            for(QueryDocumentSnapshot documentSnapshots:task.getResult())

                                list.add(documentSnapshots.getString("name"));
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
    public void onAllDoctorsLoadSuccess(List<String> doctorList) {
        doctorSpinner.setItems(doctorList);
        doctorSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position > 0)
                {
                    Common.currentDoctor = item.toString();
                    loadServicesOfDoctor(item.toString());
                }
                else{
                    serviceRV.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadServicesOfDoctor(String serviceName) {
        dialog.show();

        allServiceRef = FirebaseFirestore.getInstance()
                .collection("AllDoctors")
                .document(serviceName)
                .collection("Services");

        allServiceRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Service> list = new ArrayList<>();
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult())
                    {
                        Service service = documentSnapshot.toObject(Service.class);
                        service.setServiceId(documentSnapshot.getId());
                        list.add(service);
                    }
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServicesLoadSuccess(List<Service> serviceList) {
        MyServiceAdapter adapter = new MyServiceAdapter(getActivity(),serviceList);
        serviceRV.setAdapter(adapter);
        serviceRV.setVisibility(View.VISIBLE);

        dialog.dismiss();
    }

    @Override
    public void onServicesLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}
