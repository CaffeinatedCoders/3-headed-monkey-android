package net.three_headed_monkey.data;


import android.location.Location;

import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static net.three_headed_monkey.test_utils.LocationUtils.createLocation;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class LocationHistoryDatabaseTest extends TestBase {
    LocationHistoryDatabase lhdb;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        lhdb = new LocationHistoryDatabase(application);
    }

    @Test
    public void testInsertDelete() {
        Location location1 = createLocation(1);
        Location location2 = createLocation(2);

        lhdb.insert(location1);
        assertThat(lhdb.getLast().location, equalTo(location1));
        lhdb.insert(location2);
        assertThat(lhdb.getLast().location, equalTo(location2));

        LocationHistoryDatabase.LocationContainer container = lhdb.getLast();
        lhdb.delete(container.id); //should be location2
        assertThat(lhdb.getLast().location, equalTo(location1));

    }

    @Test
    public void testGetAll() {
        Location location1 = createLocation(1);
        Location location2 = createLocation(2);
        Location location3 = createLocation(3);
        Location location4 = createLocation(4);
        lhdb.insert(location1);
        lhdb.insert(location2);
        lhdb.insert(location3);
        lhdb.insert(location4);

        List<Location> locations = lhdb.getAll();
        assertThat(locations.size(), equalTo(4));
        assertThat(locations, hasItems(location1, location2, location3, location4));
    }

    @Test
    public void testGetLocationsNewerThan() {
        Location location1 = createLocation(1);
        location1.setTime(2);
        Location location2 = createLocation(2);
        location2.setTime(4);
        Location location3 = createLocation(3);
        location3.setTime(6);
        Location location4 = createLocation(4);
        location4.setTime(8);
        lhdb.insert(location1);
        lhdb.insert(location2);
        lhdb.insert(location3);
        lhdb.insert(location4);

        List<Location> locations = lhdb.getLocationsNewerThan(5);
        assertThat(locations.size(), equalTo(2));
        assertThat(locations, hasItems(location3, location4));
    }
}
