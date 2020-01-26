package com.example.rentacar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rentacar.Adapters.MessageConversationviewAdapter;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Controllers.SaveUserImage;
import com.example.rentacar.Model.Message;
import com.example.rentacar.Model.Notification;
import com.example.rentacar.RoomDatabase.UserImages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageConversationActivity extends AppCompatActivity implements View.OnClickListener {

    private String partner_id, u_name;
    private ListView message_listview;
    private CircleImageView user_image;
    private TextView user_name;
    private ImageView back_btn, send_message;
    private EditText enter_message;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private ArrayList<Message> messages_list;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_conversation);

        initComponents();
        
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseReference.keepSynced(true);
        mFirebaseAuth = FirebaseAuth.getInstance();

        //set on click listeners
        back_btn.setOnClickListener(this);
        send_message.setOnClickListener(this);

        messages_list = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();

        enter_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    send_message.setEnabled(false);
                } else {
                    send_message.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                u_name = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (bundle != null) {
            partner_id = bundle.getString("partner_id");

            mDatabaseReference.child(partner_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user_name.setText(dataSnapshot.child("name").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            String image = Home.appDatabase.userImagesDao().hasImage(partner_id);

            if (image != null && !image.equals("")) {
                user_image.setImageBitmap(StringToBitMap(image));
            }

            childEventListener = mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(partner_id).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(partner_id).child(dataSnapshot.getKey()).child("status").setValue(DatabaseFields.messages.READ);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("messages").child(partner_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    messages_list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Message message = snapshot.getValue(Message.class);

                        if (message != null) {
                            messages_list.add(message);
                        }
                    }

                    MessageConversationviewAdapter messageConversationviewAdapter = new MessageConversationviewAdapter(getApplicationContext(), messages_list);
                    messageConversationviewAdapter.notifyDataSetChanged();
                    message_listview.setAdapter(messageConversationviewAdapter);
                    message_listview.setSelection(messageConversationviewAdapter.getCount() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    public void onClick(View v) {
        if (v == back_btn) {
            super.onBackPressed();
        } else if (v == send_message) {
            sendMessage();
        }
    }

    private void sendMessage() {

        String message_id = mDatabaseReference.child(partner_id).child("message").child(mFirebaseAuth.getCurrentUser().getUid()).push().getKey();
        String notification_id = mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("notifications").push().getKey();

        Message message = new Message(
                message_id,
                mFirebaseAuth.getCurrentUser().getUid(),
                partner_id,
                enter_message.getText().toString().trim(),
                new Date(),
                DatabaseFields.messages.UNREAD
        );


        Notification notification = new Notification(
                mFirebaseAuth.getCurrentUser().getUid(),
                "You have new message from " + u_name,
                "Press to read message",
                DatabaseFields.notification.NOTIFICATION_NEW_MESSAGE,
                new Date(),
                DatabaseFields.notification.NOTIFICATION_STATUS_UNREAD,
                null
        );

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put(mFirebaseAuth.getCurrentUser().getUid() + "/messages/" + partner_id + "/" + message_id, message);
        stringObjectHashMap.put(partner_id + "/notifications/" + notification_id, notification);
        stringObjectHashMap.put(partner_id + "/messages/" + mFirebaseAuth.getCurrentUser().getUid() + "/" + message_id, message);

        mDatabaseReference.updateChildren(stringObjectHashMap);
        enter_message.getText().clear();
        send_message.setEnabled(false);
    }

    private void initComponents() {
        message_listview = findViewById(R.id.message_list_view);
        user_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.user_name);
        back_btn = findViewById(R.id.conversation_back_btn);
        enter_message = findViewById(R.id.message_enter);
        send_message = findViewById(R.id.conversation_send);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("messages").child(partner_id).removeEventListener(childEventListener);
    }
}
