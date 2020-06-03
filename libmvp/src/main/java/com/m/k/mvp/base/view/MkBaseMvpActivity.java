package com.m.k.mvp.base.view;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.m.k.mvp.base.presenter.MkIBasePresenter;

public abstract class MkBaseMvpActivity<P extends MkIBasePresenter> extends MkBaseActivity implements MkIBaseView<P> {

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = createPresenter();

        if(mPresenter == null){
            throw new NullPointerException(" createPresenter 必须返回一个有效的 Presenter ");
        }
        mPresenter.attachView(this);

    }

    public void cancelRequest() {
        if(mPresenter != null){
            if(mPresenter.cancelRequest()){
                closeLoadingView();
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mPresenter != null){
                if(mPresenter.cancelRequest()){
                    closeLoadingView();
                    return true;
                }
            }

        }
        return super.onKeyUp(keyCode, event);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mPresenter != null){
            mPresenter.detachView();
        }
    }
}
