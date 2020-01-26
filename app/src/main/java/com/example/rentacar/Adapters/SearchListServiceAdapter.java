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

import com.example.rentacar.Model.Service;
import com.example.rentacar.R;
import com.example.rentacar.ServiceAllVehicleActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchListServiceAdapter  extends RecyclerView.Adapter<SearchListServiceAdapter.viewHolder>{

    private Context context;
    private ArrayList<Service> services_list;

    public SearchListServiceAdapter(Context context, ArrayList<Service> services_list) {
        this.context = context;
        this.services_list = services_list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = null;

        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        mView = mLayoutInflater.inflate(R.layout.item_search_view, viewGroup, false);

        return new SearchListServiceAdapter.viewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        final Service service = services_list.get(i);

        Picasso.get().load(service.getProfile_photo()).into(viewHolder.imageView);
        viewHolder.ser_name.setText(service.getName());
        viewHolder.ser_town.setText(service.getTown());

        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ServiceAllVehicleActivity.class);
                intent.putExtra("service_id", service.getId());
                intent.putExtra("name", service.getName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return services_list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView ser_name, ser_town;
        ConstraintLayout constraintLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.sv_image);
            ser_name = itemView.findViewById(R.id.sv_service_name);
            ser_town = itemView.findViewById(R.id.sv_city_town);
            constraintLayout = itemView.findViewById(R.id.sv_item_view);
        }
    }
}

