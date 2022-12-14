package com.example.dentalqmgmtsys;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dentalqmgmtsys.databinding.ActivityChangeEmailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChangeEmailActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button changeemail;

    private ActivityChangeEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        changeemail = findViewById(R.id.changeemail);

        // click on this to change email
        binding.changeemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        binding.backToQueueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+snapshot.child("email").getValue();

                        //set data
                        binding.email.setText(email);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void validateData() {
        String email = binding.email.getText().toString().trim();
        String password = binding.logpass.getText().toString().trim();
        String change = binding.change.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //no name is entered
            Toast.makeText(this, "Enter your current email...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            //no name is entered
            Toast.makeText(this, "Enter your current password...", Toast.LENGTH_SHORT).show();
        }
        else if
        (TextUtils.isEmpty(change)){
            //no name is entered
            Toast.makeText(this, "Enter your new email...", Toast.LENGTH_SHORT).show();
        }
        else {
            changeemail(email, password);
        }
    }

    EditText change;

    private void changeemail(String email, final String password) {

        change = findViewById(R.id.change);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Log.d("value", "User re-authenticated.");


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("email", ""+change.getText().toString());

                //update to auth
                user.updateEmail(change.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //update to db
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                            databaseReference.child(user.getUid())
                                    .updateChildren(hashMap);
                            Toast.makeText(ChangeEmailActivity.this, "Email changed." + "Your current email is " + change.getText().toString(), Toast.LENGTH_LONG).show();
                            change.setText(null);
                            binding.logpass.setText(null);
                        }
                        else {
                            Log.d("TAG", "onComplete: " + task.getException());
                            Toast.makeText(ChangeEmailActivity.this, "Wrong credentials. Please enter your password correctly.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
