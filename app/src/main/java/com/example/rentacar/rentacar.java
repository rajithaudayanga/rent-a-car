package com.example.rentacar;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class rentacar extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
