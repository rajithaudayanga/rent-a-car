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

import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.BookingDetails;
import com.example.rentacar.R;
import com.example.rentacar.ServiceBookingDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.viewHolder> {

    private Context context;
    private ArrayList<BookingDetails> booking_list;

    public BookingAdapter(Context context, ArrayList<BookingDetails> booking_list) {
        this.context = context;
        this.booking_list = booking_list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        view = mLayoutInflater.inflate(R.layout.item_service_booking, viewGroup, false);

        return new BookingAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, int i) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        final BookingDetails bookingDetails = booking_list.get(i);
        viewHolder.approval_status.setText(bookingDetails.getStatus());

        //setting data
        databaseReference.child(bookingDetails.getService_id()).child("vehicles").child(bookingDetails.getVehicle_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image_uri = dataSnapshot.child(DatabaseFields.vehicleFields.IMAGE_1_URL).getValue(String.class);

                Picasso.get().load(image_uri).into(viewHolder.ser_image);

                final String vehicle_name = dataSnapshot.child("name").getValue(String.class);

                databaseReference.child(bookingDetails.getBooked_by()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String image_uri = dataSnapshot.child(DatabaseFields.userFields.PROFILE_PHOTO).getValue(String.class);

                        Picasso.get().load(image_uri).into(viewHolder.user_image);

                        String user_name = dataSnapshot.child("name").getValue(String.class);

                        viewHolder.title.setText(vehicle_name + " Booked by " + user_name + " " + bookingDetails.getFrom() + " to " + bookingDetails.getTo());
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


        //set on click
        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                String text = gson.toJson(bookingDetails);

                Intent intent = new Intent(context, ServiceBookingDetailsActivity.class);
                intent.putExtra("booking_obj", text);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return booking_list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        TextView title, approval_status;
        ImageView ser_image, user_image;
        ConstraintLayout constraintLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.ser_booking_title);
            approval_status = itemView.findViewById(R.id.ser_booking_approval);
            ser_image = itemView.findViewById(R.id.ser_booking_vehicle);
            user_image = itemView.findViewById(R.id.ser_booking_user);
            constraintLayout = itemView.findViewById(R.id.booking_item);
        }
    }
}
