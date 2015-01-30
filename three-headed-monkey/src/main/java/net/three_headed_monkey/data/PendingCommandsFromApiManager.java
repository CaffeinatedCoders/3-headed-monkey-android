package net.three_headed_monkey.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PendingCommandsFromApiManager {
    public static final String TAG = "PendingCommandsFromApiManager";
    private static final String SHARED_PREFERENCE_KEY = "PREFKEY_PendingCommandsFromApiManager";
    private Context context;
    private List<PendingCommandFromApi> pendingCommands = new ArrayList<PendingCommandFromApi>();

    public PendingCommandsFromApiManager(Context context) {
        this.context = context;
    }

    public synchronized void add(PendingCommandFromApi command) {
        pendingCommands.add(command);
        save();
    }

    public synchronized void remove(PendingCommandFromApi command) {
        pendingCommands.remove(command);
        save();
    }

    public synchronized List<PendingCommandFromApi> getAll() {
        List<PendingCommandFromApi> commands = new ArrayList<PendingCommandFromApi>();
        commands.addAll(pendingCommands);
        return commands;
    }

    public synchronized void setCommandFinished(PendingCommandFromApi command) {
        int index = pendingCommands.indexOf(command);
        if(index < 0) {
            Log.e(TAG, "setCommandFinished could not find command");
            return;
        }
        PendingCommandFromApi pc = pendingCommands.get(index);
        pc.finished = true;
        save();
    }

    public void save() {
        Gson gson = new Gson();
        String json = gson.toJson(pendingCommands);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(SHARED_PREFERENCE_KEY, json).commit();
    }

    public void load() {
        Gson gson = new Gson();
        String json = PreferenceManager.getDefaultSharedPreferences(context).getString(SHARED_PREFERENCE_KEY, "");
        Type collectionType = new TypeToken<List<PendingCommandFromApi>>() {}.getType();
        List<PendingCommandFromApi> commands = gson.fromJson(json, collectionType);
        if (commands == null)
            return;
        pendingCommands.removeAll(Collections.singleton(null));
        pendingCommands.clear();
        if (pendingCommands != null) pendingCommands.addAll(commands);
    }



}
