package net.three_headed_monkey.api;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;

import net.three_headed_monkey.data.ServiceInfo;

import java.io.IOException;

public abstract class BaseApi {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    protected ServiceInfo serviceInfo;

    public BaseApi(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public abstract Response doGetRequest(String relativeUrl) throws IOException;

    /**
     *
     * @param relativeUrl
     * @param parameters json formatted string of post parameter
     * @return
     * @throws IOException
     */
    public abstract Response doPostRequest(String relativeUrl, String parameters) throws IOException;
}
