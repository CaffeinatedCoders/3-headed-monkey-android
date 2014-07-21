package net.three_headed_monkey.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class PassiveLocationUpdatesReceiver extends BroadcastReceiver {
    public static final String TAG = "PassiveLocationUpdatesReceiver";

    //Note: duplicate value in manifest, keep up to date
    public static final String INTENT_ACTION = "net.three_headed_monkey.intent.action.PASSIVE_LOCATION_UPDATE";

    public PassiveLocationUpdatesReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "PassiveLocationUpdatesReceiver called");
        if (intent == null)
            return;
        Bundle bundle = intent.getExtras();
        Location location = (Location) bundle.get(LocationManager.KEY_LOCATION_CHANGED);
        if (location == null)
            return;

        Intent intentService = new Intent(context, PassiveLocationUpdatesReceivedService.class);
        intentService.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        context.startService(intentService);
    }
}
