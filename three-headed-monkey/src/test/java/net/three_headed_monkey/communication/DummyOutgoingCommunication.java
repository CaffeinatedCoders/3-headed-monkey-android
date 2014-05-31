package net.three_headed_monkey.communication;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

public class DummyOutgoingCommunication extends OutgoingCommunication {
    private String lastMessage = null;

    public DummyOutgoingCommunication() {
        super(null);
    }

    @Override
    public void sendMessage(String text) {
        lastMessage = text;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
