package net.three_headed_monkey.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication_;
import net.three_headed_monkey.commands.Command;
import net.three_headed_monkey.communication.OutgoingCommunication;
import net.three_headed_monkey.communication.OutgoingCommunicationFactory;

import java.util.List;


public class CommandExecutorService extends IntentService {
    public static final String TAG = "CommandExecutorService";
    public static final String INTENT_COMMAND_STRING_PARAM = "SimCardCheckService_INTENT_COMMAND_STRING_PARAM";
    public static final String INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM = "SimCardCheckService_INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM";


    public CommandExecutorService() {
        super("CommandExecutorService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Command Executor Service triggered");
        if(intent == null)
            return;
        String commandStr = intent.getStringExtra(INTENT_COMMAND_STRING_PARAM);
        if(commandStr == null || commandStr.isEmpty())
            return;

        String communication_type = intent.getStringExtra(INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM);
        OutgoingCommunicationFactory outgoingCommunicationFactory = new OutgoingCommunicationFactory((ThreeHeadedMonkeyApplication)getApplication());
        OutgoingCommunication communication = outgoingCommunicationFactory.createByType(communication_type);


        Log.d(TAG, "Command Executor Service called with command string: " + commandStr + " and communication type " + communication_type);
        ThreeHeadedMonkeyApplication_ application = (ThreeHeadedMonkeyApplication_) getApplication();
        List<Command> commands = application.commandPrototypeManager.getCommandsForString(commandStr);
        for(Command command : commands) {
            if(communication != null)
                command.setOutgoingCommunication(communication);
            command.execute(commandStr);
        }
    }
}


