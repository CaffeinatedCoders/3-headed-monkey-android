package net.three_headed_monkey;

import android.app.Application;
import com.googlecode.androidannotations.annotations.EApplication;
import net.three_headed_monkey.data.PhoneNumberSettings;
import net.three_headed_monkey.data.SimCardSettings;

@EApplication
public class ThreeHeadedMonkeyApplication extends Application {
    public SimCardSettings simCardSettings;
    public PhoneNumberSettings phoneNumberSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        simCardSettings = new SimCardSettings(this);
        simCardSettings.load();
        phoneNumberSettings = new PhoneNumberSettings(this);
        phoneNumberSettings.loadSettings();
    }
}
