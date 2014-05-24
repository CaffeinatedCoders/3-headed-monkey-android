package net.three_headed_monkey.commands;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.communication.OutgoingCommunication;


public abstract class Command implements Cloneable {
    protected ThreeHeadedMonkeyApplication application;

    private boolean isPrototype;

    protected OutgoingCommunication outgoingCommunication = null;

    public Command(ThreeHeadedMonkeyApplication application) {
        this.application = application;
        this.isPrototype = true;
    }


    /**
     * Executes the command
     *
     * @param confirm if false but the command needs confirmation, a confirmation request will be send to the user instead
     */
    public final void execute(String command, boolean confirm) {
        if(isPrototype)
            throw new RuntimeException("Can't execute prototype command!");
        if (needsConfirmation(command) && !confirm) {
            //@TODO Confirmation logic
            return;
        }
        if(respondsToCommand(command))
            doExecute(command);
    }

    public final void execute(String command) {
        execute(command, false);
    }

    @Override
    protected Object clone() {
        Command clone = null;
        try {
            clone = (Command) super.clone();
            clone.isPrototype = false;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    public OutgoingCommunication getOutgoingCommunication() {
        return outgoingCommunication;
    }

    public void setOutgoingCommunication(OutgoingCommunication outgoingCommunication) {
        this.outgoingCommunication = outgoingCommunication;
    }

    public void sendResponse(String text) {
        if(outgoingCommunication == null)
            return;
        outgoingCommunication.sendMessage(text);
    }

    protected abstract void doExecute(String command);

    /**
     * This method will be mostly called on the prototype to select the right Command object
     * @param command the command string including parameters
     * @return
     */
    protected abstract boolean respondsToCommand(String command);

    public boolean needsConfirmation(String command) {
        return false;
    }

    protected abstract String getShortUsageText();

}
