package com.example.rentacar;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;


import com.example.rentacar.Model.BookingDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerBookingDetailsActivity extends AppCompatActivity {

    private TextView vehicle_name, ser_name, brand, price, start_date, end_date;
    private Button about_vehicle, cus_profile;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_booking_details);

        initComponents();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mFirebaseAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            final String booking_id = bundle.getString("booking_id");

            mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("bookings").child(booking_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final BookingDetails bookingDetails = dataSnapshot.getValue(BookingDetails.class);

                    if (bookingDetails != null) {
                        price.setText(bookingDetails.getPrice());
                        start_date.setText(bookingDetails.getFrom());
                        end_date.setText(bookingDetails.getTo());

                        mDatabaseReference.child(bookingDetails.getService_id()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String service_name = dataSnapshot.child("name").getValue(String.class);
                                ser_name.setText(service_name);

                                String vehi_name = dataSnapshot.child("vehicles").child(bookingDetails.getVehicle_id()).child("name").getValue(String.class);
                                String bra =  dataSnapshot.child("vehicles").child(bookingDetails.getVehicle_id()).child("brand").getValue(String.class);

                                vehicle_name.setText(vehi_name);
                                brand.setText(bra);
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
    }

    private void initComponents() {
        vehicle_name = findViewById(R.id.cus_booking_vehicle_name);
        ser_name = findViewById(R.id.cus_booking_service_name);
        brand = findViewById(R.id.cus_booking_brand);
        price = findViewById(R.id.cus_booking_price);
        start_date = findViewById(R.id.cus_booking_from);
        end_date = findViewById(R.id.cus_booking_to);
        about_vehicle = findViewById(R.id.booking_see_more);
        cus_profile = findViewById(R.id.booking_cus_profile);
    }
}
