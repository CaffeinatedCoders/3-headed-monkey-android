package net.three_headed_monkey.service;


import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import net.three_headed_monkey.data.LocationHistoryDatabase;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static net.three_headed_monkey.test_utils.LocationUtils.createLocation;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class PassiveLocationUpdatesReceivedServiceTest extends TestBase {

    private LocationHistoryDatabase lhdb;
    private PassiveLocationUpdatesReceivedService service;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        lhdb = new LocationHistoryDatabase(application);
        lhdb.deleteAll();
        service = new PassiveLocationUpdatesReceivedService();
    }

    @Test
    public void shouldInsertLocationUpdateInDb() {
        Location location = createLocation(0);
        Intent intent = new Intent("doesntmatter");
        intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        service.onHandleIntent(intent);
        assertThat(lhdb.getLast().location, equalTo(location));
    }


}
