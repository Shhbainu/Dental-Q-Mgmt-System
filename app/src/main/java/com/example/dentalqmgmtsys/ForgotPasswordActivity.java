package com.example.dentalqmgmtsys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.dentalqmgmtsys.databinding.ActivityForgotPasswordBinding;
import com.example.dentalqmgmtsys.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    //Viewbinding
    private ActivityForgotPasswordBinding binding;

    //Firebase auth
    private FirebaseAuth firebaseAuth;

    //Progress dialog
    private ProgressDialog progressDialog;

    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.informForgotPasswordEmail.setVisibility(View.GONE);

        //Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        //progressDialog.setCanceledOnTouchOutside(false);
        //progressDialog.setMessage("Please wait...");

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.emailConfirmBtn.setOnClickListener(view -> {
            validateEmail();
        });

    }

    private void validateEmail() {
        /*Validate email, before sending reset email*/

        //Get input data
        email = binding.emailForgotEt.getText().toString().trim();

        //validate email
        if (TextUtils.isEmpty(email)){ // no password entered
            Toast.makeText(this, "Please enter the valid email", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){ //invalid email format
            Toast.makeText(this, "Invalid email pattern", Toast.LENGTH_SHORT).show();
        } else {
            //email is validated, send reset email
            sendResetPassword();
        }
    }

    private void sendResetPassword() {
        //progressDialog.show();
        binding.emailConfirmBtn.setVisibility(View.GONE);
        binding.forgotPassProgressBar.setVisibility(View.VISIBLE);

        firebaseAuth.sendPasswordResetEmail(binding.emailForgotEt.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //progressDialog.dismiss();
                        binding.emailConfirmBtn.setVisibility(View.VISIBLE);
                        binding.forgotPassProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            binding.informForgotPasswordEmail.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Going back to login page
                                    LoginActivity.getInstance().finish();
                                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }, 2000); // 2 sec
                            Toast.makeText(ForgotPasswordActivity.this, "Reset email has been sent", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ForgotPasswordActivity.this, "Enter a correct email address", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}