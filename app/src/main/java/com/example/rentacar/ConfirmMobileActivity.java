package com.example.rentacar;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConfirmMobileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText verification_code;
    private Button verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_mobile);

        initComponents();

        verify.setOnClickListener(this);
    }

    private void initComponents() {
        verification_code = findViewById(R.id.conf_code);
        verify = findViewById(R.id.conf_btn);
    }

    @Override
    public void onClick(View v) {
        if (v == verify) {
            Intent intent = new Intent(ConfirmMobileActivity.this, Home.class);
            startActivity(intent);
            finish();
        }
    }
}
