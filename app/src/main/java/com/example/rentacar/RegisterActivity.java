package com.example.rentacar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentacar.Controllers.DatabaseFields;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password, repeat_password;
    private TextView already_have_account;
    private Button register;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    private String register_as;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initComponents();

        mAuth = FirebaseAuth.getInstance();


        //get user type
        SharedPreferences sharedPreferences = getSharedPreferences(DatabaseFields.sharedPrefferenceData.FILE_NAME, MODE_PRIVATE);
        register_as = sharedPreferences.getString(DatabaseFields.userFields.USER_TYPE, "");

        if (register_as.equalsIgnoreCase("")) {
            startActivity(new Intent(RegisterActivity.this, RegisterAsActivity.class));
        }

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));


        already_have_account.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == already_have_account) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (v == register) {
            registerWithEmail();
        }
    }

    private void registerWithEmail() {
        String user_email = email.getText().toString().trim();
        String user_password = password.getText().toString();
        String user_repeat = repeat_password.getText().toString();

        if (TextUtils.isEmpty(user_email)) {
            Toasty.info(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(user_password)) {
            Toasty.info(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(user_repeat)) {
            Toasty.info(this, "Please enter repeat password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!user_password.equals(user_repeat)) {
            Toasty.error(this, "Passwords not match", Toast.LENGTH_SHORT).show();
        } else {
            pDialog.setTitleText("Loading");
            pDialog.setCancelable(false);
            pDialog.show();

            //create user
            mAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        HashMap<String, String> user_data = new HashMap<>();
                        user_data.put(DatabaseFields.userFields.USER_TYPE, register_as);
                        user_data.put(DatabaseFields.userFields.PROFILE_LEVEL, "1");

                        //set user profile level
                        final String current_id = mAuth.getCurrentUser().getUid();

                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
                        mDatabaseReference.keepSynced(true);
                        mDatabaseReference.child(current_id).setValue(user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()) {

                                    String device_token = FirebaseInstanceId.getInstance().getToken();

                                    mDatabaseReference.child(current_id).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isComplete()){
                                                //register as customer
                                                if (register_as.equals(DatabaseFields.constants.USER_TYPE_CUSTOMER)) {
                                                    startActivity(new Intent(RegisterActivity.this, RegisterCustomerActivity.class));

                                                    //register as a service
                                                } else if (register_as.equals(DatabaseFields.constants.USER_TYPE_SERVICE)) {
                                                    startActivity(new Intent(RegisterActivity.this, RegisterServiceActivity.class));
                                                }
                                                pDialog.dismiss();
                                                finish();
                                            }else {
                                                Toasty.info(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                } else {
                                    String message = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                    pDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        pDialog.dismiss();
                        String message = Objects.requireNonNull(task.getException()).getMessage();
                        Toasty.error(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initComponents() {
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        repeat_password = findViewById(R.id.register_conf_password);
        already_have_account = findViewById(R.id.register_already_have_account);
        register = findViewById(R.id.register_with_email_btn);
    }
}
