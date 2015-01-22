package net.three_headed_monkey.test_utils;

import com.squareup.okhttp.mockwebserver.MockWebServer;

import net.three_headed_monkey.data.ServiceInfo;

import javax.net.ssl.SSLContext;

public class ApiUtils {

    public static int MOCKWEBSERVER_PORT = 4242;

    SSLUtils sslUtils;

    public ApiUtils() {
        sslUtils = new SSLUtils();
    }

    public ServiceInfo getServiceInfoForMockWebserver() {
        ServiceInfo serviceInfo = new ServiceInfo("localhost", MOCKWEBSERVER_PORT, "SomeDeviceKey");
        serviceInfo.certHash = SSLUtils.TEST_CERT1_HASH;
        return serviceInfo;
    }

    public MockWebServer getMockWebserver() throws Exception {
        MockWebServer server = new MockWebServer();
        SSLContext sslContext = sslUtils.getSSLContext(sslUtils.getTestCert1());
        server.useHttps(sslContext.getSocketFactory(), false);
        return server;
    }


}
