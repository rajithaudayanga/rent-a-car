package com.example.rentacar.Fragments;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentacar.Adapters.RecentSearchListAdapter;
import com.example.rentacar.Model.IOnBackPressed;
import com.example.rentacar.Model.SearchHistory;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;

import es.dmoral.toasty.Toasty;

public class SelectLocationFragment extends Fragment implements View.OnFocusChangeListener, AdapterView.OnItemClickListener, View.OnClickListener, IOnBackPressed {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private AutoCompleteTextView select_location_district, select_location_town;
    private ArrayList<String> districts, towns;
    private LinkedHashSet<SearchHistory> recent_searches;
    private Button select_location_btn;
    private View view;
    private ListView listView;
    private ImageView delete_recent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_location, container, false);

        initComponents();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference.keepSynced(true);

        districts = new ArrayList<>();
        towns = new ArrayList<>();
        recent_searches = new LinkedHashSet<>();

        mDatabaseReference.child("Locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                districts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    districts.add(snapshot.getKey());
                }

                assert getContext() != null;
                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, districts);
                select_location_district.setThreshold(0);
                select_location_district.setAdapter(stringArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabaseReference.child("Users").child(mFirebaseAuth.getCurrentUser().getUid()).child("recent_searches").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recent_searches.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SearchHistory searchHistory = snapshot.getValue(SearchHistory.class);

                    recent_searches.add(searchHistory);
                }
                ArrayList<SearchHistory> searchHistories = new ArrayList<>(recent_searches);
                Collections.reverse(searchHistories);
                RecentSearchListAdapter recentSearchListAdapter = new RecentSearchListAdapter(getContext(), searchHistories);
                listView.setAdapter(recentSearchListAdapter);
                recentSearchListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //add listeners
        select_location_town.setOnFocusChangeListener(this);
        select_location_district.setOnFocusChangeListener(this);
        select_location_district.setOnItemClickListener(this);
        select_location_btn.setOnClickListener(this);
        delete_recent.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            select_location_district.setShowSoftInputOnFocus(false);
            select_location_town.setShowSoftInputOnFocus(false);
        }

        return view;
    }

    private void initComponents() {
        select_location_district = view.findViewById(R.id.select_location_district);
        select_location_town = view.findViewById(R.id.select_location_town);
        select_location_btn = view.findViewById(R.id.select_location_btn);
        listView = view.findViewById(R.id.recent_searches_list);
        delete_recent = view.findViewById(R.id.delete_recent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == select_location_town) {
            if (hasFocus) {
                if (TextUtils.isEmpty(select_location_district.getText())) {
                    Toasty.info(getContext(), "Please Select District First", Toast.LENGTH_SHORT).show();
                }else {
                    if (towns.size() > 0) {
                    select_location_town.showDropDown();
                    }
                }
            }
        } else if (v == select_location_district) {
            if (hasFocus) {
                select_location_district.showDropDown();
            } else {
                select_location_district.dismissDropDown();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view;
        String dist = textView.getText().toString().trim();

        select_location_town.setText("", false);

        mDatabaseReference.child("Locations").child(dist).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                towns.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    towns.add(snapshot.child("name").getValue(String.class));
                }

                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_spinner, towns);
                select_location_town.setAdapter(stringArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == select_location_btn) {

            final String sele_town = select_location_town.getText().toString();
            final String sele_dis = select_location_district.getText().toString();

            HashMap<String, String> searches = new HashMap<>();
            searches.put("town", sele_town);
            searches.put("district", sele_dis);

            if (!TextUtils.isEmpty(sele_town) || !TextUtils.isEmpty(sele_dis)) {
                mDatabaseReference.child("Users").child(mFirebaseAuth.getCurrentUser().getUid()).child("recent_searches").push().setValue(searches).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Bundle bundle = new Bundle();
                            bundle.putString("selected_town", sele_town);
                            bundle.putString("selected_district", sele_dis);

                            SearchFragment searchFragment = new SearchFragment();
                            searchFragment.setArguments(bundle);

                            assert getActivity() != null;
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_content_frame, searchFragment).commitAllowingStateLoss();
                        }
                    }
                });
            } else {
                assert getContext() != null;
                Toasty.info(getContext(), "Please enter a location", Toast.LENGTH_SHORT).show();
            }
        }
        if (v == delete_recent) {
            mDatabaseReference.child("Users").child(mFirebaseAuth.getCurrentUser().getUid()).child("recent_searches").setValue(null);
        }
    }

    @Override
    public boolean onBackPressed() {
        assert getActivity() != null;
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_content_frame, new SearchFragment()).commit();
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
