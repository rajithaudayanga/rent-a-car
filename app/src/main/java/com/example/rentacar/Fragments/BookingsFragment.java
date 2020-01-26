package com.example.rentacar.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.rentacar.Adapters.BookingAdapter;
import com.example.rentacar.Adapters.CustomerBookingAdapter;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.BookingDetails;
import com.example.rentacar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookingsFragment extends Fragment {

    private DatabaseReference mDatabaseReference;
    private ArrayList<BookingDetails> bookingDetails_list;
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView recyclerView;
    private SweetAlertDialog sweetAlertDialog;
    private String user_type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);

        initComponents(view);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mFirebaseAuth = FirebaseAuth.getInstance();

        //alert dialog
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Loading...");
        sweetAlertDialog.setCancelable(false);

        //setup recyclerview
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        //array list
        bookingDetails_list = new ArrayList<>();

        //get user type
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(DatabaseFields.sharedPrefferenceData.FILE_NAME, Context.MODE_PRIVATE);
        user_type = sharedPreferences.getString(DatabaseFields.userFields.USER_TYPE, "");


        if (user_type != null) {
            sweetAlertDialog.show();
            mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("bookings").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    bookingDetails_list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BookingDetails bookingDetails = snapshot.getValue(BookingDetails.class);

                        if (bookingDetails != null) {
                            bookingDetails.setId(snapshot.getKey());
                            bookingDetails_list.add(bookingDetails);
                        }
                    }

                    Collections.reverse(bookingDetails_list);
                    if (user_type.equals(DatabaseFields.constants.USER_TYPE_SERVICE)) {
                        BookingAdapter bookingAdapter = new BookingAdapter(getContext(), bookingDetails_list);
                        recyclerView.setAdapter(bookingAdapter);
                        bookingAdapter.notifyDataSetChanged();
                    }else if (user_type.equals(DatabaseFields.constants.USER_TYPE_CUSTOMER)) {
                        CustomerBookingAdapter customerBookingAdapter = new CustomerBookingAdapter(getContext(), bookingDetails_list);
                        recyclerView.setAdapter(customerBookingAdapter);
                        customerBookingAdapter.notifyDataSetChanged();
                    }

                    sweetAlertDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return view;
    }

    private void initComponents(View view) {
        recyclerView = view.findViewById(R.id.recyclerview);
    }
}
