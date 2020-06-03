package com.m.k.mvp.net;

public class MkDataResponse<D> {

    private ResponseType responseType; //数据从哪儿来的，服务器，内存 或者sdcard

    private MkDataRequest.RequestType requestType;//是第一次请求回来的，还是刷新，还是加载更多。

    private String msg; // 错误消息

    private D data; // 请求回来的数据实体对象

    public MkDataResponse(MkDataRequest.RequestType requestType, D data) {
        this.responseType = ResponseType.SERVER;
        this.requestType = requestType;
        this.data = data;
    }

    public MkDataResponse(ResponseType responseType, MkDataRequest.RequestType requestType, D data) {
        this.responseType = responseType;
        this.requestType = requestType;
        this.data = data;
    }
    public MkDataResponse(MkDataRequest.RequestType requestType, String msg) {
        this.responseType =  ResponseType.SERVER;
        this.requestType = requestType;
        this.msg = msg;
    }

    public MkDataResponse(ResponseType responseType, MkDataRequest.RequestType requestType, String msg) {
        this.responseType = responseType;
        this.requestType = requestType;
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }

    // 数据是否加载成功
    public boolean isOk(){
        return data != null;
    }
    public ResponseType getResponseType() {
        return responseType;
    }

    public D getData() {
        return data;
    }


    public MkDataRequest.RequestType getRequestType() {
        return requestType;
    }

    public static enum ResponseType{
        SERVER,MEMORY,DISK
    }

}
