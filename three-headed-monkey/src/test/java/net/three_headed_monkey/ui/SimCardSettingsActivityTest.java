package net.three_headed_monkey.ui;


import android.content.Context;
import android.widget.Button;
import android.widget.ListView;
import net.three_headed_monkey.custom_shadows.ShadowTelephonyManager;
import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import net.three_headed_monkey.R;


import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


import java.util.logging.Level;
import java.util.logging.Logger;

import static org.robolectric.Robolectric.application;
import static org.robolectric.Robolectric.newInstanceOf;
import static org.robolectric.Robolectric.shadowOf;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import net.three_headed_monkey.test_utils.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(shadows=net.three_headed_monkey.custom_shadows.ShadowTelephonyManager.class)
public class SimCardSettingsActivityTest {
    private TelephonyManager telephonyManager;
    private ShadowTelephonyManager shadowTelephonyManager;

    private static final String SERIAL_NUMBER = "42420002121";
    private static final String OPERATOR = "Robolectric";
    private static final String COUNTRY_CODE = "XY";
    private TextView text_serial_number;
    private TextView text_country_code;
    private TextView text_operator;
    private Activity activity;
    private ListView authorized_simcards_list;
    private Button button_authorize_card;
    private TextView text_currently_authorized;

    @Before
    public void setUp(){
        telephonyManager = (TelephonyManager) application.getSystemService(Context.TELEPHONY_SERVICE);
        shadowTelephonyManager = (ShadowTelephonyManager) shadowOf(telephonyManager);
        shadowTelephonyManager.setSimOperatorName(OPERATOR);
        shadowTelephonyManager.setSimCountryIso(COUNTRY_CODE);
        shadowTelephonyManager.setSimSerialNumber(SERIAL_NUMBER);
        Logger.getLogger("JUNIT").log(Level.INFO, "Package: " + application.toString());

        activity = Robolectric.buildActivity(SimCardSettingsActivity_.class).create().get();
        text_operator = (TextView) activity.findViewById(R.id.text_operator);
        text_country_code = (TextView) activity.findViewById(R.id.text_country_code);
        text_serial_number = (TextView) activity.findViewById(R.id.text_serial_number);
        text_currently_authorized = (TextView) activity.findViewById(R.id.text_currently_authorized);
        authorized_simcards_list = (ListView) activity.findViewById(R.id.authorized_simcards_list);
        button_authorize_card = (Button) activity.findViewById(R.id.button_authorize_card);
    }

    @Test
    public void testSimInfoShownOnActivityStart(){
        assertThat(text_country_code.getText().toString(), equalTo(COUNTRY_CODE));
        assertThat(text_operator.getText().toString(), equalTo(OPERATOR));
        assertThat(text_serial_number.getText().toString(), equalTo(SERIAL_NUMBER));
    }

    @Test
    public void testAuthorizeSim(){
        assertThat(text_currently_authorized.getText().toString(), equalTo("No"));
        assertThat(authorized_simcards_list.getCount(), equalTo(0));

        button_authorize_card.performClick();

        assertThat(text_currently_authorized.getText().toString(), equalTo("Yes"));
        assertThat(authorized_simcards_list.getCount(), equalTo(1));
    }

}
