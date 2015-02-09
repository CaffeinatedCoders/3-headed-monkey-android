package net.three_headed_monkey.communication;


import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.api.MessageApi;
import net.three_headed_monkey.api.PendingCommandsApi;
import net.three_headed_monkey.data.PendingCommandFromApi;

public class OutgoingServiceApiCommandCommunication extends OutgoingCommandCommunication {
    public static final String TAG = "OutgoingServiceApiCommandCommunication";
    private PendingCommandFromApi pendingCommandFromApi;
    private MessageApi messageApi;

    public OutgoingServiceApiCommandCommunication(ThreeHeadedMonkeyApplication application, PendingCommandFromApi pendingCommandFromApi) {
        super(application);
        this.pendingCommandFromApi = pendingCommandFromApi;
        this.messageApi = new MessageApi(pendingCommandFromApi.serviceInfo);
    }

    @Override
    public void sendMessage(String text) {
        try {
            messageApi.postMessage(text);
        } catch (Exception e) {
            Log.e(TAG, "Error while posting message to service", e);
        }
    }

    @Override
    public void notifyCommandFinished() {
        application.pendingCommandsFromApiManager.setCommandFinished(pendingCommandFromApi);
        PendingCommandsApi api = new PendingCommandsApi(pendingCommandFromApi.serviceInfo);
        try {
            api.setCommandFinished(pendingCommandFromApi.id);
            application.pendingCommandsFromApiManager.remove(pendingCommandFromApi);
        } catch (Exception e) {
            Log.e(TAG, "Error while notifying command finished", e);
        }
    }
}
