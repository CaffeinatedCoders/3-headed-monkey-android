package net.three_headed_monkey.commands;


import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

public class HelpCommand extends Command {
    public HelpCommand(ThreeHeadedMonkeyApplication application) {
        super(application);
    }

    @Override
    protected void doExecute(String commandStr) {
        if (!respondsToCommand(commandStr))
            return;
        String text = "Available commands:\n";
        for (Command command : application.commandPrototypeManager.getPrototypeCommandList()) {
            text += "  " + command.getShortUsageText() + "\n";
        }

        sendResponse(text);
    }

    @Override
    protected boolean respondsToCommand(String command) {
        return command.equals("help");
    }

    @Override
    protected String getShortUsageText() {
        return "help - Lists all available commands";
    }
}
