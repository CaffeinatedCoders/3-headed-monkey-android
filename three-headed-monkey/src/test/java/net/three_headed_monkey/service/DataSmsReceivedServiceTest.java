package net.three_headed_monkey.service;

import android.content.Intent;

import net.three_headed_monkey.communication.OutgoingCommandCommunicationFactory;
import net.three_headed_monkey.data.PhoneNumberInfo;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.apache.commons.codec.binary.Hex;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class DataSmsReceivedServiceTest extends TestBase {
    String testMessagePduString = "06913496096006440c910000000000000004417090114390801406050410b1000067657450686f6e655374617465";
    byte[] testMessagePdu;
    DataSmsReceivedService dataSmsReceivedService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        application.phoneNumberSettings.removeAll();
        application.phoneNumberSettings.addPhoneNumber(new PhoneNumberInfo("+000000000000", "TEST"));
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
        assertThat(startedIntent.getStringExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_TYPE_PARAM), equalTo(OutgoingCommandCommunicationFactory.OUTGOING_COMMUNICATION_TYPE_DATASMS));
        assertThat(startedIntent.getStringExtra(CommandExecutorService.INTENT_OUTGOING_COMMUNICATION_SENDER_ADDRESS_PARAM), equalTo("+000000000000"));
    }
}
