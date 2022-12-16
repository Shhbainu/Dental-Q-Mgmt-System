package com.example.dentalqmgmtsys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dentalqmgmtsys.Models.ReferralModel;
import com.example.dentalqmgmtsys.databinding.ActivityReferralBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReferralActivity extends AppCompatActivity {

    ActivityReferralBinding binding;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;

    DatabaseReference ref, reference;

    ClipData clipData;
    ClipboardManager clipboardManager;

    String oppositeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReferralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        reference = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users" + "/" + firebaseUser.getUid() + "/");
        ref = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

        binding.copyTV.setOnClickListener(view -> {
            String text = binding.referCode.getText().toString();
            clipData = ClipData.newPlainText("text", text);
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(ReferralActivity.this, "Text Copied", Toast.LENGTH_SHORT).show();
        });

        //load data
        loadData();
        redeemAvailability();
        clickListener();

    }

    private void clickListener() {
        binding.shareBtn.setOnClickListener(view ->
        {
            String referCode = binding.referCode.getText().toString();
            String shareBody = "Hey, I'm using the best dental mgmt app. Join using my invite code to get 100 " +
                    "credits. My invite code is " + referCode + "\n" +
                    "Download from Play Store\n" + "https://play.google.com/store/apps/details?id=" +
                    getPackageName();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(intent);
        });

        binding.redeemBtn.setOnClickListener(view ->
        {
            Dialog dialog = new Dialog(ReferralActivity.this);
            dialog.setContentView(R.layout.layout_redeem);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);

            EditText editText = dialog.findViewById(R.id.redeem_code);
            AppCompatButton back = dialog.findViewById(R.id.back_btn);
            AppCompatButton redeem = dialog.findViewById(R.id.redeem_btn);

            redeem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String inputCode = editText.getText().toString();

                    if (TextUtils.isEmpty(inputCode))
                    {
                        Toast.makeText(ReferralActivity.this, "Input Valid Code", Toast.LENGTH_SHORT).show();
                    return;
                    }
                    if (inputCode.equals(binding.referCode.getText().toString()))
                    {
                        Toast.makeText(ReferralActivity.this, "You can't input your code", Toast.LENGTH_SHORT).show();
                    return;
                    }
                    redeemQuery(inputCode, dialog);
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        });
    }

    private void redeemQuery(String inputCode, final DialogInterface dialog) {
        Query query = ref.orderByChild("referCode").equalTo(inputCode);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    oppositeID = dataSnapshot.getKey();

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ReferralModel model = snapshot.child(oppositeID).getValue(ReferralModel.class);
                            ReferralModel myModel = snapshot.child(firebaseUser.getUid()).getValue(ReferralModel.class);

                            assert myModel != null;
                            assert model != null;

                            int credits = model.getCredits();
                            int updateCredits = credits+100;

                            int myCredits = myModel.getCredits();
                            int myUpdate = myCredits+100;

                            HashMap<String, Object> map = new HashMap<>();
                            map.put("credits", updateCredits);

                            HashMap<String, Object> myMap = new HashMap<>();

                            myMap.put("credits", myUpdate);
                            myMap.put("redeemed", true);

                            ref.child(oppositeID).updateChildren(map);

                            reference.updateChildren(myMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(ReferralActivity.this,"Congratulations! Credits Added" ,Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.dismiss();
    }

    private void redeemAvailability() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("redeemed"));
                {
                    boolean isAvailable = snapshot.child("redeemed").getValue(Boolean.class);

                    if (isAvailable)
                    {
                        binding.redeemBtn.setVisibility(View.GONE);
                        binding.redeemBtn.setEnabled(false);
                    }
                    else
                    {
                        binding.redeemBtn.setEnabled(true);
                        binding.redeemBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Get Refer Code
                String coins = snapshot.child("referCode").getValue(String.class);
                binding.referCode.setText(String.valueOf(coins));

                //Get Credits Balance
                ReferralModel referralModel = snapshot.getValue(ReferralModel.class);
                binding.creditsTV.setText(String.valueOf(referralModel.getCredits()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}