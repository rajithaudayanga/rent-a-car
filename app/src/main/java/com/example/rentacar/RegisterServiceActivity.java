package com.example.rentacar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.Service;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class RegisterServiceActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private CircleImageView user_image;
    private EditText service_name, mobile, telephone, address;
    private Spinner city, town;
    private Button select_location, save;
    private View bottomSheet;
    private LinearLayout use_camera, use_gallery;
    private BottomSheet.Builder bottom_sheet_builder;
    private Uri imageUri;

    private final int PICK_IMAGE = 11;
    private final int IMAGE_CAPTURE = 12;

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference mDatabaseReference, mDatabaseReference1;
    private StorageReference mStorageReference;

    private String ser_name, ser_address, ser_town, ser_city, ser_telephone, ser_mobile;
    private GoogleMap map;
    private TextView current_location_selected;
    private Marker marker;
    private SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_service);

        bottomSheet = getLayoutInflater().inflate(R.layout.image_choose_option, null);

        initComponents();

        if (savedInstanceState != null) {
            ser_name = service_name.getText().toString().trim();
            ser_mobile = mobile.getText().toString().trim();
            ser_telephone = telephone.getText().toString().trim();
            ser_city = city.getSelectedItem().toString();
            ser_town = town.getSelectedItem().toString();
            ser_address = address.getText().toString();

            service_name.setText(savedInstanceState.getString("ser_name"));
            mobile.setText(savedInstanceState.getString("ser_mobile"));
            telephone.setText(savedInstanceState.getString("ser_telephone"));
            city.setSelection(savedInstanceState.getInt("ser_city"));
            town.setSelection(savedInstanceState.getInt("ser_town"));
            address.setText(savedInstanceState.getString("ser_address"));
        }

        //show map
        requestPer();
        showMap();

        // firebase
        mfirebaseAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference("Images");
        mDatabaseReference1 = FirebaseDatabase.getInstance().getReference("Locations");

        //disable town list
        town.setEnabled(false);

        mDatabaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> district_list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    district_list.add(snapshot.getKey());
                }

                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getApplication(), R.layout.item_spinner, district_list);
                city.setAdapter(stringArrayAdapter);
                town.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        select_location.setOnClickListener(this);
        save.setOnClickListener(this);
        user_image.setOnClickListener(this);
        use_camera.setOnClickListener(this);
        use_gallery.setOnClickListener(this);
        city.setOnItemSelectedListener(this);
    }

    private void requestPer() {
        ActivityCompat.requestPermissions(RegisterServiceActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    private void showMap() {
        SupportMapFragment supportMapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.ser_reg_map, supportMapFragment).commit();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(1000);

                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            current_location_selected.setVisibility(View.VISIBLE);
                            LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

                            map.clear();
                            marker = map.addMarker(new MarkerOptions().title("Your current location").position(latLng));
                            marker.showInfoWindow();
                            marker.setDraggable(false);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                        } else {
                            current_location_selected.setVisibility(View.GONE);
                        }
                    }
                };

                if (ActivityCompat.checkSelfPermission(RegisterServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegisterServiceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);

                FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                if (ActivityCompat.checkSelfPermission(RegisterServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RegisterServiceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == save) {
            registerService();
        } else if (v == select_location) {
            Intent intent = new Intent(RegisterServiceActivity.this, MapsActivity.class);
            intent.putExtra("task", "register_select_location");
            startActivityForResult(intent, 99);
        } else if (v == user_image) {
            bottom_sheet_builder = new BottomSheet.Builder(this);
            bottom_sheet_builder.setTitle("Select Option")
                    .setView(bottomSheet)
                    .show();
        } else if (v == use_camera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, IMAGE_CAPTURE);
            }
        } else if (v == use_gallery) {
            selectFromGallery();
        }
    }

    private void selectFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
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
                bottom_sheet_builder.dismiss();
                imageUri = result.getUri();
                Picasso.get().load(imageUri).into(user_image);
                user_image.setTag("selected");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            bottom_sheet_builder.dismiss();
            selectFromGallery();
        } else if (requestCode == 99 && resultCode == RESULT_OK && data != null) {
            Toast.makeText(this, "Location selected", Toast.LENGTH_SHORT).show();
            String selected_location = data.getStringExtra("selected_location");

            Gson gson = new Gson();
            LatLng latLng = gson.fromJson(selected_location, LatLng.class);

            map.clear();
            marker = map.addMarker(new MarkerOptions().title("Selected Location").position(latLng));
            marker.showInfoWindow();
            marker.setDraggable(false);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

            current_location_selected.setVisibility(View.VISIBLE);
            current_location_selected.setText(getResources().getString(R.string.your_selected_location));
        }
    }

    private void registerService() {

        ser_name = service_name.getText().toString().trim();
        ser_mobile = mobile.getText().toString().trim();
        ser_telephone = telephone.getText().toString().trim();
        ser_city = city.getSelectedItem().toString();
        ser_town = town.getSelectedItem().toString();
        ser_address = address.getText().toString();

        if (TextUtils.isEmpty(ser_name)) {
            Toasty.error(this, "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ser_mobile)) {
            Toasty.error(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ser_city)) {
            Toasty.error(this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ser_town)) {
            Toasty.error(this, "Please enter your town", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ser_address)) {
            Toasty.error(this, "Please enter your service place address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user_image.getTag().equals("temp")) {
            Toasty.info(this, "Please select a image for profile picture", Toast.LENGTH_SHORT).show();
        } else {
            sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.setTitleText("Uploading Photo...");
            sweetAlertDialog.show();

            final StorageReference storageReference = mStorageReference.child(mfirebaseAuth.getCurrentUser().getUid()).child(DatabaseFields.userFields.PROFILE_PHOTO);
            UploadTask uploadTask = storageReference.putFile(imageUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Service service = new Service(
                                null,
                                ser_name,
                                ser_mobile,
                                ser_telephone,
                                task.getResult().toString(),
                                new Gson().toJson(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude)),
                                ser_address,
                                ser_city,
                                "2",
                                ser_town,
                                DatabaseFields.constants.USER_TYPE_SERVICE
                        );

                        sweetAlertDialog.setTitle("Waiting...");
                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
                        mDatabaseReference.child(mfirebaseAuth.getCurrentUser().getUid()).setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(RegisterServiceActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterServiceActivity.this, Home.class);
                                    startActivity(intent);
                                    sweetAlertDialog.dismissWithAnimation();
                                    finish();
                                } else {
                                    Toasty.error(RegisterServiceActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            }
                        });
                    } else {
                        sweetAlertDialog.dismissWithAnimation();
                        Toasty.error(RegisterServiceActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initComponents() {
        user_image = findViewById(R.id.reg_service_image);
        service_name = findViewById(R.id.ser_reg_name);
        mobile = findViewById(R.id.ser_reg_mobile);
        telephone = findViewById(R.id.ser_reg_telephone);
        address = findViewById(R.id.ser_reg_address);
        city = findViewById(R.id.ser_reg_city);
        town = findViewById(R.id.ser_reg_town);
        select_location = findViewById(R.id.ser_reg_select_location);
        save = findViewById(R.id.ser_reg_save);
        current_location_selected = findViewById(R.id.current_location_selected);

        use_camera = bottomSheet.findViewById(R.id.select_from_camera);
        use_gallery = bottomSheet.findViewById(R.id.select_from_gallery);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        final ArrayList<String> towns = new ArrayList<>();
        mDatabaseReference1.child(parent.getItemAtPosition(position).toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    towns.add(snapshot.child("name").getValue(String.class));
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_spinner, towns);
                town.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        ser_name = service_name.getText().toString().trim();
        ser_mobile = mobile.getText().toString().trim();
        ser_telephone = telephone.getText().toString().trim();
        ser_city = city.getSelectedItem().toString();
        ser_town = town.getSelectedItem().toString();
        ser_address = address.getText().toString();

        outState.putString("ser_name", ser_name);
        outState.putString("ser_mobile", ser_mobile);
        outState.putString("ser_telephone", ser_telephone);
        outState.putInt("ser_city", city.getSelectedItemPosition());
        outState.putInt("ser_town", town.getSelectedItemPosition());
        outState.putString("ser_address", ser_address);
    }
}
