package com.example.dqms_admin.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dqms_admin.Model.Doctors;
import com.example.dqms_admin.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyDoctorsAdapter extends RecyclerView.Adapter<MyDoctorsAdapter.ViewHolder> {

    Context context;
    ArrayList<Doctors> doctorsArrayList;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPreferences;

/*    private static String MY_PREFS = "toggle_prefs";
    private static String AVAILABILITY_STATUS = "toggle_on";
    private static String TOGGLE_STATUS = "toggle_status";

    boolean toggle_status;
    boolean doctor_status;

    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;*/

    public MyDoctorsAdapter(Context context, ArrayList<Doctors> doctorsArrayList) {
        this.context = context;
        this.doctorsArrayList = doctorsArrayList;
        firebaseFirestore = FirebaseFirestore.getInstance();
/*        myPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        myEditor = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).edit();

        toggle_status = myPreferences.getBoolean(TOGGLE_STATUS, true);
        doctor_status = myPreferences.getBoolean(AVAILABILITY_STATUS, true);*/
        sharedPreferences = context.getSharedPreferences("save", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_doctor_lists,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Doctors doctors = doctorsArrayList.get(position);

        holder.doctorName.setText(doctors.getName());

        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogUpdate viewDialogUpdate = new ViewDialogUpdate();
                viewDialogUpdate.showDialog(context, doctors.getName());

            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
                viewDialogConfirmDelete.showDialog(context, doctors.getName());
            }
        });

/*        holder.doctorAvailabilityTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.doctorAvailabilityTB.isChecked()){
                }
            }
        });*/

        holder.doctorAvailabilityTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.doctorAvailabilityTB.isChecked()){
                    //Changing color if button toggled true
                    holder.doctorAvailabilityTB.setBackgroundColor(Color.parseColor("#50C2C9"));
                    holder.doctorAvailabilityTB.setTextColor(Color.parseColor("#FFFFFF"));


                    Map<String, Object> doctorAvailability = new HashMap<>();
                    doctorAvailability.put("availability", true);

                    firebaseFirestore.collection("AllDoctors").document(doctors.getName())
                            .set(doctorAvailability, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, doctors.getName() + " is now in service", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ERROR", "Error writing document", e);
                                }
                            });

                    SharedPreferences.Editor editor = context.getSharedPreferences("save", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    holder.doctorAvailabilityTB.setChecked(true);

                }
                else{
                    //Changing color if button toggled false
                    holder.doctorAvailabilityTB.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    holder.doctorAvailabilityTB.setTextColor(Color.parseColor("#000000"));

                    Map<String, Object> doctorAvailability = new HashMap<>();
                    doctorAvailability.put("availability", false);

                    firebaseFirestore.collection("AllDoctors").document(doctors.getName())
                            .set(doctorAvailability, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, doctors.getName() + " is not in service", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ERROR", "Error writing document", e);
                                }
                            });
                    SharedPreferences.Editor editor = context.getSharedPreferences("save", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    holder.doctorAvailabilityTB.setChecked(false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return doctorsArrayList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView doctorName;
        Button updateBtn, deleteBtn;
        SwitchCompat doctorAvailabilityTB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            doctorName = itemView.findViewById(R.id.doctorsNameTV);
            updateBtn = itemView.findViewById(R.id.updateDoctorBtn);
            deleteBtn = itemView.findViewById(R.id.deleteDoctorBtn);
            doctorAvailabilityTB = itemView.findViewById(R.id.doctorsAvailabilityTB);


        }
    }

    public class ViewDialogUpdate{
        public void showDialog(Context context, String name){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.layout_dialog_add_doctor);

            EditText doctorName = dialog.findViewById(R.id.addNewDoctorET);

            doctorName.setText(name);

            Button updateDoctorBtn = dialog.findViewById(R.id.addDoctorInfoBtn);
            Button cancelDoctorBtn = dialog.findViewById(R.id.cancelDoctorInfoBtn);

            updateDoctorBtn.setText("UPDATE");

            cancelDoctorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            updateDoctorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newName = doctorName.getText().toString();

                    if(name.isEmpty()){
                        Toast.makeText(context, "Please enter data", Toast.LENGTH_SHORT).show();
                    }else{

                        if(newName.equals(name)){
                            Toast.makeText(context, "Please change to update", Toast.LENGTH_SHORT).show();
                        }else {
                            firebaseFirestore.collection("AllDoctors").document(name).set(new Doctors(newName));
                            Toast.makeText(context, "Data updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    public class ViewDialogConfirmDelete{
        public void showDialog(Context context, String name){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.layout_dialog_confirm_delete);

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
                    firebaseFirestore.collection("AllDoctors").document(name).delete();
                    Toast.makeText(context, "Data deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

}