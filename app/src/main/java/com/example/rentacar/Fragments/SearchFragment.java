package com.example.rentacar.Fragments;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentacar.Adapters.SearchListServiceAdapter;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.Model.Service;
import com.example.rentacar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private Button search_select_location;
    private View view;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Service> services_list;

    private RecyclerView search_recycleview;
    private String selected_town;
    private Button search_btn;
    private EditText search_service_name;
    private String selected_dis;
    private TextView services_not_found;

    private SweetAlertDialog sweetAlertDialog;
    private String selected_ser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        initComponents();

        //initialize database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //services list
        services_list = new ArrayList<>();

        //alert dialog
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setTitleText("Loading...");

        //setup reclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        search_recycleview.setHasFixedSize(true);
        search_recycleview.setNestedScrollingEnabled(false);
        search_recycleview.setLayoutManager(linearLayoutManager);
        search_recycleview.addItemDecoration(new DividerItemDecoration(search_recycleview.getContext(), DividerItemDecoration.VERTICAL));

        Bundle bundle = getArguments();

        if (bundle != null && bundle.size() > 0) {
            selected_town = bundle.getString("selected_town");
            selected_dis = bundle.getString("selected_district");
            selected_ser = bundle.getString("selected_service_name");
        }

        if (selected_dis != null || selected_town != null || selected_ser != null) {
            services_list.clear();

            // show progress
            sweetAlertDialog.show();

            databaseReference.child("Users").orderByChild(DatabaseFields.userFields.USER_TYPE).equalTo(DatabaseFields.constants.USER_TYPE_SERVICE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    services_list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Service service = snapshot.getValue(Service.class);
                        if (service != null) {
                            service.setId(snapshot.getKey());
                            if (selected_ser != null && !TextUtils.isEmpty(selected_ser)) {
                                if (service.getName().contains(selected_ser)) {
                                    services_list.add(service);
                                }
                            } else if (!TextUtils.isEmpty(selected_town)) {
                                if (service.getTown().equalsIgnoreCase(selected_town)) {
                                    services_list.add(service);
                                }
                            } else if (!TextUtils.isEmpty(selected_dis)) {
                                if (service.getCity().equalsIgnoreCase(selected_dis)) {
                                    services_list.add(service);
                                }
                            }
                        }
                    }
                    if (services_list.size() > 0) {
                        services_not_found.setVisibility(View.GONE);
                        search_recycleview.setVisibility(View.VISIBLE);

                        //set adapter
                        SearchListServiceAdapter searchListServiceAdapter = new SearchListServiceAdapter(getContext(), services_list);
                        search_recycleview.setAdapter(searchListServiceAdapter);
                        searchListServiceAdapter.notifyDataSetChanged();
                    } else {
                        services_not_found.setVisibility(View.VISIBLE);
                        search_recycleview.setVisibility(View.GONE);
                    }
                    sweetAlertDialog.dismissWithAnimation();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            services_list.clear();

            // show progress
            sweetAlertDialog.setTitleText("Loading...");
            sweetAlertDialog.show();

            databaseReference.child("Users").orderByChild(DatabaseFields.userFields.USER_TYPE).equalTo(DatabaseFields.constants.USER_TYPE_SERVICE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    services_list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Service service = snapshot.getValue(Service.class);

                        if (service != null) {
                            service.setId(snapshot.getKey());
                            services_list.add(service);
                        }
                    }

                    if (services_list.size() > 0) {
                        services_not_found.setVisibility(View.GONE);
                        search_recycleview.setVisibility(View.VISIBLE);

                        //set adapter
                        SearchListServiceAdapter searchListServiceAdapter = new SearchListServiceAdapter(getContext(), services_list);
                        search_recycleview.setAdapter(searchListServiceAdapter);
                        searchListServiceAdapter.notifyDataSetChanged();
                    } else {
                        services_not_found.setVisibility(View.VISIBLE);
                        search_recycleview.setVisibility(View.GONE);
                    }
                    //hide alert dialog
                    sweetAlertDialog.dismissWithAnimation();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //set on click listeners
        search_select_location.setOnClickListener(this);
        search_btn.setOnClickListener(this);

        return view;
    }

    private void initComponents() {
        search_select_location = view.findViewById(R.id.search_select_location);
        search_recycleview = view.findViewById(R.id.search_recycleview);
        search_btn = view.findViewById(R.id.search_btn);
        search_service_name = view.findViewById(R.id.search_service_name);
        services_not_found = view.findViewById(R.id.services_not_found);
    }

    @Override
    public void onClick(View v) {
        if (v == search_select_location) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_content_frame, new SelectLocationFragment()).commit();
        } else if (v == search_btn) {

            hideKeyboard();
            final String search = search_service_name.getText().toString().trim();

            if (!TextUtils.isEmpty(search)) {
                HashMap<String, String> searches = new HashMap<>();
                searches.put("key_word", search);

                databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("recent_searches").push().setValue(searches).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            databaseReference.child("Users").orderByChild("name").startAt(search).endAt(search + '\uf8ff').addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    services_list.clear();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (snapshot.child(DatabaseFields.userFields.USER_TYPE).getValue(String.class).equals(DatabaseFields.constants.USER_TYPE_SERVICE)) {
                                            Service service = snapshot.getValue(Service.class);
                                            if (service != null) {
                                                service.setId(snapshot.getKey());
                                                services_list.add(service);
                                            }
                                        }
                                    }
                                    if (services_list.size() > 0) {
                                        services_not_found.setVisibility(View.GONE);
                                        search_recycleview.setVisibility(View.VISIBLE);
                                        //set adapter
                                        SearchListServiceAdapter searchListServiceAdapter = new SearchListServiceAdapter(getContext(), services_list);
                                        search_recycleview.setAdapter(searchListServiceAdapter);
                                        searchListServiceAdapter.notifyDataSetChanged();
                                    } else {
                                        services_not_found.setVisibility(View.VISIBLE);
                                        search_recycleview.setVisibility(View.GONE);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Toasty.error(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toasty.info(getContext(), "Please enter a service name", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideKeyboard();
    }
}
