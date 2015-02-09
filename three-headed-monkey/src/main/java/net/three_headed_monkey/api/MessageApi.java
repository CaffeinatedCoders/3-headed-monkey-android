package net.three_headed_monkey.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Response;

import net.three_headed_monkey.data.ServiceInfo;

import java.io.IOException;

public class MessageApi extends BaseApiV1 {
    public MessageApi(ServiceInfo serviceInfo) {
        super(serviceInfo);
    }

    public void postMessage(String text) throws Exception {
        Gson gson = new Gson();
        JsonObject jroot = new JsonObject();
        JsonObject jText = new JsonObject();
        jText.addProperty("text", text);
        jroot.add("message", jText);

        String params = gson.toJson(jroot);

        Response response = doRequest("/messages", params, RequestType.POST);
        if(!response.isSuccessful()) {
            throw new Exception("Service " + serviceInfo.getBaseUrl() + " returned error: " + response.code());
        }
    }

}
