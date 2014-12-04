package net.three_headed_monkey.commands;


import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Response;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.api.LocationHistoryApi;
import net.three_headed_monkey.data.ServiceInfo;

import java.io.IOException;
import java.util.List;

public class UpdateLocationHistoryCommand extends Command {
    public static final String TAG = "UpdateLocationHistoryCommand";

    public UpdateLocationHistoryCommand(ThreeHeadedMonkeyApplication application) {
        super(application);
    }

    @Override
    protected void doExecute(String command) {
        List<ServiceInfo> serviceInfos = application.serviceSettings.getAll();
        for(ServiceInfo serviceInfo : serviceInfos)
        {
            LocationHistoryApi locationHistoryApi = new LocationHistoryApi(serviceInfo, application);
            try {
                if(locationHistoryApi.getNotYetUploadedLocationCount() == 0) {
                    Log.v(TAG, "No new locations to upload to " + serviceInfo.getBaseUrl());
                    continue;
                }
                if(!locationHistoryApi.checkApiAvailable()) {
                    Log.e(TAG, "remote api " + serviceInfo.getBaseUrl() + " is currently unavailable");
                    continue;
                }
                Response response = locationHistoryApi.updateLocationHistory();
                if(!response.isSuccessful()) {
                    Log.e(TAG, "Error uploading locations, server returned " + response.code());
                }
            } catch (IOException e) {
                Log.e(TAG, "Error uploading locations", e);
            }
        }
    }

    @Override
    protected boolean respondsToCommand(String command) {
        return command.equalsIgnoreCase("UpdateLocationHistory") || command.equalsIgnoreCase("ulh");
    }

    @Override
    protected String getShortUsageText() {
        return "UpdateLocationHistory - Uploads a all collected locations to the remote webservices";
    }
}
