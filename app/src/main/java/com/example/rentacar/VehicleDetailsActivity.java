package com.example.rentacar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentacar.Adapters.SwipeAdapter;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.BookingDetails;
import com.example.rentacar.Model.Notification;
import com.example.rentacar.Model.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class VehicleDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private String vehicle_id, service_id, user_type, notification_id;
    private int current_position = 0, cus_position = 0;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private TextView vehi_name, vehi_brand, vehi_model, vehi_color, vehi_passengers, vehi_price_day, vehi_distance_day, vehi_price_extra_hour, vehi_price_extra_km, vehi_description, info_text;
    private Button details_service_profile_btn, details_check_availability_btn, details_delete_btn, details_hide_btn, details_edit_btn, details_set_availability_btn, details_service_book_btn;

    private ViewPager viewPager;
    private ArrayList<String> image_list;
    private LinearLayout slider_dots;

    private Timer timer;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        initComponents();

        //get user type
        SharedPreferences sharedPreferences = getSharedPreferences(DatabaseFields.sharedPrefferenceData.FILE_NAME, MODE_PRIVATE);
        user_type = sharedPreferences.getString(DatabaseFields.userFields.USER_TYPE, "");

        if (user_type.equals(DatabaseFields.constants.USER_TYPE_SERVICE)) {
            details_check_availability_btn.setVisibility(View.GONE);
            info_text.setVisibility(View.GONE);
            details_service_profile_btn.setVisibility(View.GONE);
        } else  {
            details_set_availability_btn.setVisibility(View.GONE);
            details_edit_btn.setVisibility(View.GONE);
            details_hide_btn.setVisibility(View.GONE);
            details_delete_btn.setVisibility(View.GONE);
        }

        details_service_profile_btn.setOnClickListener(this);
        details_check_availability_btn.setOnClickListener(this);
        details_delete_btn.setOnClickListener(this);
        details_hide_btn.setOnClickListener(this);
        details_edit_btn.setOnClickListener(this);
        details_set_availability_btn.setOnClickListener(this);
        details_service_book_btn.setOnClickListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseReference.keepSynced(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            vehicle_id = bundle.getString("vehicle_id");
            service_id = bundle.getString("service_id");
            notification_id = bundle.getString("notification_id");

            if (notification_id != null && !notification_id.equalsIgnoreCase("")) {
                details_service_book_btn.setVisibility(View.VISIBLE);
                details_check_availability_btn.setVisibility(View.GONE);
                info_text.setVisibility(View.GONE);
            }
        }


        if (service_id != null && vehicle_id != null) {
            mDatabaseReference.child(service_id).child("vehicles").child(vehicle_id).addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    vehicle = dataSnapshot.getValue(Vehicle.class);

                    if (vehicle != null) {
                        vehi_name.setText(vehicle.getName());
                        vehi_brand.setText(vehicle.getBrand());
                        vehi_model.setText(vehicle.getModel());
                        vehi_color.setText(vehicle.getColor());
                        vehi_passengers.setText(vehicle.getNum_of_passengers());
                        vehi_price_day.setText(vehicle.getPrice_per_day());
                        vehi_distance_day.setText(vehicle.getDistance_per_day());
                        vehi_price_extra_hour.setText(vehicle.getPrice_extra_hour());
                        vehi_price_extra_km.setText(vehicle.getPrice_extra_km());
                        vehi_description.setText(vehicle.getDescription());

                        image_list = new ArrayList<>();
                        image_list.add(vehicle.getImage_1_url());
                        image_list.add(vehicle.getImage_2_url());
                        image_list.add(vehicle.getImage_3_url());
                        image_list.add(vehicle.getImage_4_url());

                        SwipeAdapter swipeAdapter = new SwipeAdapter(getApplicationContext(), image_list);
                        viewPager.setAdapter(swipeAdapter);

                        if (vehicle.getStatus().equals("delete")) {
                            details_delete_btn.setVisibility(View.GONE);
                            details_edit_btn.setVisibility(View.GONE);
                            details_hide_btn.setVisibility(View.GONE);
                            details_set_availability_btn.setVisibility(View.GONE);
                        } else if (vehicle.getStatus().equals("hide")) {
                            details_hide_btn.setText("show in search list");
                        } else if (vehicle.getStatus().equals("active")) {
                            details_delete_btn.setVisibility(View.VISIBLE);
                            details_edit_btn.setVisibility(View.VISIBLE);
                            details_hide_btn.setVisibility(View.VISIBLE);
                            details_set_availability_btn.setVisibility(View.VISIBLE);
                        }

                        createDots(cus_position++);
                        createSlider();

                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int i, float v, int i1) {

                            }

                            @Override
                            public void onPageSelected(int i) {
                                if (cus_position == image_list.size()) {
                                    cus_position = 0;
                                }

                                createDots(cus_position++);
                            }

                            @Override
                            public void onPageScrollStateChanged(int i) {

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

    @Override
    public void onClick(View v) {
        if (v == details_service_profile_btn) {

        } else if (v == details_check_availability_btn) {
            checkAvailability();
        } else if (v == details_delete_btn) {
            deleteVehicle();
        } else if (v == details_hide_btn) {
            if (vehicle != null) {
                if (vehicle.getStatus().equals("hide")) {
                    showVehicle();
                } else if (vehicle.getStatus().equals("active")) {
                    hideVehicle();
                }
            }
        } else if (v == details_edit_btn) {
            editVehicleDetails();
        } else if (v == details_set_availability_btn) {
            setAvailability();
        } else if (v == details_service_book_btn) {
            bookVehicle();
        }
    }

    private void showVehicle() {
        new SweetAlertDialog(this)
                .setTitleText("Hide From Search List")
                .setContentText("Do you want show this vehicle in search list")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();

                        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("vehicles").child(vehicle_id).child("status").setValue("active").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(VehicleDetailsActivity.this, "Vehicle show in Search List.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(VehicleDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    private void bookVehicle() {
        new SweetAlertDialog(this)
                .setTitleText("Vehicle Booking")
                .setContentText("Do you want to book this vehicle?")
                .setCancelText("No")
                .setConfirmText("Yes")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.dismiss();

                        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("notifications").child(notification_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                final Notification notification = dataSnapshot.getValue(Notification.class);
                                if (notification != null) {
                                    mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("name").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String sender_name = dataSnapshot.getValue(String.class);

                                            Notification new_notification = new Notification(
                                                    mFirebaseAuth.getCurrentUser().getUid(),
                                                    "Vehicle Booking",
                                                    vehi_name.getText().toString() + " booked by " + sender_name + " from " + notification.getBookingDetails().getFrom() + " to " + notification.getBookingDetails().getTo(),
                                                    DatabaseFields.notification.NOTIFICATION_TYPE_VEHICLE_BOOKING,
                                                    new Date(),
                                                    DatabaseFields.notification.NOTIFICATION_STATUS_UNREAD,
                                                    new BookingDetails(
                                                            null,
                                                            mFirebaseAuth.getCurrentUser().getUid(),
                                                            vehicle_id,
                                                            notification.getBookingDetails().getFrom(),
                                                            notification.getBookingDetails().getTo(),
                                                            service_id,
                                                            notification.getBookingDetails().getPrice(),
                                                            DatabaseFields.booking.PENDING_APPROVEL)
                                            );

                                            BookingDetails bookingDetails1 = new BookingDetails(
                                                    null,
                                                    mFirebaseAuth.getCurrentUser().getUid(),
                                                    vehicle_id,
                                                    notification.getBookingDetails().getFrom(),
                                                    notification.getBookingDetails().getTo(),
                                                    notification.getBookingDetails().getService_id(),
                                                    notification.getBookingDetails().getPrice(),
                                                    DatabaseFields.booking.PENDING_APPROVEL
                                            );

                                            String booking_id = mDatabaseReference.child(service_id).child("bookings").push().getKey();
                                            String notifi_id = mDatabaseReference.child(service_id).child("Notifications").push().getKey();

                                            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
                                            stringObjectHashMap.put(service_id + "/bookings/" + booking_id, bookingDetails1);
                                            stringObjectHashMap.put(service_id + "/notifications/" + notifi_id, new_notification);
                                            stringObjectHashMap.put(mFirebaseAuth.getCurrentUser().getUid() + "/bookings/" + booking_id, bookingDetails1);

                                            mDatabaseReference.updateChildren(stringObjectHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    sweetAlertDialog.dismiss();
                                                    Toasty.success(VehicleDetailsActivity.this, "Your booking is successfully completed!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                } else {
                                    Toast.makeText(VehicleDetailsActivity.this, "kadjfl;aj", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .show();
    }

    private void checkAvailability() {
        Intent intent = new Intent(this, CustomerAvailabilityCheckActivity.class);
        intent.putExtra("service_id", service_id);
        intent.putExtra("vehicle_id", vehicle_id);

        startActivity(intent);
    }

    private void editVehicleDetails() {
        Intent intent = new Intent(this, RegisterVehicleActivity.class);
        intent.putExtra("process_type", DatabaseFields.constants.PROCESS_TYPE_EDIT);
        intent.putExtra("vehicle_id", vehicle_id);

        startActivity(intent);
    }

    private void deleteVehicle() {
        new SweetAlertDialog(this)
                .setTitleText("Delete Vehicle")
                .setContentText("Do you want delete this vehicle")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();

                        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("vehicles").child(vehicle_id).child("status").setValue("delete").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(VehicleDetailsActivity.this, "Vehicle Deleted!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(VehicleDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    private void hideVehicle() {
        new SweetAlertDialog(this)
                .setTitleText("Hide From Search List")
                .setContentText("Do you want hide this vehicle from search list")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();

                        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("vehicles").child(vehicle_id).child("status").setValue("hide").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(VehicleDetailsActivity.this, "Vehicle Hide from Search List.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(VehicleDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    private void setAvailability() {
        new SweetAlertDialog(this)
                .setTitleText("Confirm Availability")
                .setContentText("Is " + vehi_name.getText() + " available")
                .setConfirmText("Available")
                .setCancelText("Not Available")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("vehicles").child(vehicle_id).child("availability").setValue("notavailable").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(VehicleDetailsActivity.this, "Successfully Changed!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(VehicleDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();

                        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("vehicles").child(vehicle_id).child("availability").setValue("available").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(VehicleDetailsActivity.this, "Successfully Changed!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toasty.error(VehicleDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .show();
    }

    private void createSlider() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (current_position == Integer.MAX_VALUE) {
                    current_position = 0;
                }
                viewPager.setCurrentItem(current_position++, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 2000, 2500);
    }

    private void createDots(int current_position) {
        if (slider_dots.getChildCount() > 0) {
            slider_dots.removeAllViews();
        }

        ImageView dots[] = new ImageView[image_list.size()];

        for (int i = 0; i < image_list.size(); i++) {
            dots[i] = new ImageView(this);
            if (i == current_position) {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.slider_active_dot));
            } else {
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.slider_inactive_dot));
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4, 0, 4, 0);
            slider_dots.addView(dots[i], layoutParams);
        }
    }


    private void initComponents() {
        vehi_name = findViewById(R.id.details_name);
        vehi_brand = findViewById(R.id.details_brand);
        vehi_model = findViewById(R.id.details_model);
        vehi_color = findViewById(R.id.details_color);
        vehi_passengers = findViewById(R.id.details_passenger);
        vehi_price_day = findViewById(R.id.details_price_per_day);
        vehi_distance_day = findViewById(R.id.details_distance_per_day);
        vehi_price_extra_hour = findViewById(R.id.details_price_extra_hour);
        vehi_price_extra_km = findViewById(R.id.details_price_extra_km);
        vehi_description = findViewById(R.id.details_description);
        info_text = findViewById(R.id.info_text);

        details_check_availability_btn = findViewById(R.id.details_check_availability_btn);
        details_service_profile_btn = findViewById(R.id.details_service_profile_btn);
        details_delete_btn = findViewById(R.id.details_delete_btn);
        details_hide_btn = findViewById(R.id.details_hide_btn);
        details_edit_btn = findViewById(R.id.details_edit_btn);
        details_set_availability_btn = findViewById(R.id.details_set_availability_btn);
        details_service_book_btn = findViewById(R.id.details_service_book_btn);

        viewPager = findViewById(R.id.image_slider_viewpager);
        slider_dots = findViewById(R.id.slider_dots);
    }
}
