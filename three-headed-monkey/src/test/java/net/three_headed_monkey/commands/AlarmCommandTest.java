package net.three_headed_monkey.commands;

import android.media.MediaPlayer;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.ui.AlarmCommandActivity;
import net.three_headed_monkey.ui.AlarmCommandActivity_;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertTrue;
import static org.robolectric.Robolectric.application;
import static org.robolectric.Robolectric.shadowOf;
import static org.robolectric.Robolectric.buildActivity;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@RunWith(RobolectricGradleTestRunner.class)
public class AlarmCommandTest {

    ShadowApplication shadowApplication;

    @Before
    public void setUp() {

        shadowApplication = shadowOf(application);

    }

    @Test
    public void commandShouldStartActivity() {
        AlarmCommand command = new AlarmCommand((ThreeHeadedMonkeyApplication)application);
        command.setCommandString("alarm");
        command.setPrototype(false);
        command.run();
        assertThat(shadowApplication.getNextStartedActivity().getComponent().getClassName(), equalTo(AlarmCommandActivity_.class.getName()));
    }

    @Test
    public void alarmCommandActivityShouldPlayAlarmSoundWhenStarted() throws InterruptedException {
        AlarmCommandActivity activity = buildActivity(AlarmCommandActivity_.class).create().resume().get();
        Thread.sleep(1000);
        MediaPlayer mediaPlayer = activity.mediaPlayer;
        assertTrue(mediaPlayer.isPlaying());
    }


}
