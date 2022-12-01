package com.example.dqms_admin.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dqms_admin.Model.Doctors;
import com.example.dqms_admin.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyDoctorsAdapter extends RecyclerView.Adapter<MyDoctorsAdapter.ViewHolder> {

    Context context;
    ArrayList<Doctors> doctorsArrayList;
    FirebaseFirestore firebaseFirestore;

    public MyDoctorsAdapter(Context context, ArrayList<Doctors> doctorsArrayList) {
        this.context = context;
        this.doctorsArrayList = doctorsArrayList;
        firebaseFirestore = FirebaseFirestore.getInstance();
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

    }

    @Override
    public int getItemCount() {
        return doctorsArrayList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView doctorName;
        Button updateBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            doctorName = itemView.findViewById(R.id.doctorsNameTV);
            updateBtn = itemView.findViewById(R.id.updateDoctorBtn);
            deleteBtn = itemView.findViewById(R.id.deleteDoctorBtn);

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