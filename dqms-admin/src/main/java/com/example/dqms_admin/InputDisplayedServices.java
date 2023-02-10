package com.example.dqms_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.dqms_admin.Model.ServicesOffered;
import com.example.dqms_admin.databinding.ActivityInputDisplayedServicesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class InputDisplayedServices extends AppCompatActivity {

    //Viewbinding
    private ActivityInputDisplayedServicesBinding binding;

    Uri ImageUri;

    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputDisplayedServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance("https://dental-qmgmt-system-default-rtdb.asia-southeast1.firebasedatabase.app/");
        firebaseStorage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setTitle("Service Uploading");
        dialog.setCanceledOnTouchOutside(false);


        binding.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();
                binding.relative.setVisibility(View.VISIBLE);
                binding.uploadBtn.setVisibility(View.GONE);
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

                final StorageReference reference = firebaseStorage.getReference().child("service")
                        .child(System.currentTimeMillis()+"");

                reference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ServicesOffered model = new ServicesOffered();
                                model.setServiceImage(uri.toString());

                                model.setHeadline(binding.headline.getText().toString());
                                model.setDescription(binding.description.getText().toString());
                                model.setPriceRange(binding.priceRange.getText().toString());

                                database.getReference().child("service").push().setValue(model)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Toast.makeText(InputDisplayedServices.this, "Service Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(InputDisplayedServices.this, DisplayServiceActivity.class));
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                Toast.makeText(InputDisplayedServices.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        });
                    }
                });
            }
        });


    }

    private void UploadImage() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 101);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(InputDisplayedServices.this, "Permission Denied", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();

                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK){
            ImageUri = data.getData();
            binding.serviceImage.setImageURI(ImageUri);
        }
    }
}