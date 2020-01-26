package com.example.rentacar;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText email, password;
    private TextView forgot_password, dont_have_account;
    private Button login;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    private SweetAlertDialog sweetAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();

        //set listeners
        dont_have_account.setOnClickListener(this);
        login.setOnClickListener(this);
        forgot_password.setOnClickListener(this);

        // progress dialog
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Wait...");
        sweetAlertDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        if (v == dont_have_account) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        } else if (v == login) {
            login();
        }
    }

    private void login() {

        String user_email = email.getText().toString().trim();
        String user_password = password.getText().toString();

        if (TextUtils.isEmpty(user_email)) {
            email.setError("Please enter email");
            return;
        }

        if (TextUtils.isEmpty(user_password)) {
            password.setError("Please enter password");
            return;
        }


        sweetAlertDialog.show();
        mAuth.signInWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final String current_uid = mAuth.getCurrentUser().getUid();

                    mDatabaseReference.child(current_uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            final String profile_level = dataSnapshot.child(DatabaseFields.userFields.PROFILE_LEVEL).getValue(String.class);
                            final String user_type = dataSnapshot.child(DatabaseFields.userFields.USER_TYPE).getValue(String.class);


                            String device_token = FirebaseInstanceId.getInstance().getToken();

                            mDatabaseReference.child(current_uid).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()) {
                                        int pro_level = 0;
                                        if (profile_level != null) {
                                            pro_level = Integer.parseInt(profile_level);
                                        }
                                        switch (pro_level) {
                                            case 1:
                                                if (user_type.equals(DatabaseFields.constants.USER_TYPE_CUSTOMER)) {
                                                    startActivity(new Intent(LoginActivity.this, RegisterCustomerActivity.class));
                                                } else if (user_type.equals(DatabaseFields.constants.USER_TYPE_SERVICE)) {
                                                    startActivity(new Intent(LoginActivity.this, RegisterServiceActivity.class));
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
                                                startActivity(new Intent(LoginActivity.this, Home.class));
                                                finish();
                                                break;

                                            case 3:
                                                startActivity(new Intent(LoginActivity.this, Home.class));
                                                finish();
                                                break;
                                            default:
                                                sweetAlertDialog.dismissWithAnimation();
                                        }
                                    }else {
                                        sweetAlertDialog.dismissWithAnimation();
                                        Toasty.info(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                } else {
                    sweetAlertDialog.dismissWithAnimation();
                    Toasty.error(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initComponents() {
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        forgot_password = findViewById(R.id.login_forgot_password);
        dont_have_account = findViewById(R.id.login_dont_have_account);
        login = findViewById(R.id.login_with_email);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sweetAlertDialog.dismiss();
    }
}
