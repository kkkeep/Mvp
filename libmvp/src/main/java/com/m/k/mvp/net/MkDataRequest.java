package com.m.k.mvp.net;

import android.os.Message;

import java.lang.reflect.Type;
import java.util.HashMap;

public abstract class MkDataRequest {


    private RequestType requestType; // 第一次请求，刷新，加载更多

    private Object args; // 任意类型的参数

    private String url; // 请求 url

    private Type dataType; // 网络请求回来需要的java bean 对象的类型。

    private HashMap<String,String> params;   // 请求需要的参数
    private HashMap<String,String> headers ; // 请求头

    private boolean isEnableCancel; // 网络请求是否支持取消


    public MkDataRequest() {
        requestType = RequestType.FIRST;

        params =  MkDataService.getNetConfig().getCommonParams();
        headers = MkDataService.getNetConfig().getCommonHeaders();
    }

    public MkDataRequest(String url) {
        this();
        this.url = url;

        Message message;

    }





    public <T extends MkDataRequest> T setRequestType(RequestType requestType){
        this.requestType = requestType;
        return (T) this;
    }

    public MkDataRequest putParams(String key, String value){

        if(params == null){
            params = new HashMap<>();
        }

        params.put(key,value);

        return this;
    }

    public MkDataRequest putAllParams(HashMap<String,String> all){

        if(all == null){
            return this;
        }

        if(params == null){
            params = new HashMap<>();
        }


        params.putAll(all);

        return this;
    }

    public MkDataRequest putHeader(String key, String value){

        if(headers == null){
            headers = new HashMap<>();
        }

        headers.put(key,value);

        return this;
    }


    public MkDataRequest putAllHeader(HashMap<String,String> all){

        if(all == null){
            return this;
        }

        if(headers == null){
            headers = new HashMap<>();
        }

        headers.putAll(all);

        return this;
    }
    public abstract Method getMethod();

    public RequestType getRequestType() {
        return requestType;
    }

    public String getUrl() {
        return url;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    private Type getDataType() {
        return dataType;
    }

    private void setDataType(Type dataType) {
        this.dataType = dataType;
    }

    public boolean isEnableCancel() {
        return isEnableCancel;
    }

    public void setEnableCancel(boolean enableCancel) {
        isEnableCancel = enableCancel;
    }


    public boolean isFirstLoad() {
        return requestType == RequestType.FIRST;
    }

    public boolean isRefresh(){
        return  requestType == RequestType.REFRESH;
    }

    public boolean isLoadMore(){
        return  requestType == RequestType.LOAD_MORE;
    }


    public Object getArgs() {
        return args;
    }

    public void setArgs(Object args) {
        this.args = args;
    }

    public static enum Method{
        GET,POST,UPLOAD_FILE
    }

    public static enum RequestType{
        FIRST, // 第一次请求
        REFRESH, // 刷新
        LOAD_MORE // 加载更多
    }
}
