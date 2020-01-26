package com.example.rentacar;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.BookingDetails;
import com.example.rentacar.Model.Customer;
import com.example.rentacar.Model.Notification;
import com.example.rentacar.Model.Rent;
import com.example.rentacar.Model.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class ServiceBookingDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView name, mobile, call, req_date, return_date, vehicle_name, brand, model;
    private Button cus_profile, vehicle_detail, accept, decline;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private String mobile_num;
    private BookingDetails bookingDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_booking_details);

        initComponents();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mFirebaseAuth = FirebaseAuth.getInstance();

        call.setOnClickListener(this);
        accept.setOnClickListener(this);
        decline.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String text = bundle.getString("booking_obj");
            Gson gson = new Gson();

            bookingDetails = gson.fromJson(text, BookingDetails.class);


            req_date.setText(bookingDetails.getFrom());
            return_date.setText(bookingDetails.getTo());

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://rent-a-car-d9f64.firebaseio.com/Users/" + bookingDetails.getBooked_by() + ".json";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("name")) {
                                    name.setText(jsonObject.getString("name"));
                                }

                                if (jsonObject.has("mobile")) {
                                    mobile.setText(jsonObject.getString("mobile"));
                                }

                                if (jsonObject.has("name")) {
                                    mobile_num = jsonObject.getString("name");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            String url2 = "https://rent-a-car-d9f64.firebaseio.com/Users/" + mFirebaseAuth.getCurrentUser().getUid() + "/vehicles/" + bookingDetails.getVehicle_id() + ".json";
            StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url2,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("name")) {
                                    vehicle_name.setText(jsonObject.getString("name"));
                                }

                                if (jsonObject.has("brand")) {
                                    brand.setText(jsonObject.getString("brand"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(stringRequest);
            queue.add(stringRequest1);

        }
    }

    @Override
    public void onClick(View v) {
        if (v == call) {
            dialCall(mobile_num);
        } else if (v == cus_profile) {
            Intent intent = new Intent(this, TempActivity.class);
            intent.putExtra("action", "cus_details");
            intent.putExtra("cus_id", bookingDetails.getBooked_by());
            startActivity(intent);
        } else if (v == vehicle_detail) {

        } else if (v == accept) {
            acceptBooking();
        } else if (v == decline) {

        }
    }

    private void acceptBooking() {
        String rent_id = mDatabaseReference.child(bookingDetails.getService_id()).child("rentals").push().getKey();

        Rent rent = new Rent(
                rent_id,
                bookingDetails.getPrice(),
                bookingDetails.getFrom(),
                bookingDetails.getTo(),
                bookingDetails.getVehicle_id(),
                bookingDetails.getService_id(),
                bookingDetails.getBooked_by(),
                DatabaseFields.rentals.ON_GONING
        );

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put(bookingDetails.getService_id() + "/rentals/" + rent_id, rent);
        stringObjectHashMap.put(bookingDetails.getBooked_by() + "/rentals/" + rent_id, rent);
        stringObjectHashMap.put(bookingDetails.getService_id() + "/bookings/" + bookingDetails.getId() + "/status", DatabaseFields.booking.APPROVED);
        stringObjectHashMap.put(bookingDetails.getBooked_by() + "/bookings/" + bookingDetails.getId() + "/status", DatabaseFields.booking.APPROVED);

        mDatabaseReference.updateChildren(stringObjectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Notification notification = new Notification(
                            bookingDetails.getService_id(),
                            "You booking has approved!",
                            "Congratulations!, You booking has approved by vehicle rental service. Contact rent service and start your journey",
                            DatabaseFields.notification.NOTIFICATION_TYPE_VEHICLE_BOOKING_APPROVED,
                            new Date(),
                            DatabaseFields.notification.NOTIFICATION_STATUS_UNREAD,
                            bookingDetails
                    );

                    mDatabaseReference.child(bookingDetails.getBooked_by()).child("notifications").push().setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toasty.success(ServiceBookingDetailsActivity.this, "Booking Approved Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void dialCall(String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(callIntent);
    }

    private void initComponents() {
        name = findViewById(R.id.booking_description_name);
        mobile = findViewById(R.id.booking_description_mobile);
        call = findViewById(R.id.booking_description_mobile_call);
        req_date = findViewById(R.id.booking_description_req_date);
        return_date = findViewById(R.id.booking_description_return_date);
        vehicle_name = findViewById(R.id.booking_description_vehicle_name);
        brand = findViewById(R.id.booking_description_brand);

        cus_profile = findViewById(R.id.booking_description_customer_profile);
        vehicle_detail = findViewById(R.id.booking_description_see_more);
        accept = findViewById(R.id.booking_description_accept);
        decline = findViewById(R.id.booking_description_decline);
    }


}
