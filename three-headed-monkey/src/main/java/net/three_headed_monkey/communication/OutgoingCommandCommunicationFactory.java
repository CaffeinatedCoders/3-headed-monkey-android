package net.three_headed_monkey.communication;

import com.google.gson.Gson;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.data.PendingCommandFromApi;

public class OutgoingCommandCommunicationFactory {
    ThreeHeadedMonkeyApplication application;

    public static final String OUTGOING_COMMUNICATION_TYPE_BROADCAST = "OUTGOING_COMMUNICATION_TYPE_BROADCAST";
    public static final String OUTGOING_COMMUNICATION_TYPE_DATASMS = "OUTGOING_COMMUNICATION_TYPE_DATASMS";
    public static final String OUTGOING_COMMUNICATION_TYPE_SERVICEAPI = "OUTGOING_COMMUNICATION_TYPE_SERVICEAPI";

    public OutgoingCommandCommunicationFactory(ThreeHeadedMonkeyApplication application) {
        this.application = application;
    }

    public OutgoingCommandCommunication createByType(String type, String sender) {
        if (type == null)
            return null;
        OutgoingCommandCommunication communication = null;
        if (type.equals(OUTGOING_COMMUNICATION_TYPE_BROADCAST)) {
            communication = new OutgoingBroadcastCommandCommunication(application);
        } else if (type.equals(OUTGOING_COMMUNICATION_TYPE_DATASMS)) {
            if (sender != null && !sender.isEmpty())
                communication = new OutgoingDataSmsCommandCommunication(application, sender);
        } else if (type.equals(OUTGOING_COMMUNICATION_TYPE_SERVICEAPI)) {
            if (sender != null && !sender.isEmpty()) {
                Gson gson = new Gson();
                PendingCommandFromApi command = gson.fromJson(sender, PendingCommandFromApi.class);
                communication = new OutgoingServiceApiCommandCommunication(application, command);
            }
        }

        return communication;
    }

}
