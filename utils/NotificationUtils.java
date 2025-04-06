package com.example.diplom.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.diplom.MainActivity;
import com.example.diplom.R;

import java.util.Date;

/**
 * Утилитный класс для работы с уведомлениями.
 */
public class NotificationUtils {

    private static final String CHANNEL_ID = "budget_optimizer_channel";
    private static final String CHANNEL_NAME = "Budget Optimizer";
    private static final String CHANNEL_DESCRIPTION = "Notifications for Budget Optimizer app";

    /**
     * Создает канал уведомлений (требуется для Android 8.0+)
     * @param context контекст приложения
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Отправляет уведомление пользователю
     * @param context контекст приложения
     * @param title заголовок уведомления
     * @param message текст уведомления
     * @param notificationId уникальный идентификатор уведомления
     */
    public static void sendNotification(Context context, String title, String message, int notificationId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }

    /**
     * Планирует уведомление на определенное время
     * @param context контекст приложения
     * @param title заголовок уведомления
     * @param message текст уведомления
     * @param triggerTime время, когда должно быть отправлено уведомление
     * @param notificationId уникальный идентификатор уведомления
     */
    public static void scheduleNotification(Context context, String title, String message, Date triggerTime, int notificationId) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("notification_id", notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime.getTime(), pendingIntent);
    }
}
