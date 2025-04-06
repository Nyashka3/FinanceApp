package com.example.diplom.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * BroadcastReceiver для обработки запланированных уведомлений.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        int notificationId = intent.getIntExtra("notification_id", 0);

        // Отправляем уведомление
        NotificationUtils.sendNotification(context, title, message, notificationId);
    }
}
