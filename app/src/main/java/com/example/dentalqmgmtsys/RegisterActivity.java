package com.example.dentalqmgmtsys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.dentalqmgmtsys.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //Viewbinding
    private ActivityRegisterBinding binding;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;

    //Progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle back button, click
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle has an account, click
        binding.hasAnAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        //handle register click
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    //Declare Global Variables
    private String fName = "", lName = "", age = "", address = "", phone = "", email = "", password = "";

    private void validateData() {
        /*Validate data, before creating account*/

        //Get input data
        fName = binding.fNameET.getText().toString().trim();
        lName = binding.lNameET.getText().toString().trim();
        age = binding.ageET.getText().toString().trim();
        address = binding.addressET.getText().toString().trim();
        phone = binding.phoneET.getText().toString().trim();
        email = binding.emailET.getText().toString().trim();
        password = binding.passwordET.getText().toString().trim();
        String cPassword = binding.confirmPassET.getText().toString().trim();

        //validate input data
        if(TextUtils.isEmpty(fName) || TextUtils.isEmpty(lName)){ //names are empty
            Toast.makeText(this, "Enter you name..", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){ //invalid email pattern
            Toast.makeText(this, "Invalid email pattern", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){ //password is empty
            Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cPassword)){ //confirm password is empty
            Toast.makeText(this, "Confirm your password", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(cPassword)){ //password and confirm password doesn't match
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 6){ //password is less than 6 characters
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(age)){ //age is empty
            Toast.makeText(this, "Enter your age", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(address)){ //address is empty
            Toast.makeText(this, "Enter your address", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){ //phone is empty
            Toast.makeText(this, "Enter your phone", Toast.LENGTH_SHORT).show();
        }
        else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        //show progress
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account create success, now add in firebase realtime database
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //account create failed
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user info...");

        //timestamp
        long timestamp = System.currentTimeMillis();

        //get current user uid, since user is registered so we can get it now..
        String uid = firebaseAuth.getUid();

        //make a refer code from user id
        String referCode = uid.substring(0,7);

        //setup data to add in the realtime database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("fName", fName);
        hashMap.put("lName", lName);
        hashMap.put("age", age);
        hashMap.put("address", address);
        hashMap.put("email", email);
        hashMap.put("phone", phone);
        hashMap.put("userType", "user");
        hashMap.put("timestamp", timestamp);
        hashMap.put("profileImage", ""); //add empty, in case needed
        hashMap.put("credits", 100);
        hashMap.put("referCode", referCode);
        hashMap.put("redeemed", false);
        //hashMap.put("profilePic", ""); //add empty, in case needed

        //set data to db
        DatabaseReference ref = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //data added to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                        //will proceed to user homepage
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //data adding failed
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}