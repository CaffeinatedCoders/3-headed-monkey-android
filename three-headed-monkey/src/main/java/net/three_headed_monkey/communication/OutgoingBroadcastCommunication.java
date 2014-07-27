package net.three_headed_monkey.communication;


import android.content.Intent;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

/**
 * Used for sending a message via Android Broadcast so it can be displayed in activities
 */
public class OutgoingBroadcastCommunication extends OutgoingCommunication {
    public final static String INTENT_ACTION = "OutgoingBroadcastCommunication_Intent";
    public final static String INTENT_MESSAGE_PARAM = "OutgoingBroadcastCommunication_MessageParam";

    public OutgoingBroadcastCommunication(ThreeHeadedMonkeyApplication application) {
        super(application);
    }

    @Override
    public void sendMessage(String text) {
        Intent intent = new Intent();
        intent.setAction(INTENT_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(INTENT_MESSAGE_PARAM, text);
        application.sendBroadcast(intent);
    }
}
