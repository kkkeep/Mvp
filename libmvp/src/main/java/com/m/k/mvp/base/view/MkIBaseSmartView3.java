package com.m.k.mvp.base.view;

import com.m.k.mvp.base.presenter.MkIBaseSmartPresenter2;
import com.m.k.mvp.net.MkDataResponse;

public interface MkIBaseSmartView3<D1,D2,D3,P extends MkIBaseSmartPresenter2>  extends MkIBaseSmartView2<D1,D2,P> {

    void onResult3(MkDataResponse<D3> dataResponse);




}
