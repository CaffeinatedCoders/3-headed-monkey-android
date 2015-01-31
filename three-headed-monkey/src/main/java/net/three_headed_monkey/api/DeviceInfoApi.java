package net.three_headed_monkey.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Response;

import net.three_headed_monkey.data.ServiceInfo;
import net.three_headed_monkey.utils.GcmUtils;

import java.io.IOException;

public class DeviceInfoApi extends BaseApiV1 {
    private Context context;

    public DeviceInfoApi(ServiceInfo serviceInfo, Context context) {
        super(serviceInfo);
        this.context = context;
    }

    public void updateLocalServiceInfo() throws IOException {
        Response response = doGetRequest("/");
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(response.body().string(), JsonObject.class);
        JsonObject gcm = (JsonObject) root.get("gcm");
        if(gcm == null)
            return;
        JsonElement sender_id = gcm.get("sender_id");
        if(sender_id == null)
            return;
        serviceInfo.gcm_sender_id = sender_id.getAsString();
    }

    public void updateRemoteInformation() throws Exception {
        String gcm_regid = GcmUtils.getRegistrationId(context);
        String params = "{\"device\":{" +
                "\"gcm_regid\":\""+ gcm_regid + "\"" +
                "}}";
        Response response = doRequest("/", params, RequestType.PUT);
        if(!response.isSuccessful()) {
            throw new Exception("Service returned error: " + response.code());
        }
    }

}
