package com.m.k.mvp.base.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.m.k.mvp.base.presenter.MkBaseSmartPresenter;
import com.m.k.mvp.base.presenter.MkIBaseSmartPresenter;
import com.m.k.mvp.net.MkDataRequest;

import java.lang.reflect.ParameterizedType;

public abstract class MkBaseSmartActivity<D> extends MkBaseMvpActivity<MkIBaseSmartPresenter> implements MkIBaseSmartView<D, MkIBaseSmartPresenter> {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        mPresenter.setDataType(parameterizedType.getActualTypeArguments()[0]);

    }


    protected void doRequest(MkDataRequest dataRequest){
        mPresenter.doRequest(dataRequest);
    }


    @Override
    public MkIBaseSmartPresenter createPresenter() {
        return new MkBaseSmartPresenter();
    }
}
