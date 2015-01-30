package net.three_headed_monkey.communication;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

public abstract class OutgoingCommandCommunication {
    protected ThreeHeadedMonkeyApplication application;

    public OutgoingCommandCommunication(ThreeHeadedMonkeyApplication application) {
        this.application = application;
    }

    public abstract void sendMessage(String text);
    public abstract void notifyCommandFinished();
}
