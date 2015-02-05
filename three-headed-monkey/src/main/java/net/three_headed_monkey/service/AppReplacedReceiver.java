package net.three_headed_monkey.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.three_headed_monkey.commands.UpdateGcmRegistrationsCommand;
import net.three_headed_monkey.communication.OutgoingCommandCommunicationFactory;

public class AppReplacedReceiver extends BroadcastReceiver {
    public static final String TAG = "AppReplacedReceiver";
    public AppReplacedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent i) {
        Log.i(TAG, "Application update detected");
        PeriodicWorkReceiver.registerPeriodicWorkReceiver(context);
        Intent intent = new Intent(context, CommandExecutorService.class);
        intent.putExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM, UpdateGcmRegistrationsCommand.COMMAND_STRING);
        intent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM, OutgoingCommandCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_BROADCAST);
        context.startService(intent);
    }
}
