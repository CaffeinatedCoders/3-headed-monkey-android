package net.three_headed_monkey.data;

import android.content.Context;
import android.telephony.TelephonyManager;

public class SimCardInfo {
    public String serial_number = "";
    public String country_iso_code = "";
    public String operator_name = "";

    private SimCardInfo(){};
    public static SimCardInfo createFromSimCard(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState=tm.getSimState();
        if(simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN)
            return null;
        SimCardInfo simCardInfo = new SimCardInfo();
        simCardInfo.serial_number = tm.getSimSerialNumber();
        simCardInfo.country_iso_code = tm.getSimCountryIso();
        simCardInfo.operator_name = tm.getSimOperatorName();
        return simCardInfo;
    }

    @Override
    public boolean equals(Object o) {
        if(o==this)
            return true;
        if(!o.getClass().equals(SimCardInfo.class))
            return false;
        SimCardInfo simCardInfo = (SimCardInfo)o;
        return simCardInfo.serial_number.equals(serial_number);
    }
}