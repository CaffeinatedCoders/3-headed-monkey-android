package net.three_headed_monkey.commands;

import android.content.Context;
import android.telephony.TelephonyManager;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.communication.DummyOutgoingCommandCommunication;
import net.three_headed_monkey.custom_shadows.ShadowTelephonyManager;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
@Config(shadows = net.three_headed_monkey.custom_shadows.ShadowTelephonyManager.class)
public class GetPhoneStateCommandTest extends TestBase {
    private static final String SERIAL_NUMBER = "42420002121";
    private static final String OPERATOR = "Robolectric";
    private static final String COUNTRY_CODE = "XY";
    private static final String DEVICE_ID = "imei";
    private static final String SUBSCRIBER_ID = "imsi";

    DummyOutgoingCommandCommunication outgoingCommunication;
    GetPhoneStateCommand command;

    private TelephonyManager telephonyManager;
    private ShadowTelephonyManager shadowTelephonyManager;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        telephonyManager = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
        shadowTelephonyManager = (ShadowTelephonyManager) shadowOf(telephonyManager);
        shadowTelephonyManager.setSimOperatorName(OPERATOR);
        shadowTelephonyManager.setSimCountryIso(COUNTRY_CODE);
        shadowTelephonyManager.setSimSerialNumber(SERIAL_NUMBER);
        shadowTelephonyManager.setDeviceId(DEVICE_ID);
        shadowTelephonyManager.setSubscriberId(SUBSCRIBER_ID);

        outgoingCommunication = new DummyOutgoingCommandCommunication();
        command = new GetPhoneStateCommand(application);

        command.setCommandString("getPhoneState");
        command.setPrototype(false);
        command.setOutgoingCommandCommunication(outgoingCommunication);
    }

    @Test
    public void testExpectedOutput() {
        command.run();
        String output = outgoingCommunication.getLastMessage();

        assertThat(output, notNullValue());
        assertThat(output, allOf(
                CoreMatchers.containsString("simSerial: " + SERIAL_NUMBER),
                containsString("simOperator: " + OPERATOR),
                containsString("simCountryCode: " + COUNTRY_CODE),
                containsString("subscriberId: " + SUBSCRIBER_ID),
                containsString("deviceId: " + DEVICE_ID)
        ));
    }

}
