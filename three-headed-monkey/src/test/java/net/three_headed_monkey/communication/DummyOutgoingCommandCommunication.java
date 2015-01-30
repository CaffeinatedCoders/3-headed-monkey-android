package net.three_headed_monkey.communication;

public class DummyOutgoingCommandCommunication extends OutgoingCommandCommunication {
    private String lastMessage = null;

    public DummyOutgoingCommandCommunication() {
        super(null);
    }

    @Override
    public void sendMessage(String text) {
        lastMessage = text;
    }

    @Override
    public void notifyCommandFinished() {

    }

    public String getLastMessage() {
        return lastMessage;
    }
}
