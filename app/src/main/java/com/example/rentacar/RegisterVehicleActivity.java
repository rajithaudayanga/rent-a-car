package com.example.rentacar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.Vehicle;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class RegisterVehicleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE = 11;
    private EditText vehicle_name, brand, model, color, passengers, price_per_day, distance_per_day, extra_hour, extra_km, description;
    private ImageView image1, image2, image3, image4;
    private Button save;
    private TextView register_vehicle_des, register_vehicle_title;
    private Uri image_1_url, image_2_url, image_3_url, image_4_url;
    private ImageView selected_image_view;

    private String current_user_id;
    private String url_1, url_2, url_3, url_4;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private SweetAlertDialog pDialog;
    private String process_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehicle);

        initComponents();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mStorageReference = FirebaseStorage.getInstance().getReference("Images");

        current_user_id = mFirebaseAuth.getCurrentUser().getUid();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Wait...");
        pDialog.setCancelable(false);

        process_type = getIntent().getExtras().getString("process_type");

        image1.setTag("temp");
        image2.setTag("temp");
        image3.setTag("temp");
        image4.setTag("temp");

        if (process_type.equals(DatabaseFields.constants.PROCESS_TYPE_EDIT)) {
            pDialog.show();
            register_vehicle_des.setText(getResources().getString(R.string.edit_vehicle_details));
            register_vehicle_title.setText(getResources().getString(R.string.edit_vehicle));
            final String vehicle_id = getIntent().getExtras().getString("vehicle_id");
            if (vehicle_id != null) {
                mDatabaseReference.child(current_user_id).child("vehicles").child(vehicle_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);

                        if (vehicle != null) {
                            vehicle_name.setText(vehicle.getName());
                            brand.setText(vehicle.getBrand());
                            model.setText(vehicle.getModel());
                            color.setText(vehicle.getColor());
                            passengers.setText(vehicle.getNum_of_passengers());
                            price_per_day.setText(vehicle.getPrice_per_day());
                            distance_per_day.setText(vehicle.getDistance_per_day());
                            extra_hour.setText(vehicle.getPrice_extra_hour());
                            extra_km.setText(vehicle.getPrice_extra_km());
                            description.setText(vehicle.getDescription());

                            Picasso.get().load(vehicle.getImage_1_url()).into(image1);
                            Picasso.get().load(vehicle.getImage_2_url()).into(image2);
                            Picasso.get().load(vehicle.getImage_3_url()).into(image3);
                            Picasso.get().load(vehicle.getImage_4_url()).into(image4);

                            image1.setTag("select");
                            image2.setTag("select");
                            image3.setTag("select");
                            image4.setTag("select");

                        }
                        pDialog.dismissWithAnimation();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        save.setOnClickListener(this);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);


    }

    private void initComponents() {
        vehicle_name = findViewById(R.id.vehicle_reg_name);
        brand = findViewById(R.id.vehicle_reg_brand);
        model = findViewById(R.id.vehicle_reg_model);
        color = findViewById(R.id.vehicle_reg_color);
        passengers = findViewById(R.id.vehicle_reg_passengers);
        price_per_day = findViewById(R.id.vehicle_reg_day_price);
        distance_per_day = findViewById(R.id.vehicle_reg_day_distance);
        extra_hour = findViewById(R.id.vehicle_reg_extra_hour);
        extra_km = findViewById(R.id.vehicle_reg_extra_km);
        description = findViewById(R.id.vehicle_reg_description);

        register_vehicle_title = findViewById(R.id.register_vehicle_title);
        register_vehicle_des = findViewById(R.id.register_vehicle_des);

        image1 = findViewById(R.id.vehicle_reg_image1);
        image2 = findViewById(R.id.vehicle_reg_image2);
        image3 = findViewById(R.id.vehicle_reg_image3);
        image4 = findViewById(R.id.vehicle_reg_image4);

        save = findViewById(R.id.vehicle_reg_btn);
    }

    private void registerVehicle() {
        final String vehi_name = vehicle_name.getText().toString().trim();
        final String vehi_brand = brand.getText().toString().trim();
        final String vehi_model = model.getText().toString().trim();
        final String vehi_color = color.getText().toString().trim();
        final String vehi_passengers = passengers.getText().toString().trim();
        final String vehi_price_per_day = price_per_day.getText().toString().trim();
        final String vehi_distance_per_day = distance_per_day.getText().toString().trim();
        final String vehi_extra_hour = extra_hour.getText().toString().trim();
        final String vehi_extra_km = extra_km.getText().toString().trim();
        final String vehi_description = description.getText().toString().trim();

        if (TextUtils.isEmpty(vehi_name) ||
                TextUtils.isEmpty(vehi_brand) ||
                TextUtils.isEmpty(vehi_model) ||
                TextUtils.isEmpty(vehi_color) ||
                TextUtils.isEmpty(vehi_passengers) ||
                TextUtils.isEmpty(vehi_price_per_day) ||
                TextUtils.isEmpty(vehi_distance_per_day) ||
                TextUtils.isEmpty(vehi_extra_hour) ||
                TextUtils.isEmpty(vehi_extra_km) ||
                TextUtils.isEmpty(vehi_description)
        ) {
            Toasty.info(this, "Please Fill Out All Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (image1.getTag().equals("temp") || image2.getTag().equals("temp") || image3.getTag().equals("temp") || image4.getTag().equals("temp")) {
            Toasty.info(this, "Please Select 4 images", Toast.LENGTH_SHORT).show();
        } else {
            if (process_type.equals(DatabaseFields.constants.PROCESS_TYPE_REGISTER)) {
                pDialog.setTitleText("Uploading First Image");
                pDialog.show();

                final String key = mDatabaseReference.child(current_user_id).child("vehicles").push().getKey();
                final StorageReference sub_ref1 = mStorageReference.child(current_user_id).child("vehicles").child(key).child("image_1");

                //uploading 1st image
                UploadTask uploadTask1 = sub_ref1.putFile(image_1_url);

                //upload 1st image
                uploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return sub_ref1.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        pDialog.setTitleText("Uploading Second Image");
                        url_1 = task.getResult().toString();

                        //uploading 2nd image
                        final StorageReference sub_ref2 = mStorageReference.child(current_user_id).child("vehicles").child(key).child("image_2");
                        UploadTask uploadTask2 = sub_ref2.putFile(image_2_url);
                        uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return sub_ref2.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                pDialog.setTitleText("Uploading Third Image");
                                url_2 = task.getResult().toString();

                                //uploading 3rd image
                                final StorageReference sub_ref3 = mStorageReference.child(current_user_id).child("vehicles").child(key).child("image_3");
                                UploadTask uploadTask3 = sub_ref3.putFile(image_3_url);
                                Task<Uri> uriTask = uploadTask3.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }
                                        return sub_ref3.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        pDialog.setTitleText("Uploading Fourth Image");
                                        url_3 = task.getResult().toString();

                                        //uploading 4th image
                                        final StorageReference sub_ref4 = mStorageReference.child(current_user_id).child("vehicles").child(key).child("image_4");
                                        UploadTask uploadTask4 = sub_ref4.putFile(image_4_url);
                                        uploadTask4.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }
                                                return sub_ref4.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                url_4 = task.getResult().toString();

                                                Vehicle vehicle = new Vehicle(vehi_name, vehi_brand, vehi_model, vehi_color, vehi_passengers, vehi_price_per_day, vehi_distance_per_day, vehi_extra_hour, vehi_extra_km, vehi_description, url_1, url_2, url_3, url_4, "active", null);

                                                mDatabaseReference.child(current_user_id).child("vehicles").push().setValue(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(RegisterVehicleActivity.this, "Vehicle Registered Successfully.", Toast.LENGTH_SHORT).show();

                                                            //clear text fields
                                                            vehicle_name.setText("");
                                                            brand.setText("");
                                                            model.setText("");
                                                            color.setText("");
                                                            passengers.setText("");
                                                            price_per_day.setText("");
                                                            distance_per_day.setText("");
                                                            extra_hour.setText("");
                                                            extra_km.setText("");
                                                            description.setText("");

                                                            image1.setImageDrawable(null);
                                                            image2.setImageDrawable(null);
                                                            image3.setImageDrawable(null);
                                                            image4.setImageDrawable(null);

                                                            pDialog.dismiss();
                                                            findViewById(R.id.add_vehicle_scrollview).scrollTo(0, 0);
                                                        } else {
                                                            pDialog.dismiss();
                                                            Toasty.error(RegisterVehicleActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pDialog.dismiss();
                                                Toasty.error(RegisterVehicleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pDialog.dismiss();
                                        Toasty.error(RegisterVehicleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pDialog.dismiss();
                                Toasty.error(RegisterVehicleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialog.dismiss();
                        Toasty.error(RegisterVehicleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (process_type.equals(DatabaseFields.constants.PROCESS_TYPE_EDIT)) {

            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == save) {
            registerVehicle();
        } else if (v == image1) {
            selectFromGallery(image1);
        } else if (v == image2) {
            selectFromGallery(image2);
        } else if (v == image3) {
            selectFromGallery(image3);
        } else if (v == image4) {
            selectFromGallery(image4);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri image_uri = data.getData();
            CropImage.activity(image_uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(3, 2)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                if (selected_image_view == image1) {
                    image_1_url = uri;
                } else if (selected_image_view == image2) {
                    image_2_url = uri;
                } else if (selected_image_view == image3) {
                    image_3_url = uri;
                } else if (selected_image_view == image4) {
                    image_4_url = uri;
                }
                Picasso.get().load(uri).into(selected_image_view);
                selected_image_view.setTag("selected");

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void selectFromGallery(ImageView image_view) {
        selected_image_view = image_view;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }
}
