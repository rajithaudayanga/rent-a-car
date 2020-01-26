package com.example.rentacar.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rentacar.Fragments.SearchFragment;
import com.example.rentacar.Model.SearchHistory;
import com.example.rentacar.R;

import java.util.ArrayList;

public class RecentSearchListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SearchHistory> history_list;

    public RecentSearchListAdapter(Context context, ArrayList<SearchHistory> history_list) {
        this.context = context;
        this.history_list = history_list;
    }

    @Override
    public int getCount() {
        return history_list.size();
    }

    @Override
    public Object getItem(int position) {
        return history_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder viewHolder = new viewHolder();
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_recent_searches, parent, false);

        }

        final SearchHistory searchHistory = history_list.get(position);

        viewHolder.searches = convertView.findViewById(R.id.search_text);
        viewHolder.search_dis = convertView.findViewById(R.id.search_dis);
        viewHolder.recent = convertView.findViewById(R.id.recent_searches);

        if (TextUtils.isEmpty(searchHistory.getKey_word())) {
            if (TextUtils.isEmpty(searchHistory.getTown())) {
                viewHolder.searches.setText(searchHistory.getDistrict());
                viewHolder.search_dis.setVisibility(View.GONE);
            } else {
                viewHolder.search_dis.setVisibility(View.VISIBLE);
                viewHolder.searches.setText(searchHistory.getTown());
                viewHolder.search_dis.setText(searchHistory.getDistrict());
            }
        } else {
            viewHolder.searches.setText(searchHistory.getKey_word());
            viewHolder.search_dis.setVisibility(View.GONE);
        }

        viewHolder.recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("selected_town", searchHistory.getTown());
                bundle.putString("selected_district", searchHistory.getDistrict());
                bundle.putString("selected_service_name", searchHistory.getKey_word());

                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);

                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.home_content_frame, searchFragment).commit();
            }
        });

        return convertView;
    }

    static class viewHolder {
        TextView searches, search_dis;
        ConstraintLayout recent;
    }
}
