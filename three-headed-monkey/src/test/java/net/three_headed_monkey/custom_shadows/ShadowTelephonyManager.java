package net.three_headed_monkey.custom_shadows;

import android.telephony.TelephonyManager;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(TelephonyManager.class)
public class ShadowTelephonyManager extends org.robolectric.shadows.ShadowTelephonyManager {
    private String simOperatorName;
    private String simSerialNumber;
    private String subscriberId;

    public void setSimOperatorName(String simOperatorName){
        this.simOperatorName = simOperatorName;
    }

    public void setSimSerialNumber(String simSerialNumber){
        this.simSerialNumber = simSerialNumber;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Implementation
    public String getSimOperatorName(){
        return simOperatorName;
    }

    @Implementation
    public String getSimSerialNumber(){
        return simSerialNumber;
    }

    @Implementation
    public String getSubscriberId() { return subscriberId; }

}
