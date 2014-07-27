package net.three_headed_monkey.ui;

import android.app.Activity;
import android.widget.TextView;

import net.three_headed_monkey.R;
import net.three_headed_monkey.utils.SecureSettingsUtils;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_supported_features)
public class SupportedFeaturesActivity extends Activity {
    @ViewById
    TextView text_secureSettingsEnabled, text_secureSettingsLocationEnabled;

    @Override
    protected void onResume() {
        super.onResume();
        SecureSettingsUtils secureSettingsUtils = new SecureSettingsUtils(this);
        boolean secure_settings_available = secureSettingsUtils.testAccess();
        if (secure_settings_available) {
            text_secureSettingsEnabled.setText(getString(R.string.yes));
            text_secureSettingsEnabled.setTextColor(getResources().getColor(R.color.Positive));
        } else {
            text_secureSettingsEnabled.setText(getString(R.string.no));
            text_secureSettingsEnabled.setTextColor(getResources().getColor(R.color.Negative));
        }
        if (secure_settings_available && secureSettingsUtils.locationModeSettingsAvailable()) {
            text_secureSettingsLocationEnabled.setText(getString(R.string.yes));
            text_secureSettingsLocationEnabled.setTextColor(getResources().getColor(R.color.Positive));
        } else {
            text_secureSettingsLocationEnabled.setText(getString(R.string.no));
            text_secureSettingsLocationEnabled.setTextColor(getResources().getColor(R.color.Negative));
        }
    }
}
