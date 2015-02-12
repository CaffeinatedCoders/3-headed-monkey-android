package net.three_headed_monkey.test.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

import net.three_headed_monkey.R;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication_;
import net.three_headed_monkey.test.BaseActivityInstrumentationTestCase;
import net.three_headed_monkey.ui.PhoneNumbersSettingsActivity;

public class phoneNumberSettingsActivityTest extends BaseActivityInstrumentationTestCase<PhoneNumbersSettingsActivity> {

    private Solo solo;
    private PhoneNumbersSettingsActivity activity;
    private ThreeHeadedMonkeyApplication_ application;
    private String phoneNumberSettingsSaveJson;

    public phoneNumberSettingsActivityTest() {
        super(PhoneNumbersSettingsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        // TODO: Fix SharedPrefences in Robotium tests
        this.savePhoneNumberSettingsFromSharedPreferences();
        this.clearPhoneNumberSettingsFromSharedPreferences();

        super.setUp();

        this.solo = new Solo(getInstrumentation(), getActivity());
        this.activity = getActivity();
        this.application = (ThreeHeadedMonkeyApplication_) this.activity.getApplication();
    }

    @MediumTest
    public void testAddValidPhoneNumbers() throws InterruptedException {
        ListView listView = (ListView) solo.getView(R.id.phone_numbers_settings_list_view);

        solo.clickOnActionBarItem(R.id.action_add);
        solo.waitForDialogToOpen();
        solo.clickInList(2);
        solo.waitForDialogToOpen();
        solo.enterText(solo.getEditText(0), "Mr. Lorem");
        solo.enterText(solo.getEditText(1), "+436609102556");
        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.waitForDialogToClose();

        assertEquals(1, listView.getAdapter().getCount());

        solo.clickOnActionBarItem(R.id.action_add);
        solo.waitForDialogToOpen();
        solo.clickInList(2);
        solo.waitForDialogToOpen();
        solo.enterText(solo.getEditText(0), "Mrs. Ipsum");
        solo.enterText(solo.getEditText(1), "041989779");
        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.waitForDialogToClose();

        assertEquals(2, listView.getAdapter().getCount());
    }

    @MediumTest
    public void testAddInvalidPhoneNumbers() throws InterruptedException {
        ListView listView = (ListView) solo.getView(R.id.phone_numbers_settings_list_view);

        solo.clickOnActionBarItem(R.id.action_add);
        solo.waitForDialogToOpen();
        solo.clickInList(2);
        solo.waitForDialogToOpen();
        solo.enterText(solo.getEditText(0), "Mr. Lorem");
        solo.enterText(solo.getEditText(1), "1-2-3848k");
        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.waitForDialogToClose();

        assertEquals(0, listView.getAdapter().getCount());

        solo.clickOnActionBarItem(R.id.action_add);
        solo.waitForDialogToOpen();
        solo.clickInList(2);
        solo.waitForDialogToOpen();
        solo.enterText(solo.getEditText(0), "Mrs. Ipsum");
        solo.enterText(solo.getEditText(1), "ipsum68@mail.com");
        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.waitForDialogToClose();

        assertEquals(0, listView.getAdapter().getCount());
    }

    @MediumTest
    public void testImportPhoneNumbers() throws InterruptedException {
        ListView listView = (ListView) solo.getView(R.id.phone_numbers_settings_list_view);

        solo.clickOnActionBarItem(R.id.action_add);
        solo.waitForDialogToOpen();
        solo.clickInList(2);
        solo.waitForDialogToOpen();
        solo.clickOnView(solo.getView(android.R.id.button1));
        solo.waitForDialogToClose();
        assertEquals(0, listView.getAdapter().getCount());
    }

    @Override
    protected void tearDown() throws Exception {

        this.clearPhoneNumberSettingsFromSharedPreferences();
        this.restorePhoneNumberSettingsToSharedPreferences();

        solo.finishOpenedActivities();
        super.tearDown();
    }

    private void savePhoneNumberSettingsFromSharedPreferences() {
        Context context = getInstrumentation().getTargetContext();
        this.phoneNumberSettingsSaveJson = PreferenceManager.getDefaultSharedPreferences(context).getString("PREFKEY_PhoneNumberSettings", "");
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
    }

    private void clearPhoneNumberSettingsFromSharedPreferences() {
        Context context = getInstrumentation().getTargetContext();
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
    }

    private void restorePhoneNumberSettingsToSharedPreferences() {
        Context context = getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("PREFKEY_PhoneNumberSettings", this.phoneNumberSettingsSaveJson).apply();
    }
}
