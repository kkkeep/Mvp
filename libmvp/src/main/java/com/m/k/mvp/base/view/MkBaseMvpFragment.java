package com.m.k.mvp.base.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.m.k.mvp.base.presenter.MkIBasePresenter;

public abstract class MkBaseMvpFragment<P extends MkIBasePresenter> extends MkBaseFragment implements MkIBaseView<P> {

    protected P mPresenter;



    @Override
    protected void doOnPreCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.doOnPreCreateView(inflater, container, savedInstanceState);


        mPresenter = createPresenter();

        if(mPresenter == null){
            throw new NullPointerException(" createPresenter 必须返回一个有效的 Presenter ");
        }

        mPresenter.attachView(this);


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadData();
    }


    protected  void loadData(){

    }



    public void cancelRequest() {
        if(mPresenter != null){
           if( mPresenter.cancelRequest()){
               closeLoadingView();
           }
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mPresenter != null){
            mPresenter.detachView();
        }
    }



}
