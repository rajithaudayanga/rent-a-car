package com.example.rentacar.Fragments;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rentacar.Model.Service;
import com.example.rentacar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class ServiceProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView ser_image;
    private TextView ser_name, ser_mobile, ser_telephone, ser_address, call_telephone, call_mobile, see_vehicle;
    private View view;

    private String current_id;
    private String mobile_number, telephone_number;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;
    private LatLng location;
    private GoogleMap mMap;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_service_profile, container, false);

        initComponents();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mStorageReference = FirebaseStorage.getInstance().getReference("Images");

        //current user id
        if (mFirebaseAuth.getCurrentUser() != null) {
            current_id = mFirebaseAuth.getCurrentUser().getUid();
            loadContent();
        }

        //setup map
        showMap();


        call_telephone.setOnClickListener(this);
        call_mobile.setOnClickListener(this);
        see_vehicle.setOnClickListener(this);

        return view;
    }

    private void showMap() {
       SupportMapFragment supportMapFragment = new SupportMapFragment();
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.service_profile_map, supportMapFragment).commit();
        }

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (location != null) {
                    mMap.addMarker(new MarkerOptions().position(location));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18f));
                }

            }
        });

    }

    private void loadContent() {
        mDatabaseReference.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Service service = dataSnapshot.getValue(Service.class);

                if (service != null) {
                    mobile_number = service.getMobile();
                    telephone_number = service.getTelephone();

                    ser_name.setText(service.getName());
                    ser_telephone.setText(service.getTelephone());
                    ser_mobile.setText(service.getMobile());
                    ser_address.setText(service.getAddress() + "\n" + service.getTown() + "\n" + service.getCity());
                    location = new Gson().fromJson(service.getLocation(), LatLng.class);
                    Picasso.get().load(service.getProfile_photo()).into(ser_image);

                    if (service.getTelephone() != null && !service.getTelephone().equals("")) {
                        ser_telephone.setText(service.getTelephone());
                    }else {
                        call_telephone.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == call_mobile) {
            dialCall(mobile_number);
        } else if (v == call_telephone) {
            dialCall(telephone_number);
        } else if (v == see_vehicle) {
            assert getActivity() != null;
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_content_frame, new ManageVehiclesFragment()).commit();
        }
    }

    private void dialCall(String number) {
        Uri uri = Uri.parse("tel:"+number);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(callIntent);
    }

    private void initComponents() {
        ser_address = view.findViewById(R.id.service_profile_address);
        ser_name = view.findViewById(R.id.service_profile_name);
        ser_mobile = view.findViewById(R.id.service_profile_mobile);
        ser_telephone = view.findViewById(R.id.service_profile_telephone);
        ser_image = view.findViewById(R.id.service_profile_image);
        call_mobile = view.findViewById(R.id.service_profile_call_mobile);
        call_telephone = view.findViewById(R.id.service_profile_call_telephone);
        see_vehicle = view.findViewById(R.id.service_profile_see_vehicle);
    }
}
