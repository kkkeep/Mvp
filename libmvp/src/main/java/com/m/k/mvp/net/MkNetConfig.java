package com.m.k.mvp.net;

import java.util.HashMap;
import java.util.List;

import okhttp3.Interceptor;
import retrofit2.Converter;

public class MkNetConfig {

    private List<Interceptor> appIntercepters;

    private Converter.Factory factory;

    private String baseUrl;

    private Class appApiService;

    private Class<? extends MkINetEntity> responseEntityClass;




    public MkNetConfig(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<Interceptor> getAppIntercepters() {
        return appIntercepters;
    }

    public void setAppIntercepters(List<Interceptor> appIntercepters) {
        this.appIntercepters = appIntercepters;
    }

    public Converter.Factory getFactory() {
        return factory;
    }

    public void setFactory(Converter.Factory factory) {
        this.factory = factory;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Class getAppApiService() {
        return appApiService;
    }

    public void setAppApiService(Class appApiService) {
        this.appApiService = appApiService;
    }


    public Class<? extends MkINetEntity> getResponseEntityClass() {
        return responseEntityClass;
    }

    public void setResponseEntityClass(Class<? extends MkINetEntity> responseEntityClass) {
        this.responseEntityClass = responseEntityClass;
    }

    public HashMap<String,String> getCommonParams(){
        return new HashMap<>();
    }


    public HashMap<String,String> getCommonHeaders(){
        return new HashMap<>();
    }
    public  String getCodeFieldName(){
        return "code";
    }
    public  int getSuccessCode(){
        return 1; // 1 表示成功，
    }

    public String getDataFieldName(){
        return "data";
    }



}
