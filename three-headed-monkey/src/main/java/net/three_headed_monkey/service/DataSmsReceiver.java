package net.three_headed_monkey.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DataSmsReceiver extends BroadcastReceiver {
    public static final String TAG = "DataSmsReceiver";

    public DataSmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received DataSms with data: " + intent.getDataString());
        Bundle bundle = intent.getExtras();
        if (bundle == null)
            return;
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null || pdus.length == 0)
            return;

        Log.d(TAG, "Starting DataSmsReceivedService");
        Intent intentservice = new Intent(context, DataSmsReceivedService.class);
        intentservice.putExtra("pdus", pdus);
        context.startService(intentservice);
    }
}
