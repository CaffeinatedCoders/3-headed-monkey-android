package net.three_headed_monkey.ui;

import android.app.Activity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import net.three_headed_monkey.R;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class PhoneNumbersSettingsActivityTest extends TestBase {
    private TextView text_serial_number;
    private TextView text_country_code;
    private TextView text_operator;
    private Activity activity;
    private ListView authorized_simcards_list;
    private Button button_authorize_card;
    private TextView text_currently_authorized;
    private Button button_add_phonenumber;
    private ListView phonenumbers_list;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        activity = Robolectric.buildActivity(PhoneNumbersSettingsActivity.class).create().get();
        phonenumbers_list = (ListView) activity.findViewById(R.id.phone_numbers_settings_list_view);
        button_add_phonenumber = (Button) activity.findViewById(R.id.action_add);
    }

    @Test
    public void testAdd() {
        assertThat(phonenumbers_list.getCount(), equalTo(0));
    }

}
