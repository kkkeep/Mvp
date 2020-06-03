package com.m.k.mvp.net;

import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface MkApiService {


    /**
     * //4. 这是 Retrofit 的 api service 接口，如果MpvApiService 满足不了你的需求，那么需要调用传入自己的 api service,否则不需要调用，
     * 目前 MpvApiService 支持
     * 1Get ，
     *
     * 2.@FormUrlEncoded  的 Post
     *
     * 3. 带参数的文件上传
     *
     *
     */


    @GET()
    Observable<String> get(@Url String url, @QueryMap HashMap<String,String> params);

    @GET()
    Observable<String> get(@Url String url,@HeaderMap HashMap<String,String> headers, @QueryMap HashMap<String,String> params);

    @POST()
    @FormUrlEncoded
    Observable<String> post(@Url String url,@FieldMap HashMap<String,String> params);

    @POST()
    @FormUrlEncoded
    Observable<String> post(@Url String url, @HeaderMap HashMap<String,String> headers,@FieldMap HashMap<String,String> params);



    //上传文件
    @POST
    @Multipart
    Observable<String> uploadFile(@Url String url,@HeaderMap HashMap<String,String> headers,@PartMap HashMap<String, RequestBody> params, @Part MultipartBody.Part file);


    @Streaming // 防止大文件内存溢出
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);
}
