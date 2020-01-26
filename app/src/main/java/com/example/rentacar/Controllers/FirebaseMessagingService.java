package com.example.rentacar.Controllers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.rentacar.R;
import com.google.firebase.messaging.RemoteMessage;

import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;
import static androidx.core.app.NotificationCompat.DEFAULT_VIBRATE;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    int NOTIFICATION_ID = 100;
    String messageTitle, messageBody, partner_id;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            messageTitle = remoteMessage.getNotification().getTitle();
            messageBody = remoteMessage.getNotification().getBody();
            String[] args = remoteMessage.getNotification().getBodyLocalizationArgs();
            if (args != null && args.length > 0) {
                partner_id = args[0];
            }
            System.out.println(partner_id);
        }

        if (remoteMessage.getNotification() != null) {

//            Intent intent = new Intent(this, MessageConversationActivity.class);
////            intent.putExtra("partner_id", partner_id);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            createNotificationChannel();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getResources().getString(R.string.notification_channel);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
