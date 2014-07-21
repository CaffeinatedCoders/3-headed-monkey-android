package net.three_headed_monkey.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import net.three_headed_monkey.data.LocationHistoryDatabase;


public class PassiveLocationUpdatesReceivedService extends IntentService {
    public static final String TAG = "PassiveLocationUpdatesReceivedService";

    private static final float MIN_DISTANCE_CHANGE = 7;

    public PassiveLocationUpdatesReceivedService() {
        super("PassiveLocationUpdatesReceivedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null)
            return;
        Bundle bundle = intent.getExtras();
        Location location = (Location) bundle.get(LocationManager.KEY_LOCATION_CHANGED);
        if (location == null)
            return;

        Log.d(TAG, "Provider: " + location.getProvider());
        Log.d(TAG, "Latitude: " + location.getLatitude());
        Log.d(TAG, "Longitude: " + location.getLongitude());
        Log.d(TAG, "Accuracy: " + location.getAccuracy());

        LocationHistoryDatabase db = new LocationHistoryDatabase(getApplicationContext());

        LocationHistoryDatabase.LocationContainer locationContainer = db.getLast();
        Location lastLocation = null;
        if (locationContainer != null)
            lastLocation = locationContainer.location;

        if (lastLocation != null) {
            Log.d(TAG, "Last Location: " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude() + " acc: " + lastLocation.getAccuracy());
            Log.d(TAG, "Distance to last location: " + location.distanceTo(lastLocation));
        }

        if (lastLocation == null || lastLocation.distanceTo(location) >= MIN_DISTANCE_CHANGE || location.getAccuracy() < lastLocation.getAccuracy()) {
            if (lastLocation != null && lastLocation.distanceTo(location) < MIN_DISTANCE_CHANGE && location.getAccuracy() < lastLocation.getAccuracy()) {
                //We only add the location because of the better accuracy, so delete the last location to avoid duplicate values
                Log.d(TAG, "Deleting last location with id: " + locationContainer.id);
                db.delete(locationContainer.id);
            }
            db.insert(location);
        } else {
            Log.d(TAG, "Location or accuracy change to small to record");
        }

        db.close();
    }

}


