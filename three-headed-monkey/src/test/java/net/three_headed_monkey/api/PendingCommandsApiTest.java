package net.three_headed_monkey.api;


import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import net.three_headed_monkey.data.PendingCommandFromApi;
import net.three_headed_monkey.data.ServiceInfo;
import net.three_headed_monkey.test_utils.ApiUtils;
import net.three_headed_monkey.test_utils.RobolectricGradleTestRunner;
import net.three_headed_monkey.test_utils.TestBase;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(RobolectricGradleTestRunner.class)
public class PendingCommandsApiTest extends TestBase {
    ServiceInfo serviceInfo;
    ApiUtils apiUtils = new ApiUtils();
    PendingCommandsApi pendingCommandsApi;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        serviceInfo = apiUtils.getServiceInfoForMockWebserver();
        pendingCommandsApi = new PendingCommandsApi(serviceInfo);
    }

    @Test
    public void testGetPendingCommandsApiResponse() throws Exception {
        MockWebServer server = apiUtils.getMockWebserver();
        server.enqueue(getMockResponse());
        server.play(serviceInfo.baseUrlPort);

        List<PendingCommandFromApi> commands = pendingCommandsApi.getPendingCommands();
        server.shutdown();

        assertThat(commands.size(), equalTo(2));
        assertThat(commands.get(0).id, equalTo(21));
        assertThat(commands.get(0).command, equalTo("alarm"));
        assertThat(commands.get(0).serviceInfo, equalTo(serviceInfo));
        assertThat(commands.get(1).id, equalTo(42));
        assertThat(commands.get(1).command, equalTo("dummy command"));
        assertThat(commands.get(1).serviceInfo, equalTo(serviceInfo));

    }

    private static MockResponse getMockResponse () {
        return new MockResponse().setResponseCode(200).setBody("{\"name\":\"testdevice\",\"last_location\":\"2014-12-04T11:50:27.000Z\"," +
                "\"pendingCommands\":[" +
                "{\"id\": 21, \"command\":\"alarm\"}," +
                "{\"id\": 42, \"command\":\"dummy command\"}" +
                "]}");
    }


}
