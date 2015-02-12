package net.three_headed_monkey.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.three_headed_monkey.R;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowActivityManager;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowPreferenceManager;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
public class ApplicationLockActivityTest extends TestBase {
    private Activity activity;
    private EditText edit_text_password;
    private static final String SHARED_PREFERENCES_PASSWORD_KEY = "__PASSWORD__";

    @Before
    public void setUp() throws Exception {
        super.setUp();
        SharedPreferences sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(Robolectric.application.getApplicationContext());
        sharedPreferences.edit().putString(SHARED_PREFERENCES_PASSWORD_KEY, "password").apply();

        activity = Robolectric.buildActivity(ApplicationLockActivity.class).create().get();
    }

    @Test
    public void testPositiveLogin() {
        edit_text_password = (EditText) activity.findViewById(R.id.dialog_application_lock_login_password);
        AlertDialog shadow = ShadowAlertDialog.getLatestAlertDialog();
        assertEquals(((EditText) shadow.findViewById(R.id.dialog_application_lock_login_password)).getText().toString(), "");
        ((EditText) shadow.findViewById(R.id.dialog_application_lock_login_password)).setText("password");
        shadow.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        ShadowActivity shadowActivity = Robolectric.shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = Robolectric.shadowOf(startedIntent);
        assertThat(shadowIntent.getComponent().getClassName(), equalTo(MainActivity_.class.getName()));
    }

    @Test
    public void testNegativLogin() {
        edit_text_password = (EditText) activity.findViewById(R.id.dialog_application_lock_login_password);
        AlertDialog shadow = ShadowAlertDialog.getLatestAlertDialog();
        assertEquals(((EditText) shadow.findViewById(R.id.dialog_application_lock_login_password)).getText().toString(), "");
        ((EditText) shadow.findViewById(R.id.dialog_application_lock_login_password)).setText("password2");
        shadow.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        ShadowActivity shadowActivity = Robolectric.shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertNull(startedIntent);
    }

}
