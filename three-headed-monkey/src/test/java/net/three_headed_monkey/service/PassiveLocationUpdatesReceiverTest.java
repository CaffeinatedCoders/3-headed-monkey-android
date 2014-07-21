package net.three_headed_monkey.service;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.three_headed_monkey.test_utils.LocationUtils.createLocation;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class PassiveLocationUpdatesReceiverTest extends TestBase {

    PassiveLocationUpdatesReceiver receiver;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        receiver = new PassiveLocationUpdatesReceiver();
    }

    @Test
    public void shouldStartCorrespondingService() {
        Location location = createLocation(0);
        Intent intent = new Intent(PassiveLocationUpdatesReceiver.INTENT_ACTION);
        intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        receiver.onReceive(application, intent);
        Intent started_service_intent = shadowApplication.getNextStartedService();
        assertThat(started_service_intent.getComponent().getClassName(), equalTo(PassiveLocationUpdatesReceivedService.class.getName()));
        assertThat(intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED), equalTo((Object) location));

    }

}
