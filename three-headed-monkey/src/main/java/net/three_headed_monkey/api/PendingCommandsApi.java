package net.three_headed_monkey.api;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Response;

import net.three_headed_monkey.data.PendingCommandFromApi;
import net.three_headed_monkey.data.ServiceInfo;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class PendingCommandsApi extends BaseApiV1 {
    private static final String COMMANDS_BASE_URL = "/pendingcommands";

    public PendingCommandsApi(ServiceInfo serviceInfo) {
        super(serviceInfo);
    }

    public List<PendingCommandFromApi> getPendingCommands() throws IOException {
        Response response = doGetRequest(COMMANDS_BASE_URL);
        if(!response.isSuccessful())
            return null;

        Gson gson = new Gson();
        JsonObject root = gson.fromJson(response.body().string(), JsonObject.class);
        JsonElement e = root.get("pendingCommands");
        if(e == null || ! e.isJsonArray())
            return null;

        Type collectionType = new TypeToken<List<PendingCommandFromApi>>(){}.getType();
        List<PendingCommandFromApi> pendingCommandFromApis = gson.fromJson(e, collectionType);
        for(PendingCommandFromApi command : pendingCommandFromApis) {
            command.serviceInfo = serviceInfo;
        }

        return pendingCommandFromApis;
    }

    public void setCommandFinished(int id) throws Exception {
        Response response = doRequest(COMMANDS_BASE_URL + "/" + id, "", RequestType.PUT);
        if(!response.isSuccessful()) {
            throw new Exception("Service returned error: " + response.code());
        }
    }

}
