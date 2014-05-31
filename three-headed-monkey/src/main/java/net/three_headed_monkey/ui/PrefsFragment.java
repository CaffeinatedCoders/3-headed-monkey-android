package net.three_headed_monkey.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import net.three_headed_monkey.BuildConfig;
import net.three_headed_monkey.R;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.service.SimCardCheckService;
import net.three_headed_monkey.utils.RootUtils;
import net.three_headed_monkey.utils.SystemSettings;

import eu.chainfire.libsuperuser.Shell;

@EFragment
public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG="PrefsFragment";

    Preference pref_btn_version;
    public EditTextPreference pref_text_dialer_number;
    CheckBoxPreference pref_bool_root_settings_backup;

    Preference pref_btn_trigger_sim_check;

    Boolean su_available = false;

    @App
    ThreeHeadedMonkeyApplication application;

    Context context;

    SystemSettings systemSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        systemSettings = new SystemSettings(application);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.main_preferences);
//        findPreference("pref_btn_sim_card_settings").setOnPreferenceClickListener(this);
//        findPreference("pref_btn_phone_numbers_settings").setOnPreferenceClickListener(this);
        for(int x = 0; x < getPreferenceScreen().getPreferenceCount(); x++){
            Preference preference = getPreferenceScreen().getPreference(x);
            if(!(preference instanceof PreferenceCategory)){
                preference.setOnPreferenceClickListener(this);
                continue;
            }
            PreferenceCategory category = (PreferenceCategory) preference;
            for(int y = 0; y < category.getPreferenceCount(); y++){
                Preference pref = category.getPreference(y);
                pref.setOnPreferenceClickListener(this);
            }
        }

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        pref_btn_version = findPreference("pref_btn_version");
        pref_text_dialer_number = (EditTextPreference) findPreference("pref_text_dialer_number");
        pref_bool_root_settings_backup = (CheckBoxPreference) findPreference("pref_bool_root_settings_backup");

        PreferenceCategory developer_category = (PreferenceCategory) findPreference("prefcat_developer_settings");
        if(!BuildConfig.DEBUG){
            getPreferenceScreen().removePreference(developer_category);
        }

        pref_btn_trigger_sim_check = findPreference("pref_btn_trigger_sim_check");

    }

    @Override
    public void onResume() {
        super.onResume();
        checkForSuAndUpdateSettings();
        updatePreferenceValues();
    }

    @Override
    public void onDestroy() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Background
    public void checkForSuAndUpdateSettings(){
        su_available = Shell.SU.available();
        updateRootSettingsBackupPreferenceEnabled();
    }

    @UiThread
    public void updateRootSettingsBackupPreferenceEnabled(){
        pref_bool_root_settings_backup.setEnabled(su_available);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "Shared Preferences changed");
        if(key.equals("pref_bool_hide_launcher")){
            systemSettings.applyLauncherIconHidden();
        } else {
            updatePreferenceValues();
            boolean setting_backup_enabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("pref_bool_root_settings_backup", false);
            if(setting_backup_enabled && su_available){
                backupPreferencesToSystem();
            }
        }
    }

    @Background
    public void backupPreferencesToSystem(){
        RootUtils rootUtils = new RootUtils(context);
        rootUtils.backupSettingsFile();
    }

    public void updatePreferenceValues(){
        String version = "0.-1";
        try {
            version = application.getPackageManager().getPackageInfo(application.getPackageName(), 0).versionName;
        } catch (Exception ex) {

        }

        pref_btn_version.setTitle(getString(R.string.settings_preference_version_param, version));
        pref_btn_version.setSummary(application.getPackageName());

        pref_text_dialer_number.setSummary(getString(R.string.settings_preference_dialer_number_summery_param, pref_text_dialer_number.getText()));

    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        if(key == null)
            return false;

        if(key.equals("pref_btn_sim_card_settings")) {
            SimCardSettingsActivity_.intent(getActivity()).start();
            return true;
        } else if(key.equals("pref_btn_phone_numbers_settings")) {
            PhoneNumbersSettingsActivity_.intent(getActivity()).start();
            return true;
        } else if(key.equals("pref_btn_trigger_sim_check")) {
            Intent intentservice = new Intent(context, SimCardCheckService.class);
            context.startService(intentservice);
            Toast.makeText(context, "Triggered sim check", Toast.LENGTH_SHORT).show();
            return true;
        } else if(key.equals("pref_btn_open_command_shell")) {
            CommandShellActivity_.intent(getActivity()).start();
            return true;
        } else if(key.equals("pref_btn_supported_features")) {
            SupportedFeaturesActivity_.intent(getActivity()).start();
            return true;
        }

        return false;
    }

}
