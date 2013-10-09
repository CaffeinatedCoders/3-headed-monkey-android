package net.three_headed_monkey.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import net.three_headed_monkey.R;

public class MainActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        PrefsFragment mPrefsFragment = new PrefsFragment();
        mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();

    }

    public static class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

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

                return true;
            } else if(key.equals("pref_btn_phone_numbers_settings")) {

                return true;
            }

            return false;
        }
    }
}
