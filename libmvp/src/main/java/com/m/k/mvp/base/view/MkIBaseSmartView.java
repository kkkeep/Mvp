package com.m.k.mvp.base.view;

import com.m.k.mvp.base.presenter.MkIBaseSmartPresenter;
import com.m.k.mvp.net.MkDataResponse;

public interface MkIBaseSmartView<D,P extends MkIBaseSmartPresenter>  extends MkIBaseView<P> {

    void onResult(MkDataResponse<D> dataResponse);

}
