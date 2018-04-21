package com.djgeraldo.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.djgeraldo.R;
import com.djgeraldo.activities.SplashActivity;
import com.djgeraldo.phonemidea.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "mytag";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Utils.getInstance().d("data" + remoteMessage.getData());
            showNotification(remoteMessage.getData().get("body"));
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
//       Utils.getInstance().d("noti"+remoteMessage.getNotification());
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification().getBody());
        }
    }


    @SuppressLint("WrongConstant")
    private void showNotification(String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Intent resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.setFlags(Notification.FLAG_AUTO_CANCEL);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);
        mBuilder.setSmallIcon(R.drawable.dj_gradlo_app_logo);
        mBuilder.setContentTitle("Dj Gelardo");
        mBuilder.setContentText(message);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.dj_gradlo_app_logo);
        mBuilder.setLargeIcon(icon);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
        }
        mBuilder.setContentIntent(intent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }


}
