package com.m.k.mvp.base.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.m.k.mvp.base.presenter.MkBaseSmartPresenter2;
import com.m.k.mvp.base.presenter.MkIBaseSmartPresenter2;
import com.m.k.mvp.net.MkDataRequest;

import java.lang.reflect.ParameterizedType;

public abstract class MkBaseSmartFragment2<D1,D2> extends MkBaseMvpFragment<MkIBaseSmartPresenter2> implements MkIBaseSmartView2<D1,D2, MkIBaseSmartPresenter2> {


    @Override
    protected void doOnPreCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.doOnPreCreateView(inflater, container, savedInstanceState);

        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        mPresenter.setDataType(parameterizedType.getActualTypeArguments()[0]);

        mPresenter.setDataType2(parameterizedType.getActualTypeArguments()[1]);

    }

  

    protected void doRequestData1(MkDataRequest dataRequest){
        mPresenter.doRequest(dataRequest);
    }

    protected void doRequestData2(MkDataRequest dataRequest){
        mPresenter.doRequest2(dataRequest);
    }


    @Override
    public MkIBaseSmartPresenter2 createPresenter() {
        return new MkBaseSmartPresenter2();
    }


}
