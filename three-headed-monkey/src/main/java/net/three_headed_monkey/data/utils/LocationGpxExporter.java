package net.three_headed_monkey.data.utils;


import android.location.Location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocationGpxExporter {
    public static final String GPX_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n" +
            "<gpx version=\"1.1\"\n" +
            " creator=\"3-headed-monkey\"\n" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            " xmlns=\"http://www.topografix.com/GPX/1/1\"\n" +
            " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n" +
            " <trk>\n" +
            " <name>3-headed-monkey location history</name>\n";

    public static final String GPX_FOOTER = " </trk>\n</gpx>\n";
    public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public LocationGpxExporter() {
    }

    public String export(List<Location> locations) {
        String result = "";
        Date last_location_date = null;

        result += GPX_HEADER;
        for (Location location : locations) {
            Date location_date = new Date(location.getTime());
            if (last_location_date == null) {
                result += "  <trkseg>\n";
            } else if (!datesWithoutTimeEqual(location_date, last_location_date)) {
                result += "  </trkseg>\n";
                result += "  <trkseg>\n";
            }
            result += "    <trkpt lat=\"" + location.getLatitude() + "\" lon=\"" + location.getLongitude() + "\"><time>" + dateFormat.format(location_date) + "</time><ele>" + location.getAltitude() + "</ele></trkpt>\n";
            last_location_date = location_date;
        }
        result += "  </trkseg>\n";

        result += GPX_FOOTER;
        return result;
    }

    private boolean datesWithoutTimeEqual(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return false;
        if (date1.equals(date2))
            return true;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date d1 = calendar.getTime();

        calendar.setTime(date2);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date d2 = calendar.getTime();

        return d1.equals(d2);
    }
}
