package net.three_headed_monkey.communication;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

public class OutgoingCommunicationFactory {
    ThreeHeadedMonkeyApplication application;

    public static final String OUTGOING_COMMUNICATION_TYPE_BROADCAST = "OUTGOING_COMMUNICATION_TYPE_BROADCAST";
    public static final String OUTGOING_COMMUNICATION_TYPE_SMS= "OUTGOING_COMMUNICATION_TYPE_SMS";

    public OutgoingCommunicationFactory(ThreeHeadedMonkeyApplication application) {
        this.application = application;
    }

    public OutgoingCommunication createByType(String type) {
        if(type == null)
            return null;
        OutgoingCommunication communication = null;
        if(type.equals(OUTGOING_COMMUNICATION_TYPE_BROADCAST)){
            communication = new OutgoingBroadcastCommunication(application);
        }

        return communication;
    }

}
