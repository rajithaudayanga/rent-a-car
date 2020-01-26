package com.example.rentacar.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.rentacar.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SwipeAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> image_list;
    private LayoutInflater layoutInflater;
    private int cus_position = 0;

    public SwipeAdapter(Context context, ArrayList<String> image_list) {
        this.context = context;
        this.image_list = image_list;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        if (cus_position == image_list.size()) {
            cus_position = 0;
        }

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.swipe_imageview, container, false);

        ImageView imageView = view.findViewById(R.id.details_image);
        Picasso.get().load(image_list.get(cus_position)).into(imageView);
        cus_position++;
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
