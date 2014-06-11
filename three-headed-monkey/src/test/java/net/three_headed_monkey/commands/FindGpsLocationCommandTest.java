package net.three_headed_monkey.commands;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import net.three_headed_monkey.ThreeHeadedMonkeyApplication;
import net.three_headed_monkey.communication.DummyOutgoingCommunication;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowLocationManager;
import org.robolectric.shadows.ShadowLog;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import static org.robolectric.Robolectric.application;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricGradleTestRunner.class)
public class FindGpsLocationCommandTest {
    DummyOutgoingCommunication outgoingCommunication;
    FindGpsLocationCommand command;

    ShadowLocationManager shadowLocationManager;
    LocationManager locationManager;

    @Before
    public void setUp() {
        locationManager = (LocationManager)Robolectric.application.getSystemService(Context.LOCATION_SERVICE);
        shadowLocationManager = shadowOf(locationManager);
        shadowLocationManager.setProviderEnabled(LocationManager.GPS_PROVIDER, true);
        outgoingCommunication = new DummyOutgoingCommunication();
        command = new FindGpsLocationCommand((ThreeHeadedMonkeyApplication)application);
        command.TIMEOUT_SECONDS = 10;
        command.setCommandString("findGpsLocation");
        command.setPrototype(false);
        command.setOutgoingCommunication(outgoingCommunication);

    }

    @Test
    public void simpleTestSingleLocationExpected() throws InterruptedException {
        command.MAX_RESPONSES = 1;

        Location location = createLocation("GPS", 47.073557, 15.437717, 5.f);


        Thread thread = new Thread(command);
        thread.start();
        Thread.sleep(7000,0);
        System.out.println(shadowLocationManager.getRequestLocationUpdateListeners().size());
        shadowLocationManager.simulateLocation(location);
        thread.join();

        String output = outgoingCommunication.getLastMessage();

        assertThat(output, notNullValue());
        assertThat(output, allOf(
                containsString("gps:"),
                containsString("lat: 47.073557"),
                containsString("long: 15.437717"),
                containsString("acc: 5"),
                containsString("deviceId: ")
        ));
    }

    private Location createLocation(String provider, double latitude, double longitude, float accuracy) {
        Location location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAccuracy(accuracy);
        location.setTime(System.currentTimeMillis()-10000);
        return location;
    }

}
