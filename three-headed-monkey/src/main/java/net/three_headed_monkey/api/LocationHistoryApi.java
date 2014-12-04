package net.three_headed_monkey.api;


import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.squareup.okhttp.Response;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.data.LocationHistoryDatabase;
import net.three_headed_monkey.data.ServiceInfo;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LocationHistoryApi extends BaseApiV1 {
    private static final String LOCATION_POST_URL = "/locations";

    private ThreeHeadedMonkeyApplication application;
    private LocationHistoryDatabase database;

    public LocationHistoryApi(ServiceInfo serviceInfo, ThreeHeadedMonkeyApplication application) {
        super(serviceInfo);
        this.application = application;
        this.database = new LocationHistoryDatabase(application);
    }

    public int getNotYetUploadedLocationCount() {
        List<Location> locations = database.getLocationsNewerThan(serviceInfo.lastLocationHistoryUpdate);
        return locations.size();
    }

    public Response updateLocationHistory() throws IOException {
        List<Location> locations = database.getLocationsNewerThan(serviceInfo.lastLocationHistoryUpdate);


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Location.class, new LocationJsonSerializer());
        Gson gson = gsonBuilder.create();
        JsonElement jLocations = gson.toJsonTree(locations);
        JsonObject jRoot = new JsonObject();
        jRoot.add("location", jLocations);
        String json = jRoot.toString();
        Log.d(TAG, json);

        Response response = doPostRequest(LOCATION_POST_URL, json);
        if(response.isSuccessful()) {
            serviceInfo.lastLocationHistoryUpdate = locations.get(locations.size()-1).getTime();
            application.serviceSettings.save();
        }
        return response;
    }

    public class LocationJsonSerializer implements JsonSerializer<Location> {
        @Override
        public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            JsonObject json = new JsonObject();
            json.addProperty("latitude", src.getLatitude());
            json.addProperty("longitude", src.getLongitude());
            json.addProperty("altitude", src.getAltitude());
            json.addProperty("accuracy", src.getAccuracy());
            json.addProperty("provider", src.getProvider());
            json.addProperty("time", dateFormat.format(new Date(src.getTime())));
            return json;
        }
    }
}
