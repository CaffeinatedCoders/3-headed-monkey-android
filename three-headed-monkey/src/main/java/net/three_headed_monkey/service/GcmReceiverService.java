package net.three_headed_monkey.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import net.three_headed_monkey.commands.PullCommandsCommand;
import net.three_headed_monkey.communication.OutgoingCommandCommunicationFactory;

public class GcmReceiverService extends IntentService {
    public static final String TAG = "GcmReceiverService";

    public GcmReceiverService() {
        super("GcmReceiverService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "GcmReceiverService started");
        Bundle extras = intent.getExtras();
        if(extras.isEmpty())
            return;

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            Log.e(TAG, "GCM error: " + extras.toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            Log.e(TAG, "GCM Delete message received - not implemented: " + extras.toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            Log.i(TAG, "GCM message received, starting pull commands");
            Intent cintent = new Intent(getApplication(), CommandExecutorService.class);
            cintent.putExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM, PullCommandsCommand.COMMAND_STRING);
            cintent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM, OutgoingCommandCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_BROADCAST);
            getApplication().startService(cintent);
        }


    }

}
