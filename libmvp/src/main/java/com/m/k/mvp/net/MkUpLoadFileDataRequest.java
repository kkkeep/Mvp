package com.m.k.mvp.net;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MkUpLoadFileDataRequest extends MkPostDataRequest {

    private String filePath;

    private String key; // 上传文件，请求参数的 名字。

    public MkUpLoadFileDataRequest(String url, String key, String filePath, HashMap params) {
        super(url, params);
        this.filePath = filePath;
        this.key = key;
    }

    public MkUpLoadFileDataRequest(String url, String key, String filePath) {
        this(url, key,filePath, null);
    }



    @Override
    public Method getMethod() {
        return Method.UPLOAD_FILE;
    }


    public HashMap<String, RequestBody> getRequestBodyParams() {

        if (getParams() != null) {

            HashMap<String, RequestBody> hashMap = new HashMap();

            for (Map.Entry<String, String> entry : getParams().entrySet()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), entry.getValue());
                hashMap.put(entry.getKey(), requestBody);

            }

            return hashMap;
        }

        return null;

    }


    public MultipartBody.Part getFilePart() {

        File file = new File(filePath);
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileData = MultipartBody.Part.createFormData(key, file.getName(), body);

        return fileData;
    }


}
