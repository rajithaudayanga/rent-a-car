package com.example.rentacar;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.Gravity;
import android.view.MenuItem;

import com.example.rentacar.RoomDatabase.AppDatabase;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentacar.BroadcastReceivers.NetworkStateChangeReceiver;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Fragments.BookingsFragment;
import com.example.rentacar.Fragments.CustomerProfileFragment;
import com.example.rentacar.Fragments.ManageVehiclesFragment;
import com.example.rentacar.Fragments.MessageFragment;
import com.example.rentacar.Fragments.NotificationFragment;
import com.example.rentacar.Fragments.RentalsFragment;
import com.example.rentacar.Fragments.SearchFragment;
import com.example.rentacar.Fragments.ServiceProfileFragment;
import com.example.rentacar.Model.IOnBackPressed;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private String user_type;
    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private LinearLayout linearLayout;
    private TextView textView;
    public static AppDatabase appDatabase;
    private DrawerLayout drawer;
    private SensorEventListener listener;
    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "rent_a_car").allowMainThreadQueries().build();

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseReference.keepSynced(true);

        //user type
        SharedPreferences sharedPreferences = getSharedPreferences(DatabaseFields.sharedPrefferenceData.FILE_NAME, MODE_PRIVATE);
        user_type = sharedPreferences.getString(DatabaseFields.userFields.USER_TYPE, "");

        //hide navigation drawer items
        if (user_type.equals(DatabaseFields.constants.USER_TYPE_CUSTOMER)) {
            Menu navigationMenu = navigationView.getMenu();
            navigationMenu.findItem(R.id.nav_manage_vehicles).setVisible(false);
        }

        //navigation drawer components
        View drawer_header = navigationView.getHeaderView(0);
        final TextView drawer_name = drawer_header.findViewById(R.id.drawer_name);
        final TextView drawer_email = drawer_header.findViewById(R.id.drawer_email);

        //navigation drawer content change
        if (mFirebaseAuth.getCurrentUser() != null) {
            mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    drawer_name.setText(dataSnapshot.child("name").getValue(String.class));
                    drawer_email.setText(mFirebaseAuth.getCurrentUser().getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            drawer_name.setText(getResources().getString(R.string.main_name));
            drawer_email.setVisibility(View.GONE);
        }

        // load fragment on start
        if (user_type.equals(DatabaseFields.constants.USER_TYPE_SERVICE)) {
            loadFragment(new ServiceProfileFragment());
            getSupportActionBar().setTitle(getResources().getString(R.string.profile));
        } else if (user_type.equals(DatabaseFields.constants.USER_TYPE_CUSTOMER)) {
            loadFragment(new SearchFragment());
            getSupportActionBar().setTitle(getResources().getString(R.string.find_service));
        }

        //error layout
        linearLayout = findViewById(R.id.notice_layout);
        textView = findViewById(R.id.connection_status);

        //check connection
        networkStateChangeReceiver = new NetworkStateChangeReceiver(getApplicationContext(), textView, linearLayout);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateChangeReceiver, intentFilter);

        //setup sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        tilt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_content_frame);
            if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you want to exit ?")
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Home.super.onBackPressed();
                                sDialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_logout:
                new MessageFragment().onPause();
                mFirebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));

                //delete shared preferred file
                SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Toasty.success(this, "You logged out", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.nav_profile:
                getSupportActionBar().setTitle(getResources().getString(R.string.profile));
                if (user_type.equals(DatabaseFields.constants.USER_TYPE_CUSTOMER)) {
                    loadFragment(new CustomerProfileFragment());
                } else if (user_type.equals(DatabaseFields.constants.USER_TYPE_SERVICE)) {
                    loadFragment(new ServiceProfileFragment());
                }
                break;
            case R.id.nav_find_service:
                getSupportActionBar().setTitle(getResources().getString(R.string.find_service));
                loadFragment(new SearchFragment());
                break;
            case R.id.nav_manage_vehicles:
                getSupportActionBar().setTitle(getResources().getString(R.string.manage_vehicles));
                loadFragment(new ManageVehiclesFragment());
                break;
            case R.id.nav_booking:
                getSupportActionBar().setTitle(getResources().getString(R.string.bookings));
                loadFragment(new BookingsFragment());
                break;
            case R.id.nav_notification:
                getSupportActionBar().setTitle(getResources().getString(R.string.notifications));
                loadFragment(new NotificationFragment());
                break;
            case R.id.nav_message:
                getSupportActionBar().setTitle(getResources().getString(R.string.messages));
                loadFragment(new MessageFragment());
                break;

            case R.id.nav_rent_history:
                getSupportActionBar().setTitle("Rent History");
                loadFragment(new RentalsFragment());
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.home_content_frame, fragment).commit();
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateChangeReceiver);
    }

    public void tilt() {
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                if (Math.abs(x) > Math.abs(y)) {
                    if (x < -5) {
                        System.out.println("right");
                        drawer.openDrawer(Gravity.LEFT, true);
                    }
                    if (x > +5) {
                        System.out.println("right");
                        drawer.closeDrawer(Gravity.LEFT, true);
                    }
//                } else {
//                    if (y < 0) {
////                        Toast.makeText(getApplicationContext(), "You tilt the device up", Toast.LENGTH_SHORT).show();
//
//                    }
//                    if (y > 0) {
////                        Toast.makeText(getApplicationContext(), "You tilt the device down", Toast.LENGTH_SHORT).show();
////
//                    }
                }
//                if (x > (-2) && x < (2) && y > (-2) && y < (2)) {
////                    Toast.makeText(getApplicationContext(), "You tilt the device no tilt", Toast.LENGTH_SHORT).show();
//
//                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }
}
