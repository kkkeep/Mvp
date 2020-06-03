package com.m.k.mvp.net;

import com.m.k.mvp.BuildConfig;
import com.m.k.mvp.net.converter.MkGsonConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class MkDataService {

    private static final long TIME_OUT = 2000;

    private static MkApiService mvpApiService;

    private static Object appApiService;

    private static Class appApisServiceClass;

    private volatile static Retrofit mRetrofit;

    private  static MkNetConfig mNetConfig;




    public synchronized static void init(MkNetConfig netConfig) {

        if (mRetrofit == null) {

            mNetConfig = netConfig;


            if (netConfig.getAppApiService() != null) {
                appApisServiceClass = netConfig.getAppApiService();
            }

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            /**
             * 注意，如果有大文件下载，或者 response 里面的body 很大，要么不加HttpLoggingInterceptor 拦截器
             * 如果非要加，日志级别不能是 BODY,否则容易内存溢出。
             */
            if (BuildConfig.DEBUG) {
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                logging.setLevel(HttpLoggingInterceptor.Level.NONE);
            }


            //  1. 一个拦截器的list,一个 json 转换器，一个 baseUrl

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(logging);

            if (netConfig.getAppIntercepters() != null && netConfig.getAppIntercepters().size() > 0) {
                for (Interceptor interceptor : netConfig.getAppIntercepters()) {
                    builder.addInterceptor(interceptor);
                }
            }




            builder.connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                    .build();

            mRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(netConfig.getFactory() == null ? MkGsonConverterFactory.create() : netConfig.getFactory()) // 帮我们把json 窜转为 entity 对象
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 结合 rxjava 使用
                    .baseUrl(netConfig.getBaseUrl())
                    .build();

        }


    }


    public synchronized static MkApiService getMvpApiService() {

        if (mvpApiService == null) {
            mvpApiService =  mRetrofit.create(MkApiService.class);
        }
        return mvpApiService;
    }


    public synchronized static Object getAppApiService() {

        if (appApiService == null && appApisServiceClass != null) {
            appApiService = mRetrofit.create(appApisServiceClass);
        }

        return appApiService;
    }


    public static MkNetConfig getNetConfig() {
        return mNetConfig;
    }
}
