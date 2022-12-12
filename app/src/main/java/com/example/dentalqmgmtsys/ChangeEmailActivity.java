package com.example.dentalqmgmtsys;

import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        // Initialising the email and password
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.logpass);
        changeemail = findViewById(R.id.changeemail);

        // click on this to change email
        binding.changeemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeemail(email.getText().toString(), password.getText().toString());
            }
        });

        binding.backToProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    EditText change;

    // Here we are going to change our email using firebase re-authentication
    private void changeemail(String email, final String password) {

        change = findViewById(R.id.change);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, password); // Current Login Credentials

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Log.d("value", "User re-authenticated.");

                // Now change your email address \\
                //----------------Code for Changing Email Address----------\\

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("email", ""+change.getText().toString());

                //update to db
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(user.getUid())
                        .updateChildren(hashMap);
                //update to auth
                user.updateEmail(change.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangeEmailActivity.this, "Email changed." + "Your current email is " + change.getText().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
