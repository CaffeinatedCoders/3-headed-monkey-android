package net.three_headed_monkey.commands;

import android.util.Log;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

/**
 * Simple command for testing purposes which writes some text to logcat
 */
public class LogCommand extends Command {
    public final static String TAG = "LogCommand";
    private final static String pattern = "^log\\s(.+)";

    public LogCommand(ThreeHeadedMonkeyApplication application) {
        super(application);
    }

    @Override
    protected void doExecute(String command) {
        String message = command.replaceFirst("^log", "");
        Log.i(TAG, message);
    }

    @Override
    protected boolean respondsToCommand(String command) {
        return command.matches(pattern);
    }

    @Override
    protected String getShortUsageText() {
        return "log <text> - Prints text to logcat";
    }
}
