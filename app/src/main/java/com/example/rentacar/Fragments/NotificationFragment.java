package com.example.rentacar.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rentacar.Adapters.NotificationAdapter;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.Notification;
import com.example.rentacar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationFragment extends Fragment {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    private ArrayList<Notification> notifications_list;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);

        initComponents(view);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mFirebaseAuth = FirebaseAuth.getInstance();

        notifications_list = new ArrayList<>();

        //setup recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifications_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);

                    if (notification != null && !notification.getNotification_type().equalsIgnoreCase(DatabaseFields.notification.NOTIFICATION_NEW_MESSAGE)) {
                        notification.setNotification_id(snapshot.getKey());
                        notifications_list.add(notification);
                    }
                }
                Collections.reverse(notifications_list);
                NotificationAdapter notificationAdapter = new NotificationAdapter(getContext(), notifications_list);
                mRecyclerView.setAdapter(notificationAdapter);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void initComponents(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerview);
    }
}
