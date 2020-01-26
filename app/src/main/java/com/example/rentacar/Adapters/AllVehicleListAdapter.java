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

public class AllVehicleListAdapter extends RecyclerView.Adapter<AllVehicleListAdapter.viewHolder>{

    private Context context;
    private ArrayList<Vehicle> vehicle_list;

    public AllVehicleListAdapter(Context context, ArrayList<Vehicle> vehicle_list) {
        this.context = context;
        this.vehicle_list = vehicle_list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = null;

        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        mView = mLayoutInflater.inflate(R.layout.item_services_all_vehicles, viewGroup, false);

        return new AllVehicleListAdapter.viewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        final Vehicle vehicle = vehicle_list.get(i);

        Picasso.get().load(vehicle.getImage_1_url()).into(viewHolder.image);
        viewHolder.name.setText(vehicle.getName());
        viewHolder.price.setText(vehicle.getPrice_per_day());
        viewHolder.model.setText(vehicle.getModel() + "/" + vehicle.getBrand());
        viewHolder.status.setText(vehicle.getStatus());

        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VehicleDetailsActivity.class);
                intent.putExtra("vehicle_id", vehicle.getId());
                intent.putExtra("service_id", vehicle.getService_id());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicle_list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        TextView name,price,model,status;
        ImageView image;
        ConstraintLayout constraintLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.mv_vehicle_name);
            price = itemView.findViewById(R.id.mv_price);
            model = itemView.findViewById(R.id.mv_model);
            status = itemView.findViewById(R.id.mv_status);

            image = itemView.findViewById(R.id.mv_image);
            constraintLayout = itemView.findViewById(R.id.item_view_vehicle);
        }
    }
}
