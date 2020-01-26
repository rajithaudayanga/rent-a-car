package com.example.rentacar.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rentacar.Model.Vehicle;
import com.example.rentacar.R;
import com.example.rentacar.VehicleDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Vehicle_Manage_Adapter extends RecyclerView.Adapter<Vehicle_Manage_Adapter.viewHolder> {

    private Context mContext;
    private ArrayList<Vehicle> vehicles_list;
    private String service_id;

    public Vehicle_Manage_Adapter(Context mContext, ArrayList<Vehicle> vehicles_list, String service_id) {
        this.mContext = mContext;
        this.vehicles_list = vehicles_list;
        this.service_id = service_id;
    }

    @NonNull
    @Override
    public Vehicle_Manage_Adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = null;

        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        mView = mLayoutInflater.inflate(R.layout.item_service_manage_vehicles, viewGroup, false);

        return new viewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        final Vehicle vehicle = vehicles_list.get(i);

        viewHolder.vehicle_name.setText(vehicle.getName());
        viewHolder.vehicle_price.setText("Rs: " + vehicle.getPrice_per_day() + " per day");
        viewHolder.status.setText(vehicle.getStatus() + " Vehicle");
        Picasso.get().load(vehicle.getImage_1_url()).into(viewHolder.vehicle_image);

        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VehicleDetailsActivity.class);
                intent.putExtra("vehicle_id", vehicle.getVehicle_id());
                intent.putExtra("service_id", service_id);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicles_list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        ImageView vehicle_image;
        TextView vehicle_name, vehicle_price, status;
        ConstraintLayout constraintLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            vehicle_image = itemView.findViewById(R.id.mv_image);
            vehicle_name = itemView.findViewById(R.id.mv_vehicle_name);
            vehicle_price = itemView.findViewById(R.id.mv_price);
            constraintLayout = itemView.findViewById(R.id.item_manage_vehicle);
            status = itemView.findViewById(R.id.mv_status);
        }
    }
}


