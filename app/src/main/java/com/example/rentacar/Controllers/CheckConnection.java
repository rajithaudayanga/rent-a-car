package com.example.rentacar.Controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.rentacar.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckConnection extends AsyncTask<Void, Void, Boolean> {

    private String url = null;
    private WeakReference<Context> context;
    private WeakReference<TextView> textView;
    private WeakReference<LinearLayout> layoutWeakReference;

    public CheckConnection(Context context, TextView textView, LinearLayout constraintLayout) {
        this.context = new WeakReference<>(context);
        this.textView = new WeakReference<>(textView);
        this.layoutWeakReference = new WeakReference<>(constraintLayout);
    }

    @Override
    protected void onPreExecute() {
        url = "https://google.com";
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            URL url = new URL(this.url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            return httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            if (layoutWeakReference.get().getVisibility() == View.VISIBLE) {
                Animation animation = AnimationUtils.loadAnimation(context.get(), R.anim.error_end_animation);
                animation.setDuration(1000);
                layoutWeakReference.get().setBackgroundColor(context.get().getResources().getColor(R.color.colorPrimaryDark));
                textView.get().setText(DatabaseFields.connection.SUCCESS);
                layoutWeakReference.get().setAnimation(animation);
                layoutWeakReference.get().setVisibility(View.INVISIBLE);
            }
        }else {
            Animation animation = AnimationUtils.loadAnimation(context.get(), R.anim.error_show_animation);
            animation.setDuration(1000);
            layoutWeakReference.get().setVisibility(View.VISIBLE);
            layoutWeakReference.get().setBackgroundColor(context.get().getResources().getColor(R.color.colorRed));
            textView.get().setText(DatabaseFields.connection.FAIL);
            layoutWeakReference.get().setAnimation(animation);
        }
    }
}
