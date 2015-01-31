package net.three_headed_monkey.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GcmReceiver extends BroadcastReceiver {
    public static final String TAG = "GcmReceiver";

    public GcmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "GCM Message received!");
        if(intent == null)
            return;
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmReceiverService.class.getName());
        context.startService(intent.setComponent(comp));
    }
}
