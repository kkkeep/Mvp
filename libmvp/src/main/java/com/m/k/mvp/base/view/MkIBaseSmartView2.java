package com.m.k.mvp.base.view;

import com.m.k.mvp.base.presenter.MkIBaseSmartPresenter2;
import com.m.k.mvp.net.MkDataResponse;

public interface MkIBaseSmartView2<D,D2,P extends MkIBaseSmartPresenter2>  extends MkIBaseSmartView<D,P> {

    void onResult2(MkDataResponse<D2> dataResponse);

}
