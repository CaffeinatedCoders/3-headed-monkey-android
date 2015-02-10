package net.three_headed_monkey.api;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;

import net.three_headed_monkey.data.ServiceInfo;

import java.util.HashMap;

public class FileUploadApi extends BaseApiV1 {
    public FileUploadApi(ServiceInfo serviceInfo) {
        super(serviceInfo);
    }

    public void uploadFile(String file_name, MediaType content_type, byte[] file_content) throws Exception {
        String url = "/uploaded_files";
        Response response = doRequest(url, new HashMap<String, String>(), RequestType.POST, "uploaded_file[file]", file_name, content_type, file_content);
        if(!response.isSuccessful()) {
            throw new Exception("Service " + serviceInfo.getBaseUrl() + " returned error: " + response.code());
        }
    }
}
