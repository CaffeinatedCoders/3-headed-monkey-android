package net.three_headed_monkey.api;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;

import net.three_headed_monkey.data.ServiceInfo;

import java.io.IOException;

public abstract class BaseApi {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public enum RequestType {
        GET, POST, PUT, DELETE
    }


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
     * @param parameters json formatted string parameters, not used for GET or DELETE requests
     * @param requestType
     * @return
     * @throws IOException
     */
    public abstract Response doRequest(String relativeUrl, String parameters, RequestType requestType) throws IOException;
}
