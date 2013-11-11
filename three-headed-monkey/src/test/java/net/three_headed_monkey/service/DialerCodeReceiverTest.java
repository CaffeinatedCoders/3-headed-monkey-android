package net.three_headed_monkey.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.ui.MainActivity_;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.robolectric.Robolectric.shadowOf;

import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowInputDevice;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowPreferenceManager;

@RunWith(RobolectricGradleTestRunner.class)
public class DialerCodeReceiverTest {
    private Intent outgoingCallIntent;

    @Before
    public void setUp(){
        SharedPreferences sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(Robolectric.application.getApplicationContext());
        sharedPreferences.edit().putString("pref_text_dialer_number", "***#4242").commit();

        outgoingCallIntent = new Intent();
        outgoingCallIntent.setAction(Intent.ACTION_NEW_OUTGOING_CALL);
        outgoingCallIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, "***#4242");

    }

    @Test
    public void testBroadcast() {
        Activity activity = new Activity();
        ShadowActivity shadowActivity = shadowOf(activity);
        DialerCodeReceiver dialerCodeReceiver = new DialerCodeReceiver();
        try {
            dialerCodeReceiver.onReceive(activity, outgoingCallIntent);
        } catch (Exception e){}

        Intent startedIntent = shadowActivity.getNextStartedActivity();
        assertNotNull(startedIntent);
        ShadowIntent shadowIntent = shadowOf(startedIntent);

        assertThat("Main Activity should have been started", shadowIntent.getComponent().getClassName(), equalTo(MainActivity_.class.getName()));
    }

}

