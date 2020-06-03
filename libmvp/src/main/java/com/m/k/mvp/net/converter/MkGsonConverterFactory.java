package com.m.k.mvp.net.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/*
 * created by Cherry on 2020-01-10
 **/
public class MkGsonConverterFactory extends Converter.Factory {


    public static MkGsonConverterFactory create() {
        return create(new Gson());
    }


    public static MkGsonConverterFactory create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new MkGsonConverterFactory(gson);
    }

    protected final Gson gson;

    protected MkGsonConverterFactory(Gson gson) {
        this.gson = gson;
    }



    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new MkGsonResponseBodyConverter<>(gson, adapter,type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new MkGsonRequestBodyConverter<>(gson, adapter);
    }



}
