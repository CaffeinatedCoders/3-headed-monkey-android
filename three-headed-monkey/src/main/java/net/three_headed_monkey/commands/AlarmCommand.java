package net.three_headed_monkey.commands;

import android.content.Intent;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.ui.AlarmCommandActivity_;

public class AlarmCommand extends Command {
    public AlarmCommand(ThreeHeadedMonkeyApplication application) {
        super(application);
    }

    @Override
    protected void doExecute(String command) {
        Intent intent = AlarmCommandActivity_.intent(application).get();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
    }

    @Override
    protected boolean respondsToCommand(String command) {
        return command.equalsIgnoreCase("alarm");
    }

    @Override
    protected String getShortUsageText() {
        return "alarm - plays an alarm sound with full volume until closed";
    }
}
