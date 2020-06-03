package com.m.k.mvp.net;

import java.util.HashMap;

public class MkPostDataRequest extends MkDataRequest {


   public MkPostDataRequest(String url, HashMap params){
        super(url);
        putAllParams(params);
    }



    public MkPostDataRequest(String url) {

        super(url);
    }


    public MkPostDataRequest() {

    }

    @Override
    public Method getMethod() {
        return Method.POST;
    }
}
