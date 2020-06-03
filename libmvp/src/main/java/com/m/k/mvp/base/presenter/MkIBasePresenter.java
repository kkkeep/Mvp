package com.m.k.mvp.base.presenter;

import com.m.k.mvp.base.view.MkIBaseView;
import com.trello.rxlifecycle2.LifecycleProvider;

public interface MkIBasePresenter<V extends MkIBaseView> {

    void attachView(V view);

    void detachView();


    LifecycleProvider getProvider();


    boolean cancelRequest();



}
