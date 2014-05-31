package net.three_headed_monkey.commands;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.utils.SecureSettingsUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class GetLastLocation extends Command {
    LocationManager locationManager;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm");

    public GetLastLocation(ThreeHeadedMonkeyApplication application) {
        super(application);
        locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void doExecute(String command) {
        String response = "";
        Location location;
        SecureSettingsUtils secureSettingsUtils = new SecureSettingsUtils(application);
        int locationmode = -1;
        if(secureSettingsUtils.locationModeSettingsAvailable()) {
            locationmode = secureSettingsUtils.getLocationMode();
            secureSettingsUtils.setLocationMode(Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        }

        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null) {
            response += "net:";
            response += "\n  lat: " + location.getLatitude();
            response += "\n  long: " + location.getLongitude();
            response += "\n  acc: " + Math.round(location.getAccuracy()) + "m";
            response += "\n  time: " + dateFormat.format(new Date(location.getTime()));
            response += "\n";
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            response += "gps:";
            response += "\n  lat: " + location.getLatitude();
            response += "\n  long: " + location.getLongitude();
            response += "\n  acc: " + Math.round(location.getAccuracy()) + "m";
            response += "\n  time: " + dateFormat.format(new Date(location.getTime()));
            response += "\n";
        }

        if(locationmode != -1) {
            secureSettingsUtils.setLocationMode(locationmode);
        }

        if(response.isEmpty())
            response = "No last known location";
        sendResponse(response);
    }

    @Override
    protected boolean respondsToCommand(String command) {
        return command.toUpperCase().equals("getLastLocation".toUpperCase());
    }

    @Override
    protected String getShortUsageText() {
        return "getLastLocation - Gets the last known location, this is fast but might be out of date";
    }
}
