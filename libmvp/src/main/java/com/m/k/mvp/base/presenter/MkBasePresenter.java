package com.m.k.mvp.base.presenter;

import com.m.k.mvp.base.view.MkIBaseView;
import com.trello.rxlifecycle2.LifecycleProvider;

import io.reactivex.disposables.CompositeDisposable;

public  class MkBasePresenter<V extends MkIBaseView> implements MkIBasePresenter<V> {

    protected  V mView;

    protected CompositeDisposable mDisposable;

    @Override
    public void attachView(V view) {
        mView =  view;


    }

    @Override
     public void detachView() {
        mView = null;
        mDisposable = null;
    }



    @Override
    public LifecycleProvider getProvider() {
        return (LifecycleProvider) mView;
    }


    @Override
    public boolean cancelRequest() {
        if(mDisposable != null && mDisposable.size() > 0){
            mDisposable.clear();
            return true;
        }

        return false;

    }
}
