package com.m.k.mvp.base.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.m.k.mvp.base.presenter.MkBaseSmartPresenter3;
import com.m.k.mvp.base.presenter.MkIBaseSmartPresenter3;
import com.m.k.mvp.net.MkDataRequest;

import java.lang.reflect.ParameterizedType;

public abstract class MkBaseSmartFragment3<D1,D2,D3> extends MkBaseMvpFragment<MkIBaseSmartPresenter3> implements MkIBaseSmartView3<D1,D2,D3, MkIBaseSmartPresenter3> {


    @Override
    protected void doOnPreCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.doOnPreCreateView(inflater, container, savedInstanceState);

        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        mPresenter.setDataType(parameterizedType.getActualTypeArguments()[0]);

        mPresenter.setDataType2(parameterizedType.getActualTypeArguments()[1]);

        mPresenter.setDataType3(parameterizedType.getActualTypeArguments()[2]);

    }

  

    protected void doRequestData1(MkDataRequest dataRequest){
        mPresenter.doRequest(dataRequest);
    }

    protected void doRequestData2(MkDataRequest dataRequest){
        mPresenter.doRequest2(dataRequest);
    }

    protected void doRequestData3(MkDataRequest dataRequest){
        mPresenter.doRequest3(dataRequest);
    }



    @Override
    public MkIBaseSmartPresenter3 createPresenter() {
        return new MkBaseSmartPresenter3();
    }


}
