package com.m.k.mvp.base.callback;

import com.m.k.mvp.exception.MkResultException;

import io.reactivex.disposables.Disposable;

public interface MkIBaseCallBack<D> {

    void onServerSuccess(D data);
    default void onMemorySuccess(D d){

    }
    default void onSdcardSuccess(D d){

    }
    void onFail(MkResultException exception);


    default void onStart(Disposable disposable){

    }



}
