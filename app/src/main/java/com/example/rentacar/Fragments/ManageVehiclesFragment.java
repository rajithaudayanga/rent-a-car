package com.example.rentacar.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rentacar.Adapters.Vehicle_Manage_Adapter;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.Vehicle;
import com.example.rentacar.R;
import com.example.rentacar.RegisterVehicleActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageVehiclesFragment extends Fragment implements View.OnClickListener {
    private TextView no_vehicle;
    private RecyclerView recyclerView;
    private FloatingActionButton add_vehicle;
    private View view;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    private ArrayList<Vehicle> vehicles_list;
    private String current_user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manage_vehicles, container, false);

        initComponents();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseReference.keepSynced(true);

        current_user_id = mFirebaseAuth.getCurrentUser().getUid();

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        add_vehicle.setOnClickListener(this);

        vehicles_list = new ArrayList<>();

        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("vehicles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vehicles_list.clear();
                if (dataSnapshot.getChildrenCount() > 0) {
                    no_vehicle.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    for (DataSnapshot postDataSnapshot : dataSnapshot.getChildren()) {
                        Vehicle vehicle = postDataSnapshot.getValue(Vehicle.class);
                        if (vehicle.getStatus().equals("active") || vehicle.getStatus().equals("hide") ) {
                            vehicles_list.add(new Vehicle(
                                    vehicle.getName(),
                                    null,
                                    vehicle.getModel(),
                                    null,
                                    null,
                                    vehicle.getPrice_per_day(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    vehicle.getImage_1_url(),
                                    null,
                                    null,
                                    null,
                                    vehicle.getStatus(),
                                    postDataSnapshot.getKey()
                            ));
                        }

                        Vehicle_Manage_Adapter vehicle_manage_adapter = new Vehicle_Manage_Adapter(getContext(), vehicles_list, current_user_id);
                        recyclerView.setAdapter(vehicle_manage_adapter);
                        vehicle_manage_adapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == add_vehicle) {
            Intent intent = new Intent(getContext(), RegisterVehicleActivity.class);
            intent.putExtra("process_type", DatabaseFields.constants.PROCESS_TYPE_REGISTER);
            startActivity(intent);
        }
    }

    private void initComponents() {
        no_vehicle = view.findViewById(R.id.no_registered_vehicles);
        recyclerView = view.findViewById(R.id.manage_vehicle_recyclerview);
        add_vehicle = view.findViewById(R.id.manage_vehicle_add);
    }
}
