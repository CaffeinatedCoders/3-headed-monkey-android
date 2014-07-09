package net.three_headed_monkey.service;

import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.communication.OutgoingCommunicationFactory;
import net.three_headed_monkey.data.PhoneNumberInfo;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;

import org.apache.commons.codec.binary.Hex;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowApplication;
import static org.robolectric.Robolectric.shadowOf;


import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@RunWith(RobolectricGradleTestRunner.class)
public class DataSmsReceivedServiceTest {
    String testMessagePduString = "06913496096006440c910000000000000004417090114390801406050410b1000067657450686f6e655374617465";
    byte[] testMessagePdu;
    DataSmsReceivedService dataSmsReceivedService;
    ThreeHeadedMonkeyApplication application;
    ShadowApplication shadowApplication;

    @Before
    public void setUp() throws Exception {
        application = (ThreeHeadedMonkeyApplication) Robolectric.application;
        application.phoneNumberSettings.removeAll();
        application.phoneNumberSettings.addPhoneNumber(new PhoneNumberInfo("+000000000000", "TEST"));
        shadowApplication = shadowOf(application);
        dataSmsReceivedService = new DataSmsReceivedService();
        testMessagePdu = Hex.decodeHex(testMessagePduString.toCharArray());
    }

    @After
    public void tearDown() {
        application.phoneNumberSettings.removeAll();
    }

    @Test
    public void shouldStartCommandExecutorServiceWithCorrectParameters() {
        Intent intent = new Intent();
        intent.putExtra("pdus", new Object[]{testMessagePdu});
        dataSmsReceivedService.onHandleIntent(intent);


        Intent startedIntent = shadowApplication.peekNextStartedService();
        assertThat(startedIntent, notNullValue());
        assertThat(startedIntent.getStringExtra(CommandExecutorService.INTENT_COMMAND_STRING_PARAM), equalTo("getPhoneState"));
        assertThat(startedIntent.getStringExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM), equalTo(OutgoingCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_DATASMS));
        assertThat(startedIntent.getStringExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_SENDER_ADDRESS_PARAM), equalTo("+000000000000"));
    }
}
