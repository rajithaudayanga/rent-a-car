package com.example.rentacar.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.Rent;
import com.example.rentacar.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ServiceRentalsAdapter extends RecyclerView.Adapter<ServiceRentalsAdapter.viewHolder>{

    private Context context;
    private ArrayList<Rent> rents_list;

    public ServiceRentalsAdapter(Context context, ArrayList<Rent> rents_list) {
        this.context = context;
        this.rents_list = rents_list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_rentals, viewGroup, false);
        return new ServiceRentalsAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, int i) {
        final Rent rent = rents_list.get(i);

        viewHolder.status.setText(rent.getStatus());

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(rent.getService_id()).child("vehicles").child(rent.getVehicle_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child(DatabaseFields.vehicleFields.IMAGE_1_URL).getValue(String.class)).into(viewHolder.vehicle);
                final String vehicle_name = dataSnapshot.child("name").getValue(String.class);

                databaseReference.child(rent.getCus_id()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String cus_name = dataSnapshot.child("name").getValue(String.class);

                        Picasso.get().load(dataSnapshot.child(DatabaseFields.userFields.PROFILE_PHOTO).getValue(String.class)).into(viewHolder.user);
                        viewHolder.title.setText(vehicle_name + " Rent by " + cus_name + " " + rent.getFrom_date() + " to " + rent.getTo_date());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return rents_list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        TextView title, status;
        ImageView vehicle, user;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.ser_rent_title);
            status = itemView.findViewById(R.id.ser_rent_status);
            vehicle = itemView.findViewById(R.id.ser_rent_vehicle);
            user = itemView.findViewById(R.id.ser_rent_user);
        }
    }
}
