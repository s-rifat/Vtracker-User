package com.example.vtracker2.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContextWrapper;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.vtracker2.R;


public class NotificationHelper extends ContextWrapper {

    private static final String RIFAT_CHANNEL_ID = "com.example.vtracker2";
    private static final String RIFAT_CHANNEL_NAME = "vtracker2";

    private NotificationManager manager;

    public NotificationHelper(Context base)
    {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createChannel();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel rifatChannel = new NotificationChannel(RIFAT_CHANNEL_ID,RIFAT_CHANNEL_NAME
        ,NotificationManager.IMPORTANCE_DEFAULT);

        rifatChannel.enableLights(false);
        rifatChannel.enableVibration(true);
        rifatChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(rifatChannel);
    }

    public NotificationManager getManager() {

        if(manager == null)
        {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getRealtimeTrackingNotification(String title, String content, Uri defaultSound) {
        return new Notification.Builder(getApplicationContext(),RIFAT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultSound)
                .setAutoCancel(false);
    }
}
//16:00