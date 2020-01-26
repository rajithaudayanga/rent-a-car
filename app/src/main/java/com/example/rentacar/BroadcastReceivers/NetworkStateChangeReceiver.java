package com.example.rentacar.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rentacar.Controllers.CheckConnection;
import com.example.rentacar.Controllers.DatabaseFields;
import com.example.rentacar.R;

public class NetworkStateChangeReceiver extends BroadcastReceiver {

    private Context context;
    private LinearLayout linearLayout;
    private TextView textView;

    public NetworkStateChangeReceiver(Context context, TextView textView, LinearLayout constraintLayout) {
        this.context = context;
        this.textView = textView;
        this.linearLayout = constraintLayout;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (isConnected(context)) {
            CheckConnection checkConnection = new CheckConnection(context, textView, linearLayout);
            checkConnection.execute();
        } else {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.error_show_animation);
            animation.setDuration(1000);
            linearLayout.setVisibility(View.VISIBLE);
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
            textView.setText(DatabaseFields.connection.NO_CONNECTION);
            linearLayout.setAnimation(animation);
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnected();

    }
}
