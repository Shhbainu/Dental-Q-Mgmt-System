package com.example.dentalqmgmtsys.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dentalqmgmtsys.AboutUsActivity;
import com.example.dentalqmgmtsys.ContactUsActivity;
import com.example.dentalqmgmtsys.EditProfileActivity;
import com.example.dentalqmgmtsys.LandingActivity;
import com.example.dentalqmgmtsys.MyApplication;
import com.example.dentalqmgmtsys.R;
import com.example.dentalqmgmtsys.ReferralActivity;
import com.example.dentalqmgmtsys.TermsConditionsActivity;
import com.example.dentalqmgmtsys.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    //Viewbinding
    FragmentProfileBinding binding;

    //firebase auth
    FirebaseAuth firebaseAuth;

    //firebase currentUser
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        databaseReference.child(currentUser.getUid())
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
                        //String phone = ""+snapshot.child("phone").getValue();
                        String age = ""+snapshot.child("age").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();

                        //format date dd/MM/yyyy
                        String formattedDate = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        //set data
                        binding.fnameTV.setText(fName);
                        binding.firstNameTV.setText(fName);
                        binding.lastNameTV.setText(lName);
                        binding.emailTV.setText(email);
                        binding.memberDateTV.setText(formattedDate);
                        //binding.contactTV.setText(phone);
                        binding.addressTV.setText(address);
                        //binding.phone.setText(phone);
                        //binding.age.setText(age);
                        Picasso.get()
                                .load(profileImage)
                                .placeholder(R.drawable.ico_no_pic)
                                .error(R.drawable.ico_no_pic)
                                .into(binding.profileTV);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //checkUser();

        //handle logout, click
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity() , LandingActivity.class));
                getActivity().finish();
                onDestroyView();
                //checkUser();
            }
        });

        //handle referral click
        binding.referralActTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ReferralActivity.class));
            }
        });

        //handle terms and condition click
        binding.termsconditionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), TermsConditionsActivity.class));
            }
        });

        //handle about us click
        binding.AboutUsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
            }
        });

        //handle contact us click
        binding.ContactUsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ContactUsActivity.class));
            }
        });

        //handle edit profile click
        binding.editProfileTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get text

                String fName = binding.fnameTV.getText().toString();
                String lName = binding.lastNameTV.getText().toString();
                String address = binding.addressTV.getText().toString();
                //String contactNum = binding.contactTV.getText().toString();
                String email = binding.emailTV.getText().toString();

                //putExtra
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("profile", (R.id.profileTV));
                intent.putExtra("fName", String.valueOf(fName));
                intent.putExtra("lName", String.valueOf(lName));
                intent.putExtra("address", String.valueOf(address));
                //intent.putExtra("contactNum", String.valueOf(contactNum));
                intent.putExtra("email", String.valueOf(email));
                startActivity(intent);
            }
        });

    }
}