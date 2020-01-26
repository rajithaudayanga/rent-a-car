package com.example.rentacar;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.rentacar.Adapters.AllVehicleListAdapter;
import com.example.rentacar.Model.Vehicle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ServiceAllVehicleActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recycler_view;

    private DatabaseReference mDatabaseReference;
    private String service_id, name;
    private ArrayList<Vehicle> vehicles_list;
    private SweetAlertDialog sweetAlertDialog;
    private TextView service_name;
    private FloatingActionButton send_message;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_all_vehicle);

        initComponents();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            service_id = extras.getString("service_id");
            name = extras.getString("name");
        }

        //set service name
        service_name.setText(name);

        //set on click listeners
        send_message.setOnClickListener(this);

        //setup recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setHasFixedSize(true);
        recycler_view.setNestedScrollingEnabled(false);
        recycler_view.addItemDecoration(new DividerItemDecoration(recycler_view.getContext(), DividerItemDecoration.VERTICAL));

        //initialize components
        vehicles_list = new ArrayList<>();
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Waiting...");

        //show progress bar
        sweetAlertDialog.show();

        //hide message btn
        if (firebaseAuth.getCurrentUser() != null) {
            if (service_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                send_message.setEnabled(false);
            }else {
                send_message.setEnabled(true);
            }
        }

        //get data
        mDatabaseReference.child("Users").child(service_id).child("vehicles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vehicles_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vehicle vehicle = snapshot.getValue(Vehicle.class);

                    if (vehicle != null) {
                        vehicle.setId(snapshot.getKey());
                        vehicle.setService_id(service_id);
                        if (vehicle.getStatus().equalsIgnoreCase("active")) {
                            vehicles_list.add(vehicle);
                        }
                    }
                }
                AllVehicleListAdapter allVehicleListAdapter = new AllVehicleListAdapter(getApplicationContext(), vehicles_list);
                recycler_view.setAdapter(allVehicleListAdapter);
                allVehicleListAdapter.notifyDataSetChanged();
                sweetAlertDialog.dismissWithAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initComponents() {
        recycler_view = findViewById(R.id.all_vehicle_list);
        service_name = findViewById(R.id.all_vehicle_service_name);
        send_message = findViewById(R.id.fab_message);
    }

    @Override
    public void onClick(View v) {
        if (v== send_message){
            if (FirebaseAuth.getInstance().getCurrentUser()!= null) {
                Intent intent = new Intent(this, MessageConversationActivity.class);
                intent.putExtra("partner_id", service_id);
                startActivity(intent);
            }
        }
    }
}
