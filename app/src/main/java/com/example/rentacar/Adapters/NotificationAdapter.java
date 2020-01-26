package com.example.rentacar.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rentacar.AvailableCheckActivity;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.MessageConversationActivity;
import com.example.rentacar.Model.Notification;
import com.example.rentacar.R;
import com.example.rentacar.ServiceBookingDetailsActivity;
import com.example.rentacar.VehicleDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

    private Context mContext;
    private ArrayList<Notification> notifications_list;

    public NotificationAdapter(Context mContext, ArrayList<Notification> notifications_list) {
        this.mContext = mContext;
        this.notifications_list = notifications_list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;

        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        view = mLayoutInflater.inflate(R.layout.item_notification, viewGroup, false);

        return new NotificationAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, int i) {
        final Notification notification = notifications_list.get(i);

        //set description
        String des = null;
        if (notification.getNotification_text().length() > 40) {
            des = notification.getNotification_text().substring(0, 40) + "...";
        }else {
            des = notification.getNotification_text();
        }
        viewHolder.description.setText(des);
        viewHolder.title.setText(notification.getNotification_title());

        //set time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(notification.getDate());
        CharSequence relativeTimeSpanString = DateUtils.getRelativeTimeSpanString(calendar.getTimeInMillis(), Calendar.getInstance().getTimeInMillis(), MINUTE_IN_MILLIS, DateUtils.FORMAT_NUMERIC_DATE);
        viewHolder.time.setText(relativeTimeSpanString);

        //change read notification color
        if (notification.getStatus().equalsIgnoreCase(DatabaseFields.notification.NOTIFICATION_STATUS_UNREAD)) {
            viewHolder.constraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        }

        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.setTitleText("Loading...");
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notifications").child(notification.getNotification_id()).child("status").setValue(DatabaseFields.notification.NOTIFICATION_STATUS_READ).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //change background color
                        viewHolder.constraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));

                        //action check vehicle
                        if (notification.getNotification_type().equalsIgnoreCase(DatabaseFields.notification.NOTIFICATION_TYPE_CHECK_VEHICLE)) {
                            Intent intent = new Intent(mContext, AvailableCheckActivity.class);
                            intent.putExtra("notification_id", notification.getNotification_id());
                            mContext.startActivity(intent);

                        } else if (notification.getNotification_type().equalsIgnoreCase(DatabaseFields.notification.NOTIFICATION_TYPE_RESPONSE_AVAILABLE_VEHICLE)) {
                            Intent intent = new Intent(mContext, VehicleDetailsActivity.class);
                            intent.putExtra("vehicle_id", notification.getBookingDetails().getVehicle_id());
                            intent.putExtra("service_id", notification.getBookingDetails().getService_id());
                            intent.putExtra("notification_id", notification.getNotification_id());
                            mContext.startActivity(intent);
                        }else if (notification.getNotification_type().equalsIgnoreCase(DatabaseFields.notification.NOTIFICATION_TYPE_VEHICLE_BOOKING)){

                            Gson gson = new Gson();
                            String text = gson.toJson(notification.getBookingDetails());

                            Intent intent= new Intent(mContext, ServiceBookingDetailsActivity.class);
                            intent.putExtra("booking_obj", text);
                            mContext.startActivity(intent);
                        } else if (notification.getNotification_type().equalsIgnoreCase(DatabaseFields.notification.NOTIFICATION_NEW_MESSAGE)) {
                            Intent intent = new Intent(mContext, MessageConversationActivity.class);
                            intent.putExtra("partner_id", notification.getNotification_from());
                            mContext.startActivity(intent);
                        }

                        sweetAlertDialog.dismissWithAnimation();
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications_list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder {

        TextView title, description, time;
        ConstraintLayout constraintLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.notification_list_title);
            description = itemView.findViewById(R.id.notification_list_description);
            time = itemView.findViewById(R.id.notification_list_time);

            constraintLayout = itemView.findViewById(R.id.notification_item);
        }
    }
}
