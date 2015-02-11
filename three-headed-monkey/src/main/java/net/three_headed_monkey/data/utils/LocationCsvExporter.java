package net.three_headed_monkey.data.utils;

import android.location.Location;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LocationCsvExporter {
    public LocationCsvExporter() {
    }

    public String export(List<Location> locations) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = "Latitude,Longitude,Altitude,Accuracy,Provider,Time\n";
        for (Location l : locations) {
            String lat = String.valueOf(l.getLatitude());
            String lon = String.valueOf(l.getLongitude());
            String alt = String.valueOf(l.getAltitude());
            String acc = String.valueOf(l.getAccuracy());
            String prov = l.getProvider();
            String time = dateFormat.format(new Date(l.getTime()));
            result += StringUtils.join(new String[]{lat, lon, alt, acc, prov, time}, ",");
            result += "\n";
        }
        return result;
    }
}
