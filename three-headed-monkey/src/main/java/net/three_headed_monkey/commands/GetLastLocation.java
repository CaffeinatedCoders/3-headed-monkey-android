package net.three_headed_monkey.commands;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.data.LocationHistoryDatabase;

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
        LocationHistoryDatabase locationDB = new LocationHistoryDatabase(application);
        LocationHistoryDatabase.LocationContainer locC = locationDB.getLast();
        Location location = null;
        if (locC != null)
            location = locC.location;

        if(location != null) {
            response += "provider: " + location.getProvider();
            response += "\nlat: " + location.getLatitude();
            response += "\nlong: " + location.getLongitude();
            response += "\nacc: " + Math.round(location.getAccuracy()) + "m";
            response += "\ntime: " + dateFormat.format(new Date(location.getTime()));
            response += "\n";
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
