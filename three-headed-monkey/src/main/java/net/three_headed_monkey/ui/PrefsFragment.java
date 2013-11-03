package net.three_headed_monkey.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import com.googlecode.androidannotations.annotations.EFragment;
import net.three_headed_monkey.R;

@EFragment
public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.main_preferences);
        findPreference("pref_btn_sim_card_settings").setOnPreferenceClickListener(this);
        findPreference("pref_btn_phone_numbers_settings").setOnPreferenceClickListener(this);
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Toast.makeText(getActivity(), key + " clicked!", Toast.LENGTH_SHORT).show();

        if(key.equals("pref_btn_sim_card_settings")) {
            SimCardSettingsActivity_.intent(getActivity()).start();
            return true;
        } else if(key.equals("pref_btn_phone_numbers_settings")) {
            PhoneNumbersSettingsActivity_.intent(getActivity()).start();
            return true;
        }

        return false;
    }
}
