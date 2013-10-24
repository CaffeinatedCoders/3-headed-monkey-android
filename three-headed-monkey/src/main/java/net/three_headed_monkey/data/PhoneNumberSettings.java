package net.three_headed_monkey.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PhoneNumberSettings {
    private static final String SHARED_PREFERENCE_KEY = "PREFKEY_PhoneNumberSettings";
    private List<PhoneNumberInfo> phoneNumberList = new ArrayList<PhoneNumberInfo>();
    private Context context;

    public PhoneNumberSettings(Context context) {
        this.context = context;
    }

    public void saveSettings(){
        Gson gson = new Gson();
        String json = gson.toJson(phoneNumberList);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(SHARED_PREFERENCE_KEY, json).commit();
    }

    public void loadSettings(){
        Gson gson = new Gson();
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(SHARED_PREFERENCE_KEY, "");
        Type collectionType = new TypeToken<ArrayList<PhoneNumberInfo>>(){}.getType();
        List<PhoneNumberInfo> phoneNumberInfoList = gson.fromJson(json, collectionType);
        phoneNumberList.clear();
        if(phoneNumberInfoList != null) phoneNumberList.addAll(phoneNumberInfoList);
    }

    public synchronized void  addPhoneNumber(PhoneNumberInfo phoneNumberInfo){
        phoneNumberList.add(phoneNumberInfo);
        saveSettings();
    }

    public synchronized void removePhoneNumber(PhoneNumberInfo phoneNumberInfo){
        phoneNumberList.remove(phoneNumberInfo);
        saveSettings();
    }

    public synchronized List<PhoneNumberInfo> getAll(){
        if(phoneNumberList.isEmpty()) loadSettings();
        return phoneNumberList;
    }


}
