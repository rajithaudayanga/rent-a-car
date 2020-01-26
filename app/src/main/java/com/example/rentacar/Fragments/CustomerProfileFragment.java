package com.example.rentacar.Fragments;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rentacar.Model.Customer;
import com.example.rentacar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CustomerProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView imageView;
    private TextView service_profile_telephone, service_profile_call_telephone;
    private View view;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth;

    private String mobile_number;
    private TextView cus_name;
    private String current_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_profile, container, false);

        initComponents();

        service_profile_call_telephone.setOnClickListener(this);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        current_id = firebaseAuth.getCurrentUser().getUid();

        Bundle bundle = getArguments();
        if (bundle != null) {
            current_id = bundle.getString("current_id");
        }
        mDatabaseReference.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Customer customer = dataSnapshot.getValue(Customer.class);

                if (customer != null) {
                    mobile_number = customer.getMobile();
                    service_profile_telephone.setText(customer.getMobile());
                    cus_name.setText(customer.getName());

                    Picasso.get().load(customer.getprofile_photo()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == service_profile_call_telephone) {
            dialCall(mobile_number);
        }
    }

    public void dialCall(String number){
        Uri uri = Uri.parse("tel:"+number);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(callIntent);
    }

    private void initComponents() {
        imageView = view.findViewById(R.id.service_profile_image);
        service_profile_call_telephone = view.findViewById(R.id.service_profile_call_telephone);
        service_profile_telephone = view.findViewById(R.id.service_profile_telephone);
        cus_name = view.findViewById(R.id.textView1);
    }
}
