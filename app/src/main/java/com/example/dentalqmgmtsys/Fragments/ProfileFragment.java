package com.example.dentalqmgmtsys.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dentalqmgmtsys.AboutUsActivity;
import com.example.dentalqmgmtsys.ContactUsActivity;
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

public class ProfileFragment extends Fragment {

    //Viewbinding
    private FragmentProfileBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

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



    }

    //private static final String TAG = "PROFILE_TAG";

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
                        String fullName = fName+" "+lName;

                        //format date dd/MM/yyyy
                        String formattedDate = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        //set data
                        binding.fnameTV.setText(fName);
                        binding.fullnameTV.setText(fullName);
                        binding.emailTV.setText(email);
                        binding.memberDateTV.setText(formattedDate);
                        binding.addressTV.setText(address);
                        //binding.phone.setText(phone);
                        //binding.age.setText(age);

                        //set image??
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

//    private void checkUser() {
//        // Get current user
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        if (firebaseUser != null){
//            //logged in, get user info
//            String email = firebaseUser.getEmail();
//
//            //set in textView
//            binding.fnameTV.setText(email);
//        }
//        else {
//            startActivity(new Intent(getActivity(), LandingActivity.class));
//            getActivity().finish();
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}