package net.three_headed_monkey.commands;

import android.media.MediaPlayer;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;
import net.three_headed_monkey.ui.AlarmCommandActivity;
import net.three_headed_monkey.ui.AlarmCommandActivity_;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Robolectric.buildActivity;

@RunWith(RobolectricGradleTestRunner.class)
public class AlarmCommandTest extends TestBase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void commandShouldStartActivity() {
        AlarmCommand command = new AlarmCommand((ThreeHeadedMonkeyApplication) application);
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
