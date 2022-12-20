package com.example.dentalqmgmtsys.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.dentalqmgmtsys.Adapter.MyUserAppointmentAdapter;
import com.example.dentalqmgmtsys.Common.Common;
import com.example.dentalqmgmtsys.LandingActivity;
import com.example.dentalqmgmtsys.LoginActivity;
import com.example.dentalqmgmtsys.MainActivity;
import com.example.dentalqmgmtsys.Models.UserAppointmentModel;
import com.example.dentalqmgmtsys.R;
import com.example.dentalqmgmtsys.databinding.FragmentAppointmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AppointmentFragment extends Fragment {

    private FragmentAppointmentBinding binding;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    SimpleDateFormat simpleDateFormat;

    RecyclerView recyclerView;
    ArrayList<UserAppointmentModel> list;
    MyUserAppointmentAdapter adapter;

    DatabaseReference reference, removeRef, ref;

    String date;
    String time;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        recyclerView = getView().findViewById(R.id.recyclerView);

        //String uid = firebaseAuth.getUid();
        //databaseReference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(uid).child("appointments");

        reference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users" + "/" + firebaseUser.getUid() + "/");


        binding.cancelAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //supposedly delete firebase realtimedatabase
                    ViewDialogCancelAppointment viewDialogCancelAppointment = new ViewDialogCancelAppointment();
                    viewDialogCancelAppointment.showDialog(getContext());

                //loadAppointment();
            }
        });
        //Init function
        cancelAppointmentAvailability();
        loadAppointment();

    }
    public class ViewDialogCancelAppointment{

        public void showDialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.layout_dialog_confirm_cancel_appointment);

            Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeAppointmentFR();
                    removeAppointmentFS();
                    dialog.dismiss();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();

                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
    private void removeAppointmentFS() {

        String date = Common.dateNotDone;
        String underscoreDate = date.replaceAll("/", "_");

        firebaseFirestore.collection("AllDoctors")
                .document(Common.doctorNotDone)
                .collection(underscoreDate)
                .document(Common.slotNotDone)
                .delete();
    }

    private void removeAppointmentFR() {
        removeRef = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users" + "/" + firebaseUser.getUid() + "/" + "appointments" + "/");

        removeRef.removeValue();

        Toast.makeText(getActivity(), "Your Appointment has been canceled", Toast.LENGTH_SHORT).show();
    }

    private void cancelAppointmentAvailability() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("appointments")){
                    //binding.bookButton.setVisibility(View.VISIBLE);
                    binding.cancelAppointmentBtn.setEnabled(true);
                }
                else
                {
                    binding.cancelAppointmentBtn.setEnabled(false);
                    //binding.bookButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAppointment() {
        ref = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users" + "/" + firebaseUser.getUid() + "/" + "appointments" + "/");


        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyUserAppointmentAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users" + "/" + firebaseUser.getUid() + "/");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("appointments")){
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren())
                            {
                                //String doctor = dataSnapshot.child("doctor").getValue().toString();
                                //String date = dataSnapshot.child("date").getValue().toString();
                                //String time = dataSnapshot.child("time").getValue().toString();
                                //String service = dataSnapshot.child("service").getValue().toString();
                                //Log.i("Firebase", "Reading from.."+ dataSnapshot.getKey()+", value="+dataSnapshot.getValue()+"UserAppointmentModel "+dataSnapshot.getValue(UserAppointmentModel.class));
                                //Log.i("Firebase", "Test Read"+doctor+" "+date+" "+time+" "+service+"");
                                UserAppointmentModel user = dataSnapshot.getValue(UserAppointmentModel.class);
/*                    Log.i("Firebase", "Reading from.."+ dataSnapshot.child("time").getValue());
                    Log.i("Firebase", "Reading from.."+ dataSnapshot.child("date").getValue());*/

                                list.add(user);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else
                {
                    Log.i("You Have No Appointment", "No appointment");
                    //Toast.makeText(getActivity(), "You don't have an appointment", Toast.LENGTH_SHORT).show();
                    //binding.cancelAppointmentBtn.setEnabled(false);
                    //binding.bookButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}