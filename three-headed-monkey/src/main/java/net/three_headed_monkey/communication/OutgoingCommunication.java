package net.three_headed_monkey.communication;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

public abstract class OutgoingCommunication {
    protected ThreeHeadedMonkeyApplication application;

    public OutgoingCommunication(ThreeHeadedMonkeyApplication application) {
        this.application = application;
    }

    public abstract void sendMessage(String text);
}
