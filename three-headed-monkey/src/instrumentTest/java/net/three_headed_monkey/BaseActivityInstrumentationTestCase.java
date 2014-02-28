package net.three_headed_monkey;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Path;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import net.three_headed_monkey.utils.RootUtils;

import java.io.File;

public abstract class BaseActivityInstrumentationTestCase<T extends android.app.Activity> extends ActivityInstrumentationTestCase2<T> {

    protected String original_settings_backup_file;
    protected String test_settings_backup_file;

    public BaseActivityInstrumentationTestCase(Class activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("pref_bool_root_settings_backup", false).commit();
        File basedir = (new ContextWrapper(getActivity())).getFilesDir();
        test_settings_backup_file = (new File(basedir, "3hm-settings-backup-TEST.xml")).getAbsolutePath();
        File f = new File(test_settings_backup_file);
        if(f.exists()) {
            f.delete();
        }
        original_settings_backup_file = RootUtils.SETTINGS_BACKUP_FILENAME;
        RootUtils.SETTINGS_BACKUP_FILENAME = test_settings_backup_file;
    }


    @Override
    protected void tearDown() throws Exception {
        RootUtils.SETTINGS_BACKUP_FILENAME = original_settings_backup_file;
        deleteSettingsBackupTestFile();
        super.tearDown();
    }

    private void deleteSettingsBackupTestFile(){
        File f = new File(test_settings_backup_file);
        if(f.exists()) {
            f.delete();
        }
    }
}
