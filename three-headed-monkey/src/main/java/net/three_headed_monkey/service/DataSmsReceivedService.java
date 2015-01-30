package net.three_headed_monkey.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.communication.OutgoingCommandCommunicationFactory;
import net.three_headed_monkey.data.PhoneNumberInfo;

import java.util.List;


public class DataSmsReceivedService extends IntentService {
    public static final String TAG = "DataSmsReceivedService";

    public DataSmsReceivedService() {
        super("DataSmsReceivedService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Started DataSmsReceivedService");
        ThreeHeadedMonkeyApplication application = (ThreeHeadedMonkeyApplication) getApplication();
        Bundle bundle = intent.getExtras();
        if (bundle == null)
            return;
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null || pdus.length == 0)
            return;

        for (int i = 0; i < pdus.length; i++) {
            try {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                Log.d(TAG, "Received PDU: " + new String(Hex.encodeHex((byte[]) pdus[i])));
                byte[] data = smsMessage.getUserData();
                String message = new String(data, "UTF-8");
                String sender = smsMessage.getOriginatingAddress();
                Log.d(TAG, "Received data-sms from " + sender + ": " + message);

                List<PhoneNumberInfo> infos = application.phoneNumberSettings.findEntriesForNumber(sender);
                if (infos.isEmpty()) {
                    Log.d(TAG, "Datasms sender not authorized to send commands");
                    continue;
                }


                Intent command_intent = new Intent(this, CommandExecutorService.class);
                command_intent.putExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM, message);
                command_intent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM, OutgoingCommandCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_DATASMS);
                command_intent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_SENDER_ADDRESS_PARAM, sender);

                startService(command_intent);
            } catch (Exception ex) {
                Log.e(TAG, "Exception while parsing data sms", ex);
            }
        }
    }


}
