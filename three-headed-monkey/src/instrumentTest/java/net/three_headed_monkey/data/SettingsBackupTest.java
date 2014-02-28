package net.three_headed_monkey.data;

import android.preference.PreferenceManager;

import com.jayway.android.robotium.solo.Solo;

import net.three_headed_monkey.BaseActivityInstrumentationTestCase;
import net.three_headed_monkey.ui.MainActivity_;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class SettingsBackupTest extends BaseActivityInstrumentationTestCase<MainActivity_> {
    private static final int WAITING_FOR_BACKGROUND_ROOT_MILLIS = 5000;

    private Solo solo;

    public SettingsBackupTest() {
        super(MainActivity_.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("pref_bool_root_settings_backup", true).commit();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testSettingsBackupAfterDialerCOdeChange() throws InterruptedException, FileNotFoundException {
        solo.clickOnText("Dialer Code");
        solo.clearEditText(0);
        solo.enterText(0, "***000###");
        solo.clickOnText("OK");
        solo.clickOnText("Dialer Code");
        solo.clearEditText(0);
        solo.enterText(0, "***999###");
        solo.clickOnText("OK");

        solo.sleep(WAITING_FOR_BACKGROUND_ROOT_MILLIS);

        File backup_file = new File(test_settings_backup_file);
        assertTrue(backup_file.exists());

        String content = new Scanner(new File(test_settings_backup_file)).useDelimiter("\\Z").next();
        assertTrue(content.contains("***999###"));
    }
}
