package net.three_headed_monkey.commands;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.communication.OutgoingCommandCommunicationFactory;
import net.three_headed_monkey.service.CommandExecutorService;
import net.three_headed_monkey.utils.SecureSettingsUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FindGpsLocationCommand extends LooperCommand implements LocationListener {
    public final static String TAG = "FindGpsLocationCommand";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
    LocationManager locationManager;
    SecureSettingsUtils secureSettingsUtils;
    Location lastLocation = null;
    int oldLocationMode = -1;
    public int MAX_RESPONSES = 3;
    public int TIMEOUT_SECONDS = 5 * 60;
    //    public int TIMEOUT_SECONDS = 5;
    int responses;

    public FindGpsLocationCommand(ThreeHeadedMonkeyApplication application) {
        super(application);
        locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public long getTimeoutDelaySeconds() {
        return TIMEOUT_SECONDS;
    }

    @Override
    protected void doExecute(String command) {
        responses = 0;
        secureSettingsUtils = new SecureSettingsUtils(application);
        if (secureSettingsUtils.locationModeSettingsAvailable()) {
            oldLocationMode = secureSettingsUtils.getLocationMode();
            secureSettingsUtils.setLocationMode(Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
            Log.d(TAG, "GPS activated");
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);

    }

    @Override
    public void onBeforeTimeout() {
        sendResponse("FindGPSLocation timed out");
        super.onBeforeTimeout();
    }

    @Override
    public void onBeforeQuit() {
        locationManager.removeUpdates(this);
        if (oldLocationMode != -1) {
            secureSettingsUtils.setLocationMode(oldLocationMode);
            Log.d(TAG, "GPS setting restored");
        }

        // If we found a location, send them all to the webservices
        if(lastLocation != null){
            Intent intent = new Intent(application, CommandExecutorService.class);
            intent.putExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM, UpdateLocationHistoryCommand.COMMAND_STRING);
            intent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM, OutgoingCommandCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_BROADCAST);
            application.startService(intent);
        }

        super.onBeforeQuit();
    }

    @Override
    protected boolean respondsToCommand(String command) {
        return command.toLowerCase().equals("findGpsLocation".toLowerCase());
    }

    @Override
    protected String getShortUsageText() {
        return "findGpsLocation - Activates gps and tries to find an accurate location";
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location Change, accuracy: " + location.getAccuracy());
        if (location.getAccuracy() > 50)
            return;
        if (lastLocation != null) {
            //Check if either the location or the accuracy has changed less than we want to send a response for
            if (location.distanceTo(lastLocation) < 5 && Math.abs(lastLocation.getAccuracy() - location.getAccuracy()) < 5 && location.getAccuracy() > 10)
                return;
        }

        if (location.getAccuracy() > 10) {
            if (responses + 1 >= MAX_RESPONSES)
                return;
        }

        lastLocation = location;
        Log.d(TAG, "Sending location change response #" + (responses + 1));
        String response = "gps:";
        response += "\n  lat: " + location.getLatitude();
        response += "\n  long: " + location.getLongitude();
        response += "\n  acc: " + Math.round(location.getAccuracy()) + "m";
        response += "\n  time: " + dateFormat.format(new Date(location.getTime()));
        response += "\n";
        sendResponse(response);
        responses++;

        if (responses >= MAX_RESPONSES)
            cancelLooper();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
        if (s.equals(LocationManager.GPS_PROVIDER)) {
            sendResponse("Gps has been turned off");
            cancelLooper();
        }
    }
}
