package net.three_headed_monkey.commands;


import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

import java.util.LinkedList;
import java.util.List;

public class CommandPrototypeManager {
    ThreeHeadedMonkeyApplication application;
    List<Command> commands = new LinkedList<Command>();

    public CommandPrototypeManager(ThreeHeadedMonkeyApplication application) {
        this.application = application;
        initPrototypes();
    }

    public List<Command> getPrototypeCommandList() {
        return commands;
    }

    /**
     * Get commands from a single command string
     * Normally only one Command will be returned except if more than one command responds to the string
     *
     * @param commandString
     * @return
     */
    public List<Command> getCommandsForString(String commandString) {
        List<Command> ret = new LinkedList<Command>();

        for (Command command : commands) {
            if (command.respondsToCommand(commandString))
                ret.add((Command)command.clone());
        }

        return ret;
    }

    public void initPrototypes() {
        commands.clear();
        commands.add(new LogCommand(application));
        commands.add(new HelpCommand(application));
        commands.add(new GetPhoneStateCommand(application));
        commands.add(new GetLastLocation(application));
    }

}
