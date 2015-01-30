package net.three_headed_monkey.api;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import net.three_headed_monkey.communication.utils.X509TrustSingleManager;
import net.three_headed_monkey.data.ServiceInfo;

import java.io.IOException;

public class BaseApiV1 extends BaseApi {
    public static final String TAG="BaseApiV1";

    public BaseApiV1(ServiceInfo serviceInfo) {
        super(serviceInfo);
    }

    @Override
    public Response doGetRequest(String relativeUrl) throws IOException {
        return doRequest(relativeUrl, "", RequestType.GET);
    }

    @Override
    public Response doRequest(String relativeUrl, String parameters, RequestType requestType) throws IOException {
        OkHttpClient client = new OkHttpClient();
        client.setSslSocketFactory(X509TrustSingleManager.getTrustSingleFactory(serviceInfo.certHash));
        String url = serviceInfo.getDeviceApiV1Url() + relativeUrl;
        Request.Builder builder = new Request.Builder().url(url);
        switch (requestType) {
            case GET: builder.get(); break;
            case DELETE: builder.delete(); break;
            case POST: builder.post(RequestBody.create(JSON, parameters)); break;
            case PUT: builder.put(RequestBody.create(JSON, parameters)); break;
        }
        Request request = builder.build();
        return client.newCall(request).execute();
    }

    public boolean checkApiAvailable() {
        try {
            Response response = doGetRequest("");
            if(response.isSuccessful()) {
                return true;
            } else {
                Log.e(TAG, "Api currently not available");
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "Api currently not available", e);
            return false;
        }
    }
}
