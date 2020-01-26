package com.example.rentacar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.BookingDetails;
import com.example.rentacar.Model.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class CustomerAvailabilityCheckActivity extends AppCompatActivity implements android.app.DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private TextView check_from, check_to;
    private Button check_btn;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    private SweetAlertDialog sweetAlertDialog;

    private String service_id, vehicle_id, user_name, vehicle_name;
    private int clicked;
    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_availability_check);
        initComponents();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mFirebaseAuth = FirebaseAuth.getInstance();

        //dialog
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText("Waiting...");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            service_id = bundle.getString("service_id");
            vehicle_id = bundle.getString("vehicle_id");

            mDatabaseReference.child(service_id).child("vehicles").child(vehicle_id).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    vehicle_name = dataSnapshot.getValue(String.class);
                    textView1.setText(textView1.getText().toString() + "\n" + vehicle_name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_name = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        check_btn.setOnClickListener(this);
        check_to.setOnClickListener(this);
        check_from.setOnClickListener(this);
    }

    private void initComponents() {
        check_to = findViewById(R.id.check_to);
        check_from = findViewById(R.id.check_from);
        check_btn = findViewById(R.id.check_btn);
        textView1 = findViewById(R.id.textView1);
    }


    @Override
    public void onClick(View v) {
        if (v == check_btn) {
            checkAvailable();
        } else if (v == check_to) {
            clicked = 1;
            com.example.rentacar.DatePickerDialog datePickerDialog = new com.example.rentacar.DatePickerDialog();
            datePickerDialog.show(getSupportFragmentManager(), "to");
        } else if (v == check_from) {
            clicked = 2;
            com.example.rentacar.DatePickerDialog datePickerDialog = new com.example.rentacar.DatePickerDialog();
            datePickerDialog.show(getSupportFragmentManager(), "from");
        }
    }

    private void checkAvailable() {

        if (check_from.getText().toString().equalsIgnoreCase("from") || check_to.getText().toString().equalsIgnoreCase("to")) {
            Toasty.info(this, "Please select date you want!", Toast.LENGTH_SHORT).show();
            return;
        }

        sweetAlertDialog.show();

        Notification notification = new Notification();
        notification.setNotification_from(mFirebaseAuth.getCurrentUser().getUid());
        notification.setNotification_type(DatabaseFields.notification.NOTIFICATION_TYPE_CHECK_VEHICLE);
        notification.setNotification_text(user_name + " ask " + vehicle_name + " available from " + check_from.getText().toString() + " to " + check_to.getText().toString());
        notification.setDate(new Date());
        notification.setStatus(DatabaseFields.notification.NOTIFICATION_STATUS_UNREAD);
        notification.setBookingDetails(new BookingDetails(
                null,
                mFirebaseAuth.getCurrentUser().getUid(),
                vehicle_id,
                check_from.getText().toString(),
                check_to.getText().toString(),
                service_id,
                null,
                DatabaseFields.booking.PENDING_APPROVEL));
        notification.setNotification_title("Check Vehicle Availability");

        mDatabaseReference.child(service_id).child("notifications").push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sweetAlertDialog.dismissWithAnimation();

                    Toasty.success(CustomerAvailabilityCheckActivity.this, "Congrats.! Your request sent to the service. Please waiting for the response.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    finish();
                } else {
                    sweetAlertDialog
                            .setTitleText("Oops!")
                            .setContentText(task.getException().getMessage())
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String text = simpleDateFormat.format(calendar.getTime());

        if (clicked == 1) {
            check_to.setText(text);
        } else if (clicked == 2) {
            check_from.setText(text);
        }
    }
}
