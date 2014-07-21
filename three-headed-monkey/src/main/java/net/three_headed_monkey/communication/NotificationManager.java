package net.three_headed_monkey.communication;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import net.three_headed_monkey.R;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

@EBean
public class NotificationManager {
    public static final String TAG = "3hmNotificationManager";

    @Bean
    SmsSender smsSender;

    @RootContext
    Context context;

    @SystemService
    android.app.NotificationManager notificationService;

    public enum NotificationType {
        NONE, ALL, SMS
    }

    public void sendNotification(NotificationType notificationType, String text) {
        boolean nosms = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_bool_nosms", false);

        Log.d(TAG, "Send Notification, Type: " + notificationType.toString() + " nosms: " + nosms);

        if (notificationType == NotificationType.SMS || notificationType == NotificationType.ALL) {
            if (!nosms) {
                smsSender.sendSms(text);
            } else {
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("3hm dev nosms notification")
                                .setContentText(text);

                notificationService.notify(0, notificationBuilder.build());

            }
        }
    }
}
