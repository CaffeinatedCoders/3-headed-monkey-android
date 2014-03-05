package net.three_headed_monkey.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication_;
import net.three_headed_monkey.communication.NotificationManager;
import net.three_headed_monkey.communication.NotificationManager_;



public class SimCardCheckService extends IntentService {
    public static final String TAG="SimCardCheckService";

    public SimCardCheckService() {
        super("SimCardCheckService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "SimCardCheckService triggered");
        ThreeHeadedMonkeyApplication_ application = (ThreeHeadedMonkeyApplication_) getApplication();
        boolean sim_authorized = application.simCardSettings.currentSimCardAuthorized();
        Log.d("TAG", "SimCardCheckService, Card authorized: " + sim_authorized);
        if(!sim_authorized){
            String text = "An unauthorized simcard has been put into Mr. Smiths phone";
            NotificationManager_ notificationManager = NotificationManager_.getInstance_(getApplication());
            notificationManager.sendNotification(NotificationManager.NotificationType.ALL, text);
        }
    }

}
