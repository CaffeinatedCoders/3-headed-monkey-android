package net.three_headed_monkey.commands;

import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.communication.OutgoingCommandCommunication;


public abstract class Command implements Cloneable, Runnable {
    protected ThreeHeadedMonkeyApplication application;

    private boolean isPrototype;

    protected boolean isConfirmed = false;

    protected OutgoingCommandCommunication outgoingCommandCommunication = null;

    protected String commandString = null;

    public Command(ThreeHeadedMonkeyApplication application) {
        this.application = application;
        this.isPrototype = true;
    }

    @Override
    public void run() {
        Log.d("Command", "Running command: " + this.getClass().getName());
        if (isPrototype)
            throw new RuntimeException("Can't execute prototype command!");
        if (getCommandString() == null)
            return;
        if (needsConfirmation(getCommandString()) && !isConfirmed()) {
            //@TODO Confirmation logic
            return;
        }
        if (respondsToCommand(getCommandString())) {
            doExecute(getCommandString());
            if(outgoingCommandCommunication != null)
                outgoingCommandCommunication.notifyCommandFinished();
        }
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

    public OutgoingCommandCommunication getOutgoingCommandCommunication() {
        return outgoingCommandCommunication;
    }

    public void setOutgoingCommandCommunication(OutgoingCommandCommunication outgoingCommandCommunication) {
        this.outgoingCommandCommunication = outgoingCommandCommunication;
    }

    public String getCommandString() {
        return commandString;
    }

    public void setCommandString(String commandString) {
        this.commandString = commandString;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    // Should only used for testing
    public void setPrototype(boolean prototype) {
        isPrototype = prototype;
    }


    public void sendResponse(String text) {
        if (getOutgoingCommandCommunication() == null)
            return;
        getOutgoingCommandCommunication().sendMessage(text);
    }

    protected abstract void doExecute(String command);

    /**
     * This method will be mostly called on the prototype to select the right Command object
     *
     * @param command the command string including parameters
     * @return
     */
    protected abstract boolean respondsToCommand(String command);

    public boolean needsConfirmation(String command) {
        return false;
    }

    protected abstract String getShortUsageText();

}
