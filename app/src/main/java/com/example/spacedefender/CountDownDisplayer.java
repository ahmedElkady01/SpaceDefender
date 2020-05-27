package com.example.spacedefender;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.snackbar.Snackbar;

import static androidx.core.content.ContextCompat.getSystemService;

public class CountDownDisplayer {

    private static CountDownDisplayer instance;
    private CountDownTimer timer;

    private Context context;

    private CountDownDisplayer() {}

    public static CountDownDisplayer getInstance() {
        if (instance == null) {
            instance = new CountDownDisplayer();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = getTimer();
        timer.start();
    }

    private CountDownTimer getTimer() {
        return new CountDownTimer(10 * 1000,  1000) {

            public void onTick(long millisUntilFinished) {
                System.out.println("Count down remaining: " + millisUntilFinished/1000);
            }
            public void onFinish() {
                sendNotification();
            }
        };
    }

    private void sendNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel mChannel = new NotificationChannel(
                channelId, channelName, importance);
        notificationManager.createNotificationChannel(mChannel);

        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentText("Time up ")
                .setContentTitle("Notification")
                .setSmallIcon(android.R.drawable.ic_menu_save)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        notificationManager.notify(1, notification);
    }
}
