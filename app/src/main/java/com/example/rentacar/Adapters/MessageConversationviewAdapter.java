package com.example.rentacar.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rentacar.Home;
import com.example.rentacar.Model.Message;
import com.example.rentacar.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

import static android.text.format.DateUtils.DAY_IN_MILLIS;
import static android.text.format.DateUtils.FORMAT_NUMERIC_DATE;
import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

public class MessageConversationviewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Message> messages_list;

    public MessageConversationviewAdapter(Context context, ArrayList<Message> messages_list) {
        this.context = context;
        this.messages_list = messages_list;
    }

    @Override
    public int getCount() {
        return messages_list.size();
    }

    @Override
    public Object getItem(int position) {
        return messages_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        Message message = messages_list.get(position);



        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser().getUid().equalsIgnoreCase(message.getSender_id())) {
            convertView = messageInflater.inflate(R.layout.item_own_message, null);

            //init image view
            holder.imageView = convertView.findViewById(R.id.user_image);

            String image = Home.appDatabase.userImagesDao().hasImage(firebaseAuth.getCurrentUser().getUid());

            if (image != null && !image.equals("")) {
                holder.imageView.setImageBitmap(StringToBitMap(image));
            }


        } else {
            convertView = messageInflater.inflate(R.layout.item_another_message, null);

            //init image view
            holder.imageView = convertView.findViewById(R.id.user_image);

            String image = Home.appDatabase.userImagesDao().hasImage(message.getSender_id());

            if (image != null && !image.equals("")) {
                holder.imageView.setImageBitmap(StringToBitMap(image));
            }
        }

        holder.message_content = convertView.findViewById(R.id.message_content);
        holder.time = convertView.findViewById(R.id.message_time);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(message.getTime());

        holder.message_content.setText(message.getMessage());
        holder.time.setText(DateUtils.getRelativeDateTimeString(context, calendar.getTimeInMillis(), MINUTE_IN_MILLIS, DAY_IN_MILLIS, FORMAT_NUMERIC_DATE));

        return convertView;
    }

    private class MessageViewHolder {
        TextView message_content, time;
        ImageView imageView;
    }

    private Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
