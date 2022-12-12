package com.example.dqms_admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddTimeMainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String dbTime;
    private TextView textView;
        /*Todo
           Connect Database on main system - Major
           Troubleshoot incoming bugs
              */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_main);
        textView = findViewById(R.id.textView);

        databaseReference = FirebaseDatabase.getInstance("https://queuesystem-7e0c3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("dateTimer");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long time = Long.parseLong(dataSnapshot.child("userTime2").child("timeStamp").getValue().toString());
//                    textView.setText(time);
                buttonIdentify(time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Failed");
            }
        });
    }

    private void getTime(long time, long givenTime){
        databaseReference.child("userTime2").child("newTimeStamp").setValue(givenTime);
    }

    private void buttonIdentify(long time){
        Button b30, b10, b5;
        b30 = findViewById(R.id.button30);
        b10 = findViewById(R.id.button10);
        b5 = findViewById(R.id.button5);

        b30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add function, convert string to int -> int to milliseconds -> add 30 mins(in milli) -> convert back to string
                getTime(time, 1800000);
            }
        });

        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add function, convert string to int -> int to milliseconds -> add 30 mins(in milli) -> convert back to string
                getTime(time, 600000);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add function, convert string to int -> int to milliseconds -> add 30 mins(in milli) -> convert back to string
                getTime(time, 300000);
            }
        });
    }

}