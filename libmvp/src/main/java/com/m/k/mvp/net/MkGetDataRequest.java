package com.m.k.mvp.net;

import java.util.HashMap;


public class MkGetDataRequest extends MkDataRequest {

    public MkGetDataRequest(String url, HashMap params) {
        super(url);
        putAllParams(params);
    }

    public MkGetDataRequest(String url) {
        super(url);
    }

    @Override
    public Method getMethod() {
        return Method.GET;
    }
}
