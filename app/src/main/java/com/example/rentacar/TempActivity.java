package com.example.rentacar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.rentacar.Fragments.CustomerProfileFragment;

public class TempActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String action = bundle.getString("action");

            assert action != null;
            if (action.equalsIgnoreCase("cus_details")) {
                Bundle bundle1 = new Bundle();
                bundle.putString("current_id", bundle.getString("cus_id"));

                CustomerProfileFragment customerProfileFragment = new CustomerProfileFragment();
                customerProfileFragment.setArguments(bundle1);
                loadFragment(customerProfileFragment);
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
    }
}
