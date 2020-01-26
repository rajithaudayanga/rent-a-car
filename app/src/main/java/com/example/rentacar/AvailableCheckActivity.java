package com.example.rentacar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.Date;

import es.dmoral.toasty.Toasty;

public class AvailableCheckActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView description;
    private Button available, not_available, cus_profile, vehicle_details;

    private String notification_id, vehicle_name;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;
    private Notification notification;
    private String vehicle_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avilable_check);

        initComponents();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            notification_id = bundle.getString("notification_id");

            mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("notifications").child(notification_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notification = dataSnapshot.getValue(Notification.class);

                    if (notification != null) {
                        description.setText(notification.getNotification_text());

                        mDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("vehicles").child(notification.getBookingDetails().getVehicle_id()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                vehicle_name = dataSnapshot.child("name").getValue(String.class);
                                vehicle_price = dataSnapshot.child("price_per_day").getValue(String.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        available.setOnClickListener(this);
        not_available.setOnClickListener(this);
        cus_profile.setOnClickListener(this);
        vehicle_details.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == available) {
            setAvailable(1);
        } else if (v == not_available) {
            setAvailable(0);
        }
    }

    private void setAvailable(int i) {
        String vehicle_avilability = null, notification_type = null, notification_text = null, toast_text = null;
        String notification_title = null;

        if (i == 0) {
            vehicle_avilability = DatabaseFields.constants.VEHICLE_AVAILABILITY_NOT_AVAILABLE;
            notification_type = DatabaseFields.notification.NOTIFICATION_TYPE_RESPONSE_NOT_AVAILABLE_VEHICLE;
            notification_text = "Oops! You requested " + vehicle_name + "is currently not available. You can try another one";
            toast_text = "Vehicle successfully marked as not available.";
            notification_title = "Vehicle not Available";
        } else if (i == 1) {
            vehicle_avilability = DatabaseFields.constants.VEHICLE_AVAILABILITY_AVAILABLE;
            notification_type = DatabaseFields.notification.NOTIFICATION_TYPE_RESPONSE_AVAILABLE_VEHICLE;
            notification_text = "Congratulations! You requested " + vehicle_name + "is available. You can book now.";
            toast_text = "Vehicle successfully marked as available.";
            notification_title = "Vehicle Available";
        }

        final String finalNotification_text = notification_text;
        final String finalNotification_type = notification_type;

        final String finalToast_text = toast_text;
        final String finalNotification_title = notification_title;
        mDatabaseReference
                .child(firebaseAuth.getCurrentUser().getUid())
                .child("vehicles")
                .child(notification.getBookingDetails().getVehicle_id())
                .child(DatabaseFields.vehicleFields.AVAILABILITY)
                .setValue(vehicle_avilability)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Notification new_notification = new Notification(
                                    firebaseAuth.getCurrentUser().getUid(),
                                    finalNotification_title,
                                    finalNotification_text,
                                    finalNotification_type,
                                    new Date(),
                                    DatabaseFields.notification.NOTIFICATION_STATUS_UNREAD,
                                    new BookingDetails(
                                            null,
                                            firebaseAuth.getCurrentUser().getUid(),
                                            notification.getBookingDetails().getVehicle_id(),
                                            notification.getBookingDetails().getFrom(),
                                            notification.getBookingDetails().getTo(),
                                            notification.getBookingDetails().getService_id(),
                                            vehicle_price,
                                            DatabaseFields.booking.PENDING_APPROVEL));

                            mDatabaseReference.child(notification.getNotification_from()).child("notifications").push().setValue(new_notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toasty.success(AvailableCheckActivity.this, finalToast_text, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AvailableCheckActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(AvailableCheckActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initComponents() {
        description = findViewById(R.id.ca_description);
        available = findViewById(R.id.ca_available);
        not_available = findViewById(R.id.ca_not_available);
        cus_profile = findViewById(R.id.ca_description_customer_profile);
        vehicle_details = findViewById(R.id.ca_vehicle_details);

    }
}
