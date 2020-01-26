package com.example.rentacar.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rentacar.Adapters.MessageListAdapter;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Controllers.SaveUserImage;
import com.example.rentacar.Home;
import com.example.rentacar.Model.Message;
import com.example.rentacar.Model.MessageUserList;
import com.example.rentacar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;

import static android.text.format.DateUtils.FORMAT_NUMERIC_DATE;
import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

public class MessageFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private ArrayList<MessageUserList> message_list;
    private ValueEventListener messages;


    @Override
    public void onResume() {
        super.onResume();
        messages = databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();

        message_list = new ArrayList<>();

        return view;
    }

    public void loadData() {
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                message_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String partner_id = snapshot.getKey();
                    final long max_count = dataSnapshot.getChildrenCount();

                    final MessageUserList messageUserList = new MessageUserList();
                    messageUserList.setPartner_id(partner_id);

                    if (partner_id != null) {
                        databaseReference.child(partner_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                String partner_name = dataSnapshot1.child("name").getValue(String.class);
                                String partner_image = dataSnapshot1.child(DatabaseFields.userFields.PROFILE_PHOTO).getValue(String.class);

                                String image = Home.appDatabase.userImagesDao().hasImage(partner_id);

                                if (image != null && !image.equals("")) {
                                    messageUserList.setUser_image_string(image);
                                } else {
                                    SaveUserImage saveUserImages = new SaveUserImage();
                                    saveUserImages.execute(partner_id, partner_image);
                                    messageUserList.setUser_image(partner_image);
                                }
                                messageUserList.setUser_name(partner_name);

                                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("messages").child(partner_id).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                        for (DataSnapshot snapshot1 : dataSnapshot2.getChildren()) {
                                            Message message = snapshot1.getValue(Message.class);

                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTime(message.getTime());
                                            messageUserList.setLast_message(message.getMessage());
                                            messageUserList.setLast_message_time((String) DateUtils.getRelativeTimeSpanString(calendar.getTimeInMillis(), Calendar.getInstance().getTimeInMillis(), MINUTE_IN_MILLIS, FORMAT_NUMERIC_DATE));

                                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("messages").child(partner_id).orderByChild("status").equalTo(DatabaseFields.messages.UNREAD).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    messageUserList.setMessage_count(dataSnapshot.getChildrenCount() + "");

                                                    message_list.add(messageUserList);

                                                    if (message_list.size() == max_count) {
                                                        MessageListAdapter messageListAdapter = new MessageListAdapter(getContext(), message_list);
                                                        recyclerView.setAdapter(messageListAdapter);
                                                        messageListAdapter.notifyDataSetChanged();

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (firebaseAuth != null && firebaseAuth.getCurrentUser() != null && messages != null) {
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("messages").removeEventListener(messages);
        }
    }
}

