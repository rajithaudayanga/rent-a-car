package com.example.rentacar.Fragments;

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

import com.example.rentacar.Adapters.CustomerRentalsAdapter;
import com.example.rentacar.Adapters.ServiceRentalsAdapter;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.Rent;
import com.example.rentacar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RentalsFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private ArrayList<Rent> rentals_list;
    private String user_type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);

        //setup recycler view
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        //firebse
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        //array list
        rentals_list = new ArrayList<>();

        //get user type
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(DatabaseFields.sharedPrefferenceData.FILE_NAME, MODE_PRIVATE);
        user_type = sharedPreferences.getString(DatabaseFields.userFields.USER_TYPE, "");


        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("rentals").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Rent rent = snapshot.getValue(Rent.class);

                    if (rent != null) {
                        rentals_list.add(rent);
                    }
                }

                if (user_type != null && user_type.equals(DatabaseFields.constants.USER_TYPE_CUSTOMER)) {
                    CustomerRentalsAdapter customerRentalsAdapter = new CustomerRentalsAdapter(getContext(), rentals_list);
                    recyclerView.setAdapter(customerRentalsAdapter);
                    customerRentalsAdapter.notifyDataSetChanged();

                } else if (user_type != null && user_type.equals(DatabaseFields.constants.USER_TYPE_SERVICE)) {
                    ServiceRentalsAdapter serviceRentalsAdapter = new ServiceRentalsAdapter(getContext(), rentals_list);
                    recyclerView.setAdapter(serviceRentalsAdapter);
                    serviceRentalsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }
}
