package net.three_headed_monkey.communication;

import android.telephony.SmsManager;
import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

import java.io.UnsupportedEncodingException;

public class OutgoingDataSmsCommunication extends OutgoingCommunication {
    public static final String TAG = "OutgoingDataSmsCommunication";
    private static final short TARGET_PORT = 7342;

    private String address;

    public OutgoingDataSmsCommunication(ThreeHeadedMonkeyApplication application, String address) {
        super(application);
        this.address = address;
    }

    @Override
    public void sendMessage(String text) {
        if (text == null || address == null)
            return;
        SmsManager smsManager = SmsManager.getDefault();
        try {
            Log.d(TAG, "Sending response to" + address);
            smsManager.sendDataMessage(address, null, TARGET_PORT, text.getBytes("UTF-8"), null, null);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Failed to send datasms", e);
        }
    }
}
