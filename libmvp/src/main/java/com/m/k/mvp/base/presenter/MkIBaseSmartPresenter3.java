package com.m.k.mvp.base.presenter;

import com.m.k.mvp.base.view.MkIBaseSmartView3;
import com.m.k.mvp.net.MkDataRequest;

import java.lang.reflect.Type;

public interface MkIBaseSmartPresenter3<V extends MkIBaseSmartView3> extends MkIBaseSmartPresenter2<V> {

     void setDataType3(Type dataType);
     void doRequest3(MkDataRequest dataRequest);
}
