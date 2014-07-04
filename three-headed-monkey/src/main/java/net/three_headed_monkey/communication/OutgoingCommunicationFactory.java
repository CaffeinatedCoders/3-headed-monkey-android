package net.three_headed_monkey.communication;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

public class OutgoingCommunicationFactory {
    ThreeHeadedMonkeyApplication application;

    public static final String OUTGOING_COMMUNICATION_TYPE_BROADCAST = "OUTGOING_COMMUNICATION_TYPE_BROADCAST";
    public static final String OUTGOING_COMMUNICATION_TYPE_DATASMS= "OUTGOING_COMMUNICATION_TYPE_DATASMS";

    public OutgoingCommunicationFactory(ThreeHeadedMonkeyApplication application) {
        this.application = application;
    }

    public OutgoingCommunication createByType(String type, String sender) {
        if(type == null)
            return null;
        OutgoingCommunication communication = null;
        if(type.equals(OUTGOING_COMMUNICATION_TYPE_BROADCAST)){
            communication = new OutgoingBroadcastCommunication(application);
        } else if(type.equals(OUTGOING_COMMUNICATION_TYPE_DATASMS)) {
            if(sender != null && !sender.isEmpty())
                communication = new OutgoingDataSmsCommunication(application, sender);
        }

        return communication;
    }

}
