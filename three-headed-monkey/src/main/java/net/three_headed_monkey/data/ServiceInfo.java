package net.three_headed_monkey.data;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

public class ServiceInfo {
    public static final String TAG = "ServiceInfo";

    public String baseUrl;
    public int baseUrlPort;
    public String deviceKey;
    public String certHash;
    public String gcm_sender_id;

    public long lastLocationHistoryUpdate = 0;

    public ServiceInfo() {}

    public ServiceInfo(String baseUrl, int port, String deviceKey) {
        this.baseUrl = baseUrl;
        this.baseUrlPort = port;
        this.deviceKey = deviceKey;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!o.getClass().equals(ServiceInfo.class))
            return false;
        ServiceInfo serviceInfo = (ServiceInfo) o;
        return serviceInfo.baseUrl.equals(baseUrl)
                && serviceInfo.baseUrlPort == baseUrlPort
                && serviceInfo.deviceKey.equals(deviceKey);
    }

    public URL getBaseUrl() throws MalformedURLException {
        return new URL("https", baseUrl, baseUrlPort, "/");
    }

    public URL getDeviceApiV1Url() throws MalformedURLException {
        return new URL("https", baseUrl, baseUrlPort, "/api/v1/"+deviceKey);
    }

    public static ServiceInfo createFromJson(String json) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> map = new Gson().fromJson(json, type);
        if(map == null)
            return null;

        if( !map.containsKey("base_url") || !map.containsKey("device_key") )
            return null;

        ServiceInfo info = new ServiceInfo();
        info.baseUrl = map.get("base_url");
        info.deviceKey = map.get("device_key");
        try {
            if (map.containsKey("base_url_port")) {
                info.baseUrlPort = Integer.parseInt(map.get("base_url_port"));
            } else {
                info.baseUrlPort = 443;
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error parsing port", ex);
            return null;
        }
        return info;
    }

}
