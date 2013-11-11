package net.three_headed_monkey.ui;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import net.three_headed_monkey.R;



public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity_> {
    Solo solo;
    PrefsFragment prefsFragment;


    public MainActivityTest() {
        super("net.three_headed_monkey", MainActivity_.class);
    }

    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        prefsFragment = (PrefsFragment) getActivity().getFragmentManager().findFragmentById(R.id.preference_fragment);
    }

    public void testDialerCodeSummeryUpdate() {
        solo.clickOnText("Dialer Code");
        solo.clearEditText(0);
        solo.enterText(0, "***1234567890###");
        solo.clickOnText("OK");
        solo.waitForDialogToClose(5);
        assertTrue("Dialer Code description should contain new number", prefsFragment.pref_text_dialer_number.getSummary().toString().contains("***1234567890###"));
    }


    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
