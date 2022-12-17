package com.example.dentalqmgmtsys;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ReferInfoDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Refer & Earn Details")
                .setMessage("If You Find Any Problem With Refer System Kindly Contact Us\n\n" +
                        "500 credits = 5% Discount\n1000 credits = 10% Discount\n\n" +
                        "Do Not Make Multiple Accounts From Same Device\n\n" +
                        "Terms & Conditions Apply")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }
}
