package com.example.rentacar;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rentacar.BroadcastReceivers.NetworkStateChangeReceiver;
import com.example.rentacar.Controllers.DatabaseFields;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash_Screen extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private NetworkStateChangeReceiver networkStateChangeReceiver;

    private LinearLayout linearLayout;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);

        linearLayout = findViewById(R.id.notice_layout);
        textView = findViewById(R.id.connection_status);

        networkStateChangeReceiver = new NetworkStateChangeReceiver(getApplicationContext(), textView, linearLayout);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateChangeReceiver, intentFilter);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mFirebaseAuth.getCurrentUser() != null) {
                    final String currentID = mFirebaseAuth.getCurrentUser().getUid();

                    mDatabaseReference.child(currentID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String user_type = dataSnapshot.child(DatabaseFields.userFields.USER_TYPE).getValue(String.class);
                            String level = dataSnapshot.child(DatabaseFields.userFields.PROFILE_LEVEL).getValue(String.class);
                            int profile_level = 0;
                            if (level != null && !level.equals("")) {
                                profile_level = Integer.parseInt(level);
                            }

                            // 0 - new user
                            // 1 - complete sign in with email
                            // 2 - complete detail registration
                            // 3 - complete registration process

                            switch (profile_level) {
                                case 0:
                                    Intent intent = new Intent(getApplicationContext(), RegisterAsActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case 1:
                                    if (user_type.equals(DatabaseFields.constants.USER_TYPE_CUSTOMER)) {
                                        startActivity(new Intent(Splash_Screen.this, RegisterCustomerActivity.class));
                                    } else if (user_type.equals(DatabaseFields.constants.USER_TYPE_SERVICE)) {
                                        startActivity(new Intent(Splash_Screen.this, RegisterServiceActivity.class));
                                    }
                                    finish();
                                    break;
                                case 2:

                                    //set usertype to shared preferences
                                    SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();

                                    if (!sharedPreferences.contains(DatabaseFields.userFields.USER_TYPE)) {
                                        editor.putString(DatabaseFields.userFields.USER_TYPE, user_type);
                                        editor.apply();
                                    }
                                    startActivity(new Intent(Splash_Screen.this, Home.class));
                                    finish();
                                    break;

                                case 3:
                                    startActivity(new Intent(Splash_Screen.this, Home.class));
                                    finish();
                                    break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Intent intent = new Intent(getApplicationContext(), RegisterAsActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateChangeReceiver);
    }
}
