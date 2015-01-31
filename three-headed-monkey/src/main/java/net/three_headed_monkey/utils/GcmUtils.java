package net.three_headed_monkey.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.data.ServiceInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GcmUtils {
    public static final String TAG = "GcmUtils";
    private static final String SHARED_PREF_KEY_REGID = "PREFKEY_GCM_REGID";

    private ThreeHeadedMonkeyApplication application;

    public GcmUtils(ThreeHeadedMonkeyApplication application) {
        this.application = application;
    }

    public synchronized List<String> getAllGcmSenderIds() {
        List<String> ids = new ArrayList<String>();
        for(ServiceInfo info : application.serviceSettings.getAll()) {
            if(info.gcm_sender_id != null && info.gcm_sender_id.length() > 0) {
                ids.add(info.gcm_sender_id);
            }
        }
        return ids;
    }

    // Should always be called async
    public synchronized void reRegisterGcm() throws IOException {
        String regid = getRegistrationId(application);
        if(regid != null && regid.length() > 0 ) {
            unregisterGcm();
        }
        registerGcm();
    }

    private void registerGcm() throws IOException {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(application);
        List<String> ids = getAllGcmSenderIds();
        String[] ids_a = new String[ids.size()];
        for(int i = 0; i < ids.size(); i++) {
            ids_a[i] = ids.get(i);
        }
        String regid = gcm.register(ids_a);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(application);
        sharedPref.edit().putString(SHARED_PREF_KEY_REGID, regid).commit();
    }

    private void unregisterGcm() throws IOException {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(application);
        gcm.unregister();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(application);
        sharedPref.edit().putString(SHARED_PREF_KEY_REGID, "").commit();
    }

    public static boolean isPlayServicesAvailable(Context context) {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return result == ConnectionResult.SUCCESS;
    }

    public static String getRegistrationId(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(SHARED_PREF_KEY_REGID, "");
    }

}
