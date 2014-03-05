package net.three_headed_monkey.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentservice = new Intent(context, SimCardCheckService.class);
        context.startService(intentservice);
    }
}
