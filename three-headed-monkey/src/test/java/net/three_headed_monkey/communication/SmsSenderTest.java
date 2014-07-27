package net.three_headed_monkey.communication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;

import net.three_headed_monkey.data.PhoneNumberInfo;
import net.three_headed_monkey.service.SimCardCheckService;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowPreferenceManager;
import org.robolectric.shadows.ShadowSmsManager;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.shadowOf;


@RunWith(RobolectricGradleTestRunner.class)
public class SmsSenderTest extends TestBase {
    SmsSender smsSender;
    ShadowSmsManager shadowSmsManager;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        smsSender = SmsSender_.getInstance_(application);
        shadowSmsManager = shadowOf(SmsManager.getDefault());
        application.phoneNumberSettings.removeAll();
        SharedPreferences sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(Robolectric.application.getApplicationContext());
        sharedPreferences.edit().putString("pref_text_sms_notification_text", "Default Text").commit();
    }

    @Test
    public void testSendSmsToSpecificNumber() {
        String number = "+424242";
        String text = "your phone is stolen!!111!1";

        smsSender.sendSms(text, number);
        ShadowSmsManager.TextSmsParams last_msg = shadowSmsManager.getLastSentTextMessageParams();
        assertThat(last_msg.getText(), equalTo(text));
        assertThat(last_msg.getDestinationAddress(), equalTo(number));
    }

    @Test
    public void testSendSmsNotification() {
        String text = "message @all, ding dong your sim is gone";
        PhoneNumberInfo phoneNumberInfo1 = new PhoneNumberInfo();
        phoneNumberInfo1.phoneNumber = "+1337";
        PhoneNumberInfo phoneNumberInfo2 = new PhoneNumberInfo();
        phoneNumberInfo2.phoneNumber = "002121";

        application.phoneNumberSettings.addPhoneNumber(phoneNumberInfo1);
        application.phoneNumberSettings.addPhoneNumber(phoneNumberInfo2);

        NotificationManager notificationManager = NotificationManager_.getInstance_(application);
        notificationManager.sendNotification(NotificationManager.NotificationType.SMS, text);

        ShadowSmsManager.TextSmsParams last_msg = shadowSmsManager.getLastSentTextMessageParams();

        assertThat(last_msg.getDestinationAddress(), anyOf(equalTo(phoneNumberInfo1.phoneNumber), equalTo(phoneNumberInfo2.phoneNumber)));
    }

    @Test
    public void testSimCardCheckServiceUseRightMessage() {
        String text = "This is the right message";
        SharedPreferences sharedPreferences = ShadowPreferenceManager.getDefaultSharedPreferences(Robolectric.application.getApplicationContext());
        sharedPreferences.edit().putString("pref_text_sms_notification_text", text).commit();

        PhoneNumberInfo phoneNumberInfo1 = new PhoneNumberInfo();
        phoneNumberInfo1.phoneNumber = "+1337";
        application.phoneNumberSettings.addPhoneNumber(phoneNumberInfo1);

        TestSimCardCheckService simCardCheckService = new TestSimCardCheckService();
        simCardCheckService.onHandleIntent(null);

        ShadowSmsManager.TextSmsParams last_msg = shadowSmsManager.getLastSentTextMessageParams();
        assertThat(last_msg.getText(), equalTo(text));
    }

    private static class TestSimCardCheckService extends SimCardCheckService {
        @Override
        public void onHandleIntent(Intent intent) {
            super.onHandleIntent(intent);
        }
    }

}
