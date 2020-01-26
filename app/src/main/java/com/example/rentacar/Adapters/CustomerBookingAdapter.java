package com.example.rentacar.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rentacar.CustomerBookingDetailsActivity;
import com.example.rentacar.Model.BookingDetails;
import com.example.rentacar.Model.Vehicle;
import com.example.rentacar.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerBookingAdapter extends RecyclerView.Adapter<CustomerBookingAdapter.viewHolder> {

    private Context context;
    private ArrayList<BookingDetails> bookingDetails_list;

    public CustomerBookingAdapter(Context context, ArrayList<BookingDetails> bookingDetails_list) {
        this.context = context;
        this.bookingDetails_list = bookingDetails_list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.item_customer_booking, viewGroup, false);
        return new CustomerBookingAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, int i) {
        final BookingDetails bookingDetails = bookingDetails_list.get(i);

        viewHolder.hire_date.setText(bookingDetails.getFrom());
        viewHolder.price.setText("Rs: " + bookingDetails.getPrice());

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(bookingDetails.getService_id()).child("vehicles").child(bookingDetails.getVehicle_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);

                if (vehicle != null) {
                    viewHolder.vehicle_name.setText(vehicle.getName());
                    Picasso.get().load(vehicle.getImage_1_url()).into(viewHolder.image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //see more
        viewHolder.see_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CustomerBookingDetailsActivity.class);
                intent.putExtra("booking_id", bookingDetails.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingDetails_list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView vehicle_name, price, hire_date;
        Button see_more;
        ConstraintLayout constraintLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.mv_image);
            vehicle_name = itemView.findViewById(R.id.mv_vehicle_name);
            price = itemView.findViewById(R.id.mv_price);
            hire_date = itemView.findViewById(R.id.mv_hire_date);
            see_more = itemView.findViewById(R.id.mv_see_more);
            constraintLayout = itemView.findViewById(R.id.item_booking);

        }
    }
}
