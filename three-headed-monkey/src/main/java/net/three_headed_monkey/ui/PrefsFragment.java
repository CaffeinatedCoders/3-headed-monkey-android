package net.three_headed_monkey.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.App;
import com.googlecode.androidannotations.annotations.EFragment;

import net.three_headed_monkey.R;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication;

@EFragment
public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    Preference pref_btn_version;

    @App
    ThreeHeadedMonkeyApplication application;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.main_preferences);
//        findPreference("pref_btn_sim_card_settings").setOnPreferenceClickListener(this);
//        findPreference("pref_btn_phone_numbers_settings").setOnPreferenceClickListener(this);
        for(int x = 0; x < getPreferenceScreen().getPreferenceCount(); x++){
            PreferenceCategory category = (PreferenceCategory) getPreferenceScreen().getPreference(x);
            for(int y = 0; y < category.getPreferenceCount(); y++){
                Preference pref = category.getPreference(y);
                pref.setOnPreferenceClickListener(this);
            }
        }

        pref_btn_version = findPreference("pref_btn_version");

    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferenceValues();
    }

    public void updatePreferenceValues(){
        String version = "0.-1";
        try {
            version = application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionName;
        } catch (Exception ex) {

        }

        pref_btn_version.setTitle(getString(R.string.settings_preference_version_param, version));
        pref_btn_version.setSummary(application.getPackageName());
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Toast.makeText(getActivity(), key + " clicked!", Toast.LENGTH_SHORT).show();

        if(key == null)
            return false;

        if(key.equals("pref_btn_sim_card_settings")) {
            SimCardSettingsActivity_.intent(getActivity()).start();
            return true;
        } else if(key.equals("pref_btn_phone_numbers_settings")) {

            return true;
        }

        return false;
    }
}
