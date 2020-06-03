package com.m.k.mvp.base.presenter;

import com.m.k.mvp.base.view.MkIBaseSmartView;
import com.m.k.mvp.net.MkDataRequest;

import java.lang.reflect.Type;

public interface MkIBaseSmartPresenter<V extends MkIBaseSmartView> extends MkIBasePresenter<V> {

     void setDataType(Type dataType);

     void doRequest(MkDataRequest dataRequest);
}
