package net.three_headed_monkey.communication;

import android.telephony.SmsManager;
import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.data.PhoneNumberInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowSmsManager;

import static org.robolectric.Robolectric.shadowOf;

import net.three_headed_monkey.test_utils.*;


@RunWith(RobolectricGradleTestRunner.class)
public class SmsSenderTest {
    SmsSender smsSender;
    ShadowSmsManager shadowSmsManager;
    ThreeHeadedMonkeyApplication application;

    @Before
    public void setUp(){
        application = (ThreeHeadedMonkeyApplication) Robolectric.application;
        smsSender = SmsSender_.getInstance_(application);
        shadowSmsManager = shadowOf(SmsManager.getDefault());
    }

    @Test
    public void testSendSmsToSpecificNumber(){
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

        assertThat(last_msg.getDestinationAddress(), anyOf( equalTo(phoneNumberInfo1.phoneNumber), equalTo(phoneNumberInfo2.phoneNumber) ));
    }

}
