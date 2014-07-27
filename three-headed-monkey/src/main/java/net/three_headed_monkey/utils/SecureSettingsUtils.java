package net.three_headed_monkey.utils;


import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class SecureSettingsUtils {
    public final static String TAG = "SecureSettingsUtils";
    Context context;

    public SecureSettingsUtils(Context context) {
        this.context = context;
    }

    public boolean testAccess() {
        try {
            String prop = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
            Settings.Secure.putString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, prop);
        } catch (SecurityException e) {
            Log.d(TAG, "Np SecureSettings access", e);
            return false;
        }
        return true;
    }

    public boolean locationModeSettingsAvailable() {
        return Build.VERSION.SDK_INT >= 19 && getLocationMode() != -1;
    }

    public int getLocationMode() {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            return -1;
        }
    }

    public void setLocationMode(int mode) {
        try {
            Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, mode);
        } catch (SecurityException e) {
            Log.d(TAG, "Can't set location mode", e);
        }
    }

}
