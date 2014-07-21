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

public class SimCardSettings {
    public static final String TAG = "SimCardSettings";
    private static final String SHARED_PREFERENCE_KEY = "PREFKEY_SimCardSettings";

    private Context context;
    private List<SimCardInfo> authorized_sim_cards = new ArrayList<SimCardInfo>();

    public SimCardSettings(Context context) {
        this.context = context;
    }


    public List<SimCardInfo> getAll() {
        if (authorized_sim_cards.isEmpty())
            load();
        return authorized_sim_cards;
    }

    public void addCurrentSimCard() {
        if (currentSimCardAuthorized())
            return;
        SimCardInfo current = SimCardInfo.createFromSimCard(context);
        if (current == null)
            return;
        authorized_sim_cards.add(current);
        save();
    }

    public void removeCurrentSimCard() {
        SimCardInfo current = SimCardInfo.createFromSimCard(context);

        authorized_sim_cards.remove(current);
        save();
    }

    public void save() {
        Gson gson = new Gson();
        String json = gson.toJson(authorized_sim_cards);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putString(SHARED_PREFERENCE_KEY, json).commit();
    }

    public void load() {
        Gson gson = new Gson();
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(SHARED_PREFERENCE_KEY, "");
        Log.d("SimCardSettings: ", json);
        Type collectionType = new TypeToken<ArrayList<SimCardInfo>>() {
        }.getType();
        List<SimCardInfo> cardInfos = gson.fromJson(json, collectionType);
        if (cardInfos == null)
            return;
        cardInfos.removeAll(Collections.singleton(null));
        authorized_sim_cards.clear();
        if (cardInfos != null)
            authorized_sim_cards.addAll(cardInfos);
    }

    public boolean currentSimCardAuthorized() {
        SimCardInfo current = SimCardInfo.createFromSimCard(context);
        if (current == null)
            return false;
        return authorized_sim_cards.contains(current);
    }


}
