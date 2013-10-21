package net.three_headed_monkey.communication;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class NotificationManager {

    @Bean
    SmsSender smsSender;

    public enum NotificationType {
        NONE, ALL, SMS
    }

    public void sendNotification(NotificationType notificationType, String text){
        if(notificationType == NotificationType.SMS || notificationType == NotificationType.ALL){
            smsSender.sendSms(text);
        }
    }
}
