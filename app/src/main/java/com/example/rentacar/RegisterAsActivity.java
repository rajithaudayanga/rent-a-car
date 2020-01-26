package com.example.rentacar;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.rentacar.Controllers.DatabaseFields;

public class RegisterAsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button as_customer, as_service;
    private String register_as;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as);

        initComponents();

        as_service.setOnClickListener(this);
        as_customer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == as_customer) {
            register_as = DatabaseFields.constants.USER_TYPE_CUSTOMER;
        } else if (v == as_service) {
            register_as = DatabaseFields.constants.USER_TYPE_SERVICE;
        }

        //set usertype to shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(DatabaseFields.sharedPrefferenceData.FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(DatabaseFields.userFields.USER_TYPE, register_as);
        editor.apply();


        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void initComponents() {
        as_customer = findViewById(R.id.register_as_customer);
        as_service = findViewById(R.id.register_as_service);
    }
}
