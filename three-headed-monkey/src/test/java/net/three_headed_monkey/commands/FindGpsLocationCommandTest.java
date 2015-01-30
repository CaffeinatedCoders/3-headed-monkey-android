package net.three_headed_monkey.commands;

import android.location.Location;
import android.location.LocationManager;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.communication.DummyOutgoingCommandCommunication;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class FindGpsLocationCommandTest extends TestBase {
    DummyOutgoingCommandCommunication outgoingCommunication;
    FindGpsLocationCommand command;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, true);
        outgoingCommunication = new DummyOutgoingCommandCommunication();
        command = new FindGpsLocationCommand((ThreeHeadedMonkeyApplication) application);
        command.TIMEOUT_SECONDS = 10;
        command.setCommandString("findGpsLocation");
        command.setPrototype(false);
        command.setOutgoingCommandCommunication(outgoingCommunication);

    }

    @Ignore("Not working because of Robolectric bug")
    //@TODO: Remove once https://github.com/robolectric/robolectric/pull/1182 has been merged
    @Test
    public void simpleTestSingleLocationExpected() throws InterruptedException {
        command.MAX_RESPONSES = 1;
        command.TIMEOUT_SECONDS = 10;

        Location location = createLocation(LocationManager.GPS_PROVIDER, 47.073557, 15.437717, 5.f);


        Thread thread = new Thread(command);
        thread.start();
        Thread.sleep(7000, 0);
        System.out.println(shadowLocationManager.getRequestLocationUpdateListeners().size());
        shadowLocationManager.simulateLocation(location);
//        Robolectric.runUiThreadTasksIncludingDelayedTasks();
        thread.join();

        String output = outgoingCommunication.getLastMessage();

        assertThat(output, notNullValue());
        assertThat(output, allOf(
                containsString("gps:"),
                containsString("lat: 47.073557"),
                containsString("long: 15.437717"),
                containsString("acc: 5")
        ));
    }

    private Location createLocation(String provider, double latitude, double longitude, float accuracy) {
        Location location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAccuracy(accuracy);
        location.setTime(System.currentTimeMillis() - 10000);
        return location;
    }

}
