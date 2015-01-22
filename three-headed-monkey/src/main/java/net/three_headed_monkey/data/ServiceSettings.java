package net.three_headed_monkey.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceSettings {
    public static final String TAG = "ServiceSettings";
    private static final String SHARED_PREFERENCE_KEY = "PREFKEY_ServiceSettings";

    private Context context;
    private List<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();

    public ServiceSettings(Context context) {
        this.context = context;
    }


    public synchronized List<ServiceInfo> getAll() {
        if (serviceInfos.isEmpty())
            load();
        return serviceInfos;
    }

    public synchronized void add(ServiceInfo serviceInfo) {
        serviceInfos.add(serviceInfo);
        Log.v(TAG, serviceInfo.baseUrl + " added");
        save();
    }

    public synchronized void remove(ServiceInfo serviceInfo) {
        serviceInfos.remove(serviceInfo);
        Log.v(TAG, serviceInfo.baseUrl + " removed");
        save();
    }

    public synchronized ServiceInfo get(int i) {
        return serviceInfos.get(i);
    }

    public int size() {
        return serviceInfos.size();
    }

    public synchronized void save() {
        Gson gson = new Gson();
        String json = gson.toJson(serviceInfos);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putString(SHARED_PREFERENCE_KEY, json).commit();
    }

    public synchronized void load() {
        Gson gson = new Gson();
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(SHARED_PREFERENCE_KEY, "");
        Log.d(TAG, json);
        Type collectionType = new TypeToken<ArrayList<ServiceInfo>>() {
        }.getType();
        List<ServiceInfo> infos = gson.fromJson(json, collectionType);
        if (infos == null)
            return;
        infos.removeAll(Collections.singleton(null));
        serviceInfos.clear();
        if (infos != null)
            serviceInfos.addAll(infos);
    }

}
