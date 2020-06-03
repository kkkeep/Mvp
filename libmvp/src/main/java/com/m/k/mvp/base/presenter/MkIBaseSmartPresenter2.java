package com.m.k.mvp.base.presenter;

import com.m.k.mvp.base.view.MkIBaseSmartView2;
import com.m.k.mvp.net.MkDataRequest;

import java.lang.reflect.Type;

public interface MkIBaseSmartPresenter2<V extends MkIBaseSmartView2> extends MkIBaseSmartPresenter<V> {

     void setDataType2(Type dataType);
     void doRequest2(MkDataRequest dataRequest);
}
