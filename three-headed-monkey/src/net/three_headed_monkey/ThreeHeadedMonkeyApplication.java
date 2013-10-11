package net.three_headed_monkey;

import android.app.Application;
import net.three_headed_monkey.data.SimCardSettings;

public class ThreeHeadedMonkeyApplication extends Application {
    public SimCardSettings simCardSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        simCardSettings = new SimCardSettings(this);
        simCardSettings.load();
    }
}
