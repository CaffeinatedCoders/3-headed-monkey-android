package net.three_headed_monkey.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.commands.Command;
import net.three_headed_monkey.communication.OutgoingCommandCommunication;
import net.three_headed_monkey.communication.OutgoingCommandCommunicationFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class CommandExecutorService extends Service {
    public static final String TAG = "CommandExecutorService";
    public static final String INTENT_COMMAND_STRING_PARAM = "SimCardCheckService_INTENT_COMMAND_STRING_PARAM";
    public static final String INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM = "SimCardCheckService_INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM";
    public static final String INTENT_OUTGOING_COMMUNICATION_SENDER_ADDRESS_PARAM = "SimCardCheckService_INTENT_OUTGOING_COMMUNICATION_SENDER_ADDRESS_PARAM";
    ThreadPoolExecutor executor;

    private static final int THREAD_POOL_MIN_SIZE = 1;
    private static final int THREAD_POOL_MAX_SIZE = 7;
    private static final int THREAD_QUEUE_SIZE = 10;


    public CommandExecutorService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(THREAD_QUEUE_SIZE);
        executor = new ThreadPoolExecutor(THREAD_POOL_MIN_SIZE, THREAD_POOL_MAX_SIZE, 1, TimeUnit.MINUTES, queue);
//        executor = new ThreadPoolExecutor(THREAD_POOL_MIN_SIZE, THREAD_POOL_MAX_SIZE, 0, TimeUnit.MILLISECONDS, queue);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            handleIntent(intent);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, "Command Executor Service triggered");
        if (intent == null)
            return;
        String commandStr = intent.getStringExtra(INTENT_COMMAND_STRING_PARAM);
        if (commandStr == null || commandStr.isEmpty())
            return;

        String communication_type = intent.getStringExtra(INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM);
        String sender = intent.getStringExtra(INTENT_OUTGOING_COMMUNICATION_SENDER_ADDRESS_PARAM);

        OutgoingCommandCommunicationFactory outgoingCommandCommunicationFactory = new OutgoingCommandCommunicationFactory((ThreeHeadedMonkeyApplication) getApplication());
        OutgoingCommandCommunication communication = outgoingCommandCommunicationFactory.createByType(communication_type, sender);


        Log.d(TAG, "Command Executor Service called with command string: " + commandStr + " and communication type " + communication_type);
        ThreeHeadedMonkeyApplication application = (ThreeHeadedMonkeyApplication) getApplication();
        List<Command> commands = application.commandPrototypeManager.getCommandsForString(commandStr);
        Log.d(TAG, "Executing " + commands.size() + " commands");
        for (Command command : commands) {
            command.setCommandString(commandStr);
            if (communication != null)
                command.setOutgoingCommandCommunication(communication);
            executor.execute(command);
        }
    }
}


