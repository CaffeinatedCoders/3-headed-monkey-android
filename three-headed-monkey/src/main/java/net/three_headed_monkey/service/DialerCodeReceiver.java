package net.three_headed_monkey.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import net.three_headed_monkey.ui.MainActivity_;


public class DialerCodeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);

            String dialerCode = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_text_dialer_number", "");
            System.err.println("================ " + dialerCode);
            if (phoneNumber.equals(dialerCode)) {
                Intent intent1 = new Intent(context, MainActivity_.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                setResultData(null);
            }

        }
    }
}
