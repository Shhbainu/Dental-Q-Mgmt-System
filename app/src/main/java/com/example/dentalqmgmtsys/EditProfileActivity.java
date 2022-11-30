package com.example.dentalqmgmtsys;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dentalqmgmtsys.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    //view binding
    private ActivityEditProfileBinding binding;

    //firebase auth, get/update user data using uid
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    private static  final String TAG = "PROFILE_EDIT_TAG";

    private Uri imageUri = null;

    private String fName = "";
    private String lName = "";
    private String address = "";
    private String phone = "";
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false); //don't dismiss while clicking outside


        //setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        //handle back button, click
        ImageView back = findViewById(R.id.backBtn);

        //Event onClick for back button
        back.setOnClickListener(v -> finish());

        //handle click, pick image
        binding.profileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageAttachMenu();
            }
        });

        //handle click update profile
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }
    private void loadUserInfo() {
        //Log.d(TAG, "loadUserInfo: Loading user info of user"+firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Get all info of user here from snapshot
                        String email = ""+snapshot.child("email").getValue();
                        String fName = ""+snapshot.child("fName").getValue();
                        String lName = ""+snapshot.child("lName").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String userType = ""+snapshot.child("userType").getValue();
                        String address = ""+snapshot.child("address").getValue();
                        String phone = ""+snapshot.child("phone").getValue();
                        String age = ""+snapshot.child("age").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String fullName = fName+" "+lName;

                        //set data
                        binding.fNameET.setText(fName);
                        binding.lNameET.setText(lName);
                        binding.fullnameET.setText(fullName);
                        binding.emailET.setText(email);
                        binding.addressET.setText(address);
                        binding.phoneET.setText(phone);
                        Glide.with(EditProfileActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ico_no_pic)
                                .error(R.drawable.ico_no_pic)
                                .into(binding.profileTV);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void validateData(){
        //get data
        fName = binding.fNameET.getText().toString().trim();
        lName = binding.lNameET.getText().toString().trim();
        address = binding.addressET.getText().toString().trim();
        phone = binding.phoneET.getText().toString().trim();
        email = binding.emailET.getText().toString().trim();

        //validate data
        if(TextUtils.isEmpty(fName)){
            //no name is entered
            Toast.makeText(this, "Enter first name...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(lName)){
            //no name is entered
            Toast.makeText(this, "Enter last name...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(address)){
            //no name is entered
            Toast.makeText(this, "Enter address...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone)){
            //no name is entered
            Toast.makeText(this, "Enter phone number...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email)){
            //no name is entered
            Toast.makeText(this, "Enter email...", Toast.LENGTH_SHORT).show();
        }
        else{
            //name is entered
            if(imageUri == null){
                //need to update without image
                updateProfile("");
            }
            else {
                //need to update with image
                uploadImage();
            }
        }
    }

    private void uploadImage(){
        Log.d(TAG, "uploadImage: Uploading profile image");
        progressDialog.setMessage("Updating profile image");
        progressDialog.show();

        //image path and name, use uid to replace prev0ious
        String filePathAndName = "ProfileImages/"+firebaseAuth.getUid();

        //storage reference
        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Profile image uploaded");
                        Log.d(TAG, "onSuccess: Getting url of uploaded image");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedImageUrl = ""+uriTask.getResult();

                        Log.d(TAG, "onSuccess: Uploaded Image URL: "+uploadedImageUrl);
                        updateProfile(uploadedImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to upload image due to"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Failed to upload image due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfile(String imageUrl){
        Log.d(TAG, "updateProfile: Updating user profile");
        progressDialog.setMessage("Updating user profile...");
        progressDialog.show();

        //setup data to update in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fName", ""+fName);
        hashMap.put("lName", ""+lName);
        hashMap.put("address", ""+address);
        hashMap.put("phone", ""+phone);
        hashMap.put("email", ""+email);
        if (imageUri != null){
            hashMap.put("profileImage", ""+imageUrl);
        }

        //update data to db
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Profile updated...");
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Profile updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to update due to"+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Failed to update due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImageAttachMenu(){
        //init setup popup menu
        PopupMenu popupMenu = new PopupMenu(this, binding.profileTV);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

        popupMenu.show();

        //handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //get id of clicked
                int which = menuItem.getItemId();
                if (which==0){
                    //camera clicked
                    pickImageCamera();
                }
                else if (which==1){
                    //gallery clicked
                    pickImageGallery();
                }
                return false;
            }
        });
    }

    private void pickImageCamera(){
        //intent to pick from camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private void pickImageGallery(){
        //intent to pick from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent>cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of camera intent
                    //get uri of image
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult: Picked From Camera" +imageUri);
                        Intent data = result.getData();

                        binding.profileTV.setImageURI(imageUri);
                    }
                    else{
                        Toast.makeText(EditProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent>galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of gallery intent
                    //get uri of image
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult:" +imageUri);
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: Picked From Gallery" +imageUri);
                        binding.profileTV.setImageURI(imageUri);
                    }
                    else{
                        Toast.makeText(EditProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}