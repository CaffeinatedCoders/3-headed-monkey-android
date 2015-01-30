package net.three_headed_monkey.commands;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.api.PendingCommandsApi;
import net.three_headed_monkey.communication.OutgoingCommandCommunicationFactory;
import net.three_headed_monkey.data.PendingCommandFromApi;
import net.three_headed_monkey.data.ServiceInfo;
import net.three_headed_monkey.service.CommandExecutorService;

import java.io.IOException;
import java.util.List;

public class PullCommandsCommand extends Command {
    public static final String TAG = "PullCommandsCommand";

    public PullCommandsCommand(ThreeHeadedMonkeyApplication application) {
        super(application);
    }

    @Override
    protected void doExecute(String commandString) {
        Gson gson = new Gson();
        List<ServiceInfo> serviceInfos = application.serviceSettings.getAll();
        for(ServiceInfo serviceInfo : serviceInfos) {
            PendingCommandsApi api = new PendingCommandsApi(serviceInfo);
            try {
                if(!api.checkApiAvailable()) {
                    Log.e(TAG, "remote api " + serviceInfo.getBaseUrl() + " is currently unavailable");
                    continue;
                }
                List<PendingCommandFromApi> commands = api.getPendingCommands();
                if(commands == null) {
                    Log.e(TAG, "Fetching commands from " + serviceInfo.getBaseUrl() + " failed");
                    continue;
                }
                List<PendingCommandFromApi> alreadyPendingCommands = application.pendingCommandsFromApiManager.getAll();
                commands.removeAll(alreadyPendingCommands);
                for(PendingCommandFromApi command : commands) {
                    Intent command_intent = new Intent(application, CommandExecutorService.class);
                    command_intent.putExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM, command.command);
                    command_intent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM, OutgoingCommandCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_SERVICEAPI);
                    command_intent.putExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_SENDER_ADDRESS_PARAM, gson.toJson(command));
                    application.startService(command_intent);
                }

            } catch (IOException ex) {
                Log.e(TAG, "Error while executing " + commandString, ex);
            }
        }
    }

    @Override
    protected boolean respondsToCommand(String command) {
        return command.equalsIgnoreCase("PullCommands");
    }

    @Override
    protected String getShortUsageText() {
        return "PullCommands - Loads command from services and runs them";
    }
}
