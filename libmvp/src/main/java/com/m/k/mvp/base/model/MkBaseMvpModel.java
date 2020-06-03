package com.m.k.mvp.base.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.m.k.mvp.Utils.MkLogger;
import com.m.k.mvp.base.callback.MkIBaseCallBack;
import com.m.k.mvp.exception.MkResultException;
import com.m.k.mvp.net.MkDataRequest;
import com.m.k.mvp.net.MkINetEntity;
import com.m.k.mvp.net.MkDataService;
import com.m.k.mvp.net.MkUpLoadFileDataRequest;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class MkBaseMvpModel {



    public <D> void doRequest(LifecycleProvider provider, final MkDataRequest dataRequest, final MkIBaseCallBack<D> callBack){

        doRequest(provider, dataRequest, new Consumer<D>() {
            @Override
            public void accept(D d) throws Exception {

            }
        }, callBack);
    }



    public <D> void doRequest(LifecycleProvider provider, final MkDataRequest dataRequest, Consumer<D> consumer, final MkIBaseCallBack<D> callBack) {

               Observable<String> observable = null;// 原材料，数据源




        switch (dataRequest.getMethod()) {

            case GET:
                observable = MkDataService.getMvpApiService().get(dataRequest.getUrl(), dataRequest.getHeaders(), dataRequest.getParams());
                break;

            case POST:
                observable = MkDataService.getMvpApiService().post(dataRequest.getUrl(), dataRequest.getHeaders(), dataRequest.getParams());
                break;
            case UPLOAD_FILE:{
                MkUpLoadFileDataRequest fileRequest = (MkUpLoadFileDataRequest) dataRequest;
                observable = MkDataService.getMvpApiService().uploadFile(dataRequest.getUrl(), dataRequest.getHeaders(),fileRequest.getRequestBodyParams(),fileRequest.getFilePart());
                break;
            }
        }

        if (observable == null) {
            if (callBack != null) {
                callBack.onFail(new MkResultException("不支持的请求方法"));
            }

        }


        Type type = getTypeFromRequest(dataRequest);

        if(type == null){
            type = tryToGetDataType(callBack);
        }



        subscribe(provider,observable.map(this.<D>getConverterJsonFunction(type)),consumer,callBack);


    }

    protected <D> void subscribe(LifecycleProvider provider, Observable<? extends MkINetEntity<D>> observable, MkIBaseCallBack<D> callBack){
        subscribe(provider, observable, new Consumer<D>() {
            @Override
            public void accept(D d) throws Exception {

            }
        },callBack);
    }

    protected <D> void subscribe(LifecycleProvider provider, Observable<? extends MkINetEntity<D>> observable, Consumer<D> consumer, MkIBaseCallBack<D> callBack){


        observable.flatMap(getConverterHttpDataFunction())
                .doOnNext(consumer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<D>bindLifecycle(provider))
                .subscribe(new Observer<D>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if(callBack != null ){
                           callBack.onStart(d);
                        }

                    }

                    @Override
                    public void onNext(D s) {

                        if (callBack != null) {
                            callBack.onServerSuccess(s);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callBack != null) {
                            if (e instanceof MkResultException) {
                                callBack.onFail((MkResultException) e);
                            } else {
                                callBack.onFail(new MkResultException(e));
                            }
                        }
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private <D> Type tryToGetDataType(MkIBaseCallBack<D> callBack){

        Type[] types = callBack.getClass().getGenericInterfaces();
        if(types != null && types.length > 0 && types[0] instanceof ParameterizedType){
            ParameterizedType pType = (ParameterizedType) types[0];

            if(pType.getActualTypeArguments()[0].toString().equals("D")){
                return null;
            }

            return pType.getActualTypeArguments()[0];
        }

        return null;



    }

    /**
     * 上游给的 服务器返回的 对象 ，该方法返回发 function 就是判断服务器返回的对象的code 是否成功，
     * 数据是否有值，如果两个都满足，那么去除我们真正想要 数据类型，继续往下游发送。如果 code 表示为成功，或者 code 成功，但是
     * 数据没有值，也意味着失败，那么就往下游发送失败，
     * @param <D>
     * @return
     */

    protected <D> Function<MkINetEntity<D>, ObservableSource<D>> getConverterHttpDataFunction() {


       return  new Function<MkINetEntity<D>, ObservableSource<D>>() {

            @Override
            public ObservableSource<D> apply(MkINetEntity<D> iNetEntity) throws Exception {

                if(MkLogger.isDebug()){
                    MkLogger.d("DataResponse Data = %s", MkLogger.toStringOb(iNetEntity));
                }

                if (iNetEntity.isOk()) { // 如果状态码表示为成功
                    if (iNetEntity.getData() != null) { // 如果数据不为空
                        return Observable.just(iNetEntity.getData());
                    } else {
                        return Observable.error(MkResultException.newServerException());
                    }

                } else {
                    return Observable.error(new MkResultException(iNetEntity.getErrorMessage()));
                }


            }
        };
    }

    /**
     * 把上游发送过来的 json 转成 对象，然后发送给下游
     * @param type
     * @param <D>
     * @return
     */
    protected <D> Function<String, MkINetEntity<D>>  getConverterJsonFunction(final Type type){
       return  new Function<String, MkINetEntity<D>>() {
            @Override
            public MkINetEntity<D> apply(String s) throws Exception {

                Gson gson = new Gson();

                ParameterizedType parameterizedType = new ParameterizedType() {
                    @NonNull
                    @Override
                    public Type[] getActualTypeArguments() {
                        return new Type[]{type};
                    }

                    @NonNull
                    @Override
                    public Type getRawType() {
                        return MkDataService.getNetConfig().getResponseEntityClass(); // HttpResult<ColumnData>.classs
                    }

                    @Nullable
                    @Override
                    public Type getOwnerType() {
                        return null;
                    }
                };

                //  1. HttpResult.class,2 HttpResult 里面的泛型参数的类型
                return gson.fromJson(s, parameterizedType);
            }
        };
    }

    /**
     * 把 rxjava 订阅 和  View 层的生命周期做绑定，在指定生命周期执行时接触绑定，取消网络请求
     * @param provider
     * @param <D>
     * @return
     */

    public <D> LifecycleTransformer<D> bindLifecycle(LifecycleProvider provider){
        if(provider instanceof RxAppCompatActivity){
            return ((RxAppCompatActivity)provider).<D>bindUntilEvent(ActivityEvent.DESTROY);
        }else {
            return ((RxFragment)provider).<D>bindUntilEvent(FragmentEvent.DESTROY_VIEW);
        }

    }






    private Type getTypeFromRequest(MkDataRequest dataRequest){
        try {
            Method method = dataRequest.getClass().getSuperclass().getDeclaredMethod("getDataType");
            method.setAccessible(true);
             Type type = (Type) method.invoke(dataRequest);

             return type;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }





}



