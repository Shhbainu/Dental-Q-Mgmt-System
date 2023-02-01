package com.example.dentalqmgmtsys;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dentalqmgmtsys.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    //Viewbinding
    private ActivityRegisterBinding binding;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;

    //Progress dialog
    private ProgressDialog progressDialog;

    //BirthDate Datepicker
    DatePickerDialog.OnDateSetListener setListener;

    //Declare Global Variables
    private String fName = "", lName = "", age = "", address = "", phone = "", email = "", password = "", birthdate = "", displayOtpNum = "", enteredOtp = "", getOtpBackend = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hides OTP Authentication Layout - Shows Register Layout
        binding.registerLayout.setVisibility(View.VISIBLE);
        binding.otpLayout.setVisibility(View.GONE);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // Start Birthdate Fields
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.bdayET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = month + "/" + day + "/" + year;
                        binding.bdayET.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        // Birthdate Fields ends

        //handle back button, click
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(RegisterActivity.this, LandingActivity.class));
//                finish();
                onBackPressed();
            }
        });

        //handle has an account, click
        binding.hasAnAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                //onBackPressed();
            }
        });

        //handle register/continue click
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate entered fields first before sending OTP
                validateData();
            }
        });

        // Phone Authentication Block Starts
        // *Verification of entered OTP
        binding.submitOtpBtn.setOnClickListener(view -> {
            // Validate OTP input fields if empty
            if (!binding.inputotp1.getText().toString().trim().isEmpty()
                    && !binding.inputotp2.getText().toString().trim().isEmpty()
                    && !binding.inputotp3.getText().toString().trim().isEmpty()
                    && !binding.inputotp4.getText().toString().trim().isEmpty()
                    && !binding.inputotp5.getText().toString().trim().isEmpty()
                    && !binding.inputotp6.getText().toString().trim().isEmpty()) {
                // Store to a variable the OTP in the input field
                enteredOtp = binding.inputotp1.getText().toString() +
                        binding.inputotp2.getText().toString() +
                        binding.inputotp3.getText().toString() +
                        binding.inputotp4.getText().toString() +
                        binding.inputotp5.getText().toString() +
                        binding.inputotp6.getText().toString();

                // If saved otp variable is not empty
                if (getOtpBackend != null) {
                    binding.progressbarSubmitOtp.setVisibility(View.VISIBLE);
                    binding.submitOtpBtn.setVisibility(View.INVISIBLE);

                    // Compare the auth OTP entered from saved otp variable
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            getOtpBackend, enteredOtp
                    );
                    // Link phone credentials to the created firebase user with its email, password
                    verifyAuthentication(phoneAuthCredential);
//                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    binding.progressbarSubmitOtp.setVisibility(View.GONE);
//                                    binding.submitOtpBtn.setVisibility(View.VISIBLE);
//
//                                    if (task.isSuccessful()) {
//
//                                        createUserAccount();
//
//                                        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                        //startActivity(intent);
//                                    } else {
//                                        Toast.makeText(RegisterActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
                } else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                // Toast.makeText(this, "OTP verified", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter all the number", Toast.LENGTH_SHORT).show();
            }
        });

        // Resend OTP after 60 mins
        binding.resendOtpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+63" + displayOtpNum,
                        60, TimeUnit.SECONDS,
                        RegisterActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newOtpBackEnd, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                //super.onCodeSent(s, forceResendingToken);

                                getOtpBackend = newOtpBackEnd;
                                Toast.makeText(RegisterActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });

        inputOtpMove();
        // Phone Authentication Block Ends


    }

    //===== Functions + Methods =======
    private void validateData() {
        /*Validate data, before sending OTP | create an account*/

        //Get input data
        fName = binding.fNameET.getText().toString().trim();
        lName = binding.lNameET.getText().toString().trim();
        age = binding.ageET.getText().toString().trim();
        address = binding.addressET.getText().toString().trim();
        phone = binding.phoneET.getText().toString().trim();
        email = binding.emailET.getText().toString().trim();
        password = binding.passwordET.getText().toString().trim();
        birthdate = binding.bdayET.getText().toString().trim();
        String cPassword = binding.confirmPassET.getText().toString().trim();

        //validate input data
        if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(lName)) { //names are empty
            Toast.makeText(this, "Enter you name..", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { //invalid email pattern
            Toast.makeText(this, "Invalid email pattern", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) { //password is empty
            Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cPassword)) { //confirm password is empty
            Toast.makeText(this, "Confirm your password", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(cPassword)) { //password and confirm password doesn't match
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) { //password is less than 6 characters
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(age)) { //age is empty
            Toast.makeText(this, "Enter your age", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address)) { //address is empty
            Toast.makeText(this, "Enter your address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) { //phone is empty
            Toast.makeText(this, "Enter your phone", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(birthdate)) { //phone is empty
            Toast.makeText(this, "Enter your Birth Date", Toast.LENGTH_SHORT).show();
        } else if (!(phone.length() == 10)) { //phone must be 10 length long
            Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
        } else {
            // Display entered number onto OTP auth layout
            displayOtpNum = binding.phoneET.getText().toString().trim();
            binding.dispNumTV.setText("+63" + displayOtpNum);

            // Sending OTP after continue button
            continueToSendOtp();

            // Create user with email, password
            createUserAccount();
        }
    }

    private void continueToSendOtp() {
        binding.progressbarSendingOtp.setVisibility(View.VISIBLE);
        binding.registerBtn.setVisibility(View.INVISIBLE);

        // Send OTP code to the entered phone number
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+63" + binding.phoneET.getText().toString(),
                60, TimeUnit.SECONDS,
                RegisterActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        binding.progressbarSendingOtp.setVisibility(View.GONE);
                        binding.registerBtn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        binding.progressbarSendingOtp.setVisibility(View.GONE);
                        binding.registerBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String otpBackEnd, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        //super.onCodeSent(s, forceResendingToken);
                        binding.progressbarSendingOtp.setVisibility(View.GONE);
                        binding.registerBtn.setVisibility(View.VISIBLE);

                        // Saved OTP sent to a variable
                        getOtpBackend = otpBackEnd;

                        // Hide Register layout - Show OTP Auth layout / Shows OTP submit button
                        binding.registerLayout.setVisibility(View.GONE);
                        binding.otpLayout.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void inputOtpMove() {
        binding.inputotp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputotp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.inputotp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputotp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.inputotp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputotp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.inputotp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputotp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.inputotp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputotp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void verifyAuthentication(PhoneAuthCredential phoneAuthCredential) {
        // Inform user
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        firebaseAuth.getCurrentUser().linkWithCredential(phoneAuthCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Account linked with verified phone
                        // Insert details in Realtime Database
                        updateUserInfo();

                        // Inform account link success
                        Toast.makeText(RegisterActivity.this, "Account linked", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Inform account link failed
                        Toast.makeText(RegisterActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createUserAccount() {
        //show progress
        //progressDialog.setMessage("Creating account...");
        //progressDialog.show();

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Account create success
                        // x-updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //account create failed
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        String referCode = uid.substring(0, 7);

        //setup data to add in the realtime database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("fName", fName);
        hashMap.put("lName", lName);
        hashMap.put("age", age);
        hashMap.put("address", address);
        hashMap.put("email", email);
        hashMap.put("phone", phone);
        hashMap.put("birthdate", birthdate);
        hashMap.put("userType", "user");
        hashMap.put("timestamp", timestamp);
        hashMap.put("profileImage", "https://firebasestorage.googleapis.com/v0/b/dental-qmgmt-system.appspot.com/o/ProfileImages%2Fno-profile.png?alt=media&token=353ccad9-5f1b-41c7-a272-9c64821362ab"); //add empty, in case needed
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

                        //will proceed to user homepage
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                        LandingActivity.getInstance().finish();

                        // Inform user credentials saved
                        Toast.makeText(RegisterActivity.this, "Credentials Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //data adding failed
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}