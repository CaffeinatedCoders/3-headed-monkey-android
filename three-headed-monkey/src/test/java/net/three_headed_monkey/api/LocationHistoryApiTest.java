package net.three_headed_monkey.api;


import android.location.Location;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import net.three_headed_monkey.data.LocationHistoryDatabase;
import net.three_headed_monkey.data.ServiceInfo;
import net.three_headed_monkey.test_utils.ApiUtils;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static net.three_headed_monkey.test_utils.LocationUtils.createLocation;

@RunWith(RobolectricGradleTestRunner.class)
public class LocationHistoryApiTest extends TestBase {

    ServiceInfo serviceInfo;
    LocationHistoryApi locationHistoryApi;
    ApiUtils apiUtils = new ApiUtils();

    @Before
    public void setUp() throws Exception {
        super.setUp();
        serviceInfo = apiUtils.getServiceInfoForMockWebserver();
        locationHistoryApi = new LocationHistoryApi(serviceInfo, application);
    }

    @Test
    public void updateLocationHistoryShouldPostToApi() throws Exception {
        LocationHistoryDatabase mockedDatabase = createMockLocationHistoryDatabase();
        Whitebox.setInternalState(locationHistoryApi, "database", mockedDatabase);
        MockWebServer server = apiUtils.getMockWebserver();
        server.enqueue(new MockResponse().setResponseCode(200));
        server.play(serviceInfo.baseUrlPort);

        locationHistoryApi.updateLocationHistory();

        RecordedRequest recordedRequest = server.takeRequest();
        assertThat(serviceInfo.getDeviceApiV1Url() + "/locations", CoreMatchers.endsWith(recordedRequest.getPath()));
        assertThat(recordedRequest.getMethod(), equalTo("POST"));
        assertThat(recordedRequest.getHeader("Content-Type"), containsString("application/json"));

        String requestBody = recordedRequest.getUtf8Body();
        //@todo check request body or test serializer seperatly

        server.shutdown();
    }

    private LocationHistoryDatabase createMockLocationHistoryDatabase() {
        LocationHistoryDatabase database = mock(LocationHistoryDatabase.class);
        when(database.getLocationsNewerThan(anyLong())).thenReturn(createTestLocations());
        return database;
    }

    private List<Location> createTestLocations() {
        Location location1 = createLocation(1);
        location1.setTime(2);
        Location location2 = createLocation(2);
        location2.setTime(4);
        Location location3 = createLocation(3);
        location3.setTime(6);
        return new ArrayList<Location>(Arrays.asList(new Location[]{location1, location2, location3}));
    }

}
