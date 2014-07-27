package net.three_headed_monkey.test_utils;

import android.location.Location;
import android.location.LocationManager;

public class LocationUtils {
    public static Location createLocation(double offset) {
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(47.073557 + offset);
        location.setLongitude(15.437717 + offset);
        location.setAltitude(500);
        location.setAccuracy(20);
        location.setTime(0);
        return location;
    }
}
