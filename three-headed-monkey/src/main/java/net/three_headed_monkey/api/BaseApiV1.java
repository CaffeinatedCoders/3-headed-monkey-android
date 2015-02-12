package net.three_headed_monkey.api;

import android.util.Log;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import net.three_headed_monkey.communication.utils.X509TrustSingleManager;
import net.three_headed_monkey.data.ServiceInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        OkHttpClient client = getHttpClient();
        String url = serviceInfo.getDeviceApiV1Url() + relativeUrl;
        Request.Builder builder = buildRequest(url, requestType, RequestBody.create(JSON, parameters));
        Request request = builder.build();
        return client.newCall(request).execute();
    }

    @Override
    public Response doRequest(String relativeUrl, Map<String, String> parameters, RequestType requestType, String file_parameter_name, String file_name, MediaType file_content_type, byte[] file_content) throws IOException {
//        File file = File.createTempFile("image_", "jpg", new File("/sdcard"));
//        FileOutputStream os = new FileOutputStream(file);
//        os.write(file_content);
//        os.close();

        OkHttpClient client = getHttpClient();
        String url = serviceInfo.getDeviceApiV1Url() + relativeUrl;
        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);

        for (Map.Entry<String, String> entry : parameters.entrySet())
        {
            multipartBuilder.addPart(
                    Headers.of("Content-Disposition", "form-data; name=\""+ entry.getKey() + "\""),
                    RequestBody.create(null, entry.getValue())
            );
        }

        multipartBuilder.addFormDataPart(
                file_parameter_name,
                file_name,
                RequestBody.create(file_content_type, file_content)
        );
        RequestBody requestBody = multipartBuilder.build();
        Request.Builder requestBuilder = buildRequest(url, requestType, requestBody);
        Request request = requestBuilder.build();
        return client.newCall(request).execute();
    }

    protected OkHttpClient getHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.setSslSocketFactory(X509TrustSingleManager.getTrustSingleFactory(serviceInfo.certHash));
        client.setConnectTimeout(7, TimeUnit.SECONDS);
        return client;
    }

    protected Request.Builder buildRequest(String url, RequestType requestType, RequestBody requestBody) {
        Request.Builder builder = new Request.Builder().url(url);
        switch (requestType) {
            case GET: builder.get(); break;
            case DELETE: builder.delete(); break;
            case POST: builder.post(requestBody); break;
            case PUT: builder.put(requestBody); break;
        }
        return builder;
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
