package net.three_headed_monkey.commands;

import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.api.DeviceInfoApi;
import net.three_headed_monkey.data.ServiceInfo;
import net.three_headed_monkey.utils.GcmUtils;

import java.io.IOException;

public class UpdateGcmRegistrationsCommand extends Command {
    public static final String TAG = "UpdateGcmRegistrationsCommand";
    public static final String COMMAND_STRING = "UpdateGcmRegistrations";

    public UpdateGcmRegistrationsCommand(ThreeHeadedMonkeyApplication application) {
        super(application);
    }

    @Override
    protected void doExecute(String command) {

        if(!GcmUtils.isPlayServicesAvailable(application)) {
            Log.e(TAG, "Error, Google play services not available");
            sendResponse("Command Failed: play services not available");
            return;
        }

        //first update all local service infos
        for(ServiceInfo info : application.serviceSettings.getAll()) {
            DeviceInfoApi api = new DeviceInfoApi(info, application);
            try {
                api.updateLocalServiceInfo();
            } catch (IOException e) {
                Log.e(TAG, "An Error occurred updating local service info " + info.baseUrl, e);
            }
        }
        application.serviceSettings.save();

        //Reregistering gcm
        GcmUtils gcmUtils = new GcmUtils(application);
        try {
            gcmUtils.reRegisterGcm();
        } catch (IOException e) {
            Log.e(TAG, "An Error occurred reregistering gcm ", e);
            return;
        }

        //Sending new regid to remote serives
        for(ServiceInfo info : application.serviceSettings.getAll()) {
            DeviceInfoApi api = new DeviceInfoApi(info, application);
            try {
                api.updateRemoteInformation();
            } catch (Exception e) {
                Log.e(TAG, "An Error occurred updating remote service info from " + info.baseUrl, e);
            }
        }
    }

    @Override
    protected boolean respondsToCommand(String command) {
        return command.equalsIgnoreCase(COMMAND_STRING) || command.equalsIgnoreCase("uGCM" +
                "r");
    }

    @Override
    protected String getShortUsageText() {
        return COMMAND_STRING + " - reregisters gcm and updates services";
    }
}
