package com.example.rentacar.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentacar.MessageConversationActivity;
import com.example.rentacar.Model.MessageUserList;
import com.example.rentacar.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.viewHolder> {

    private Context context;
    private ArrayList<MessageUserList> messageUserLists;

    public MessageListAdapter(Context context, ArrayList<MessageUserList> messageUserLists) {
        this.context = context;
        this.messageUserLists = messageUserLists;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_massage_list, viewGroup, false);
        return new MessageListAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        final MessageUserList messageUserList = messageUserLists.get(i);

        if (messageUserList.getUser_image_string() != null && !messageUserList.getUser_image_string().equals("")) {
            viewHolder.user_image.setImageBitmap(StringToBitMap(messageUserList.getUser_image_string()));
        }else {
            Picasso.get().load(messageUserList.getUser_image()).into(viewHolder.user_image);
        }

        viewHolder.user_name.setText(messageUserList.getUser_name());
        viewHolder.last_message_time.setText(messageUserList.getLast_message_time());

        if (viewHolder.last_message.length() > 30) {
            viewHolder.last_message.setText(messageUserList.getLast_message().substring(30));
        } else {
            viewHolder.last_message.setText(messageUserList.getLast_message());
        }

        if (Integer.parseInt(messageUserList.getMessage_count()) > 0) {
            viewHolder.message_count.setVisibility(View.VISIBLE);
            viewHolder.message_count.setText(messageUserList.getMessage_count());
            viewHolder.last_message_time.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            viewHolder.last_message.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        } else {
            viewHolder.message_count.setVisibility(View.GONE);
            viewHolder.last_message_time.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            viewHolder.last_message.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageConversationActivity.class);
                intent.putExtra("partner_id", messageUserList.getPartner_id());
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return messageUserLists.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        CircleImageView user_image;
        TextView user_name, last_message, last_message_time, message_count;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.item_message_list);
            user_image = itemView.findViewById(R.id.message_list_image);
            user_name = itemView.findViewById(R.id.message_list_name);
            last_message = itemView.findViewById(R.id.message_list_last_message);
            last_message_time = itemView.findViewById(R.id.message_list_message_time);
            message_count = itemView.findViewById(R.id.message_list_message_count);
        }
    }

    private Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
