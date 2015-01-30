package net.three_headed_monkey.service;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import net.three_headed_monkey.commands.PullCommandsCommand;
import net.three_headed_monkey.commands.UpdateLocationHistoryCommand;
import net.three_headed_monkey.communication.OutgoingCommandCommunicationFactory;

public class PeriodicWorkReceiver extends BroadcastReceiver {
    public static final String TAG="PeriodicWorkReceiver";

    private static final int PENDING_INTENT_ID=9762458;


    @Override
    public void onReceive(Context context, Intent in) {
        Log.v(TAG, "PeriodicWorkReceiver called");
        Intent intent;

        // Pull commands from Api
        intent = new Intent(context, CommandExecutorService.class);
        intent.putExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM, PullCommandsCommand.COMMAND_STRING);
        intent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM, OutgoingCommandCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_BROADCAST);
        context.startService(intent);

        // Execute UpdateLocationHistoryCommand
        intent = new Intent(context, CommandExecutorService.class);
        intent.putExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM, UpdateLocationHistoryCommand.COMMAND_STRING);
        intent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM, OutgoingCommandCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_BROADCAST);
        context.startService(intent);

    }

    public static void registerPeriodicWorkReceiver(Context context) {
        Log.v(TAG, "Resistering PeriodicWorkReceiver");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createPendingIntent(context));
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5000, AlarmManager.INTERVAL_HALF_HOUR, createPendingIntent(context));
    }

    private static PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, PeriodicWorkReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, PENDING_INTENT_ID, intent, 0);
        return pendingIntent;
    }
}
