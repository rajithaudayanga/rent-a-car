package com.example.rentacar.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class CustomerRentalsAdapter extends RecyclerView.Adapter<CustomerRentalsAdapter.viewHolder> {

    private Context context;
    private ArrayList<Rent> rents_list;

    public CustomerRentalsAdapter(Context context, ArrayList<Rent> rents_list) {
        this.context = context;
        this.rents_list = rents_list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.item_customer_rentals, viewGroup, false);
        return new CustomerRentalsAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, int i) {
        Rent rent = rents_list.get(i);

        viewHolder.hire_date.setText(rent.getFrom_date());
        viewHolder.status.setText(rent.getStatus());
        viewHolder.price.setText(rent.getPrice());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(rent.getService_id()).child("vehicles").child(rent.getVehicle_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewHolder.name.setText(dataSnapshot.child("name").getValue(String.class));
                Picasso.get().load(dataSnapshot.child(DatabaseFields.vehicleFields.IMAGE_1_URL).getValue(String.class)).into(viewHolder.imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return rents_list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView name, price, hire_date, status;
        ImageView imageView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.item_cus_rentals);
            name = itemView.findViewById(R.id.mv_vehicle_name);
            price = itemView.findViewById(R.id.mv_price);
            hire_date = itemView.findViewById(R.id.mv_hire_date);
            status = itemView.findViewById(R.id.mv_hire_status);
            imageView = itemView.findViewById(R.id.mv_image);
        }
    }
}
