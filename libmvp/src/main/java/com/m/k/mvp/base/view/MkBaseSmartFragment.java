package com.m.k.mvp.base.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.m.k.mvp.base.presenter.MkBaseSmartPresenter;
import com.m.k.mvp.base.presenter.MkIBaseSmartPresenter;
import com.m.k.mvp.net.MkDataRequest;

import java.lang.reflect.ParameterizedType;

public abstract class MkBaseSmartFragment<D> extends MkBaseMvpFragment<MkIBaseSmartPresenter> implements MkIBaseSmartView<D, MkIBaseSmartPresenter> {


    @Override
    protected void doOnPreCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.doOnPreCreateView(inflater, container, savedInstanceState);

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
