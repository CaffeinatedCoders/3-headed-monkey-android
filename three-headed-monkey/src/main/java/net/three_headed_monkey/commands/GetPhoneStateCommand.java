package net.three_headed_monkey.commands;

import android.content.Context;
import android.telephony.TelephonyManager;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;


public class GetPhoneStateCommand extends Command {
    public GetPhoneStateCommand(ThreeHeadedMonkeyApplication application) {
        super(application);
    }

    @Override
    protected void doExecute(String command) {
        TelephonyManager tm = (TelephonyManager)application.getSystemService(Context.TELEPHONY_SERVICE);
        String text = "";
        text += "deviceId: " + tm.getDeviceId();
        text += "\nsimCountryCode: " + tm.getSimCountryIso();
        text += "\nsimOperator: " + tm.getSimOperatorName();
        text += "\nsimSerial: " + tm.getSimSerialNumber();
        text += "\nsubscriberId: " + tm.getSubscriberId();
        sendResponse(text);
    }

    @Override
    protected boolean respondsToCommand(String command) {
        return command.equals("getPhoneState");
    }

    @Override
    protected String getShortUsageText() {
        return "getPhoneState - Lists basic phone and sim information";
    }
}
