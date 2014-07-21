package net.three_headed_monkey.utils;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import net.three_headed_monkey.ui.LauncherActivity;


/**
 * This class is used for settings that effect the android system and might have to be reapplied after reboot or factory reset
 */
public class SystemSettings {
    private Application app;

    public SystemSettings(Application application) {
        this.app = application;
    }

    public void applyAll() {
        applyLauncherIconHidden();
    }

    public void applyLauncherIconHidden() {
        boolean hide_launcher = PreferenceManager.getDefaultSharedPreferences(app).getBoolean("pref_bool_hide_launcher", false);
        PackageManager packageManager = app.getPackageManager();
        String packageName = app.getPackageName();
        ComponentName componentName = new ComponentName(packageName, LauncherActivity.class.getName());
        int new_component_state = hide_launcher ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        packageManager.setComponentEnabledSetting(componentName, new_component_state, PackageManager.DONT_KILL_APP);
    }

}
