package net.three_headed_monkey.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.telephony.PhoneNumberUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
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
        if(phoneNumberInfoList == null)
            return;
        phoneNumberInfoList.removeAll(Collections.singleton(null));
        phoneNumberList.clear();
        if(phoneNumberInfoList != null) phoneNumberList.addAll(phoneNumberInfoList);
    }

    public synchronized void  addPhoneNumber(PhoneNumberInfo phoneNumberInfo){
        Log.v("Phonenumber", "added " + phoneNumberInfo.phoneNumber);
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

    public synchronized void removeAll() {
        phoneNumberList.removeAll(phoneNumberList);
        saveSettings();
    }

    public synchronized boolean nameExists(String name) {
        for(PhoneNumberInfo phoneNumberInfo : phoneNumberList) {
            if(phoneNumberInfo.name.equals(name)) return true;
        }
        return false;
    }

    public synchronized List<PhoneNumberInfo> findEntriesForNumber(String phoneNumber) {
        List<PhoneNumberInfo> erg = new ArrayList<PhoneNumberInfo>();
        for(PhoneNumberInfo phoneNumberInfo : phoneNumberList) {
            if(PhoneNumberUtils.compare(phoneNumber, phoneNumberInfo.phoneNumber)) {
                erg.add(phoneNumberInfo);
            }
        }
        return erg;
    }


}
