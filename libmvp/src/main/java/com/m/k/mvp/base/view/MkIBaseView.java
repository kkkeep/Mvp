package com.m.k.mvp.base.view;

import com.m.k.mvp.base.presenter.MkIBasePresenter;

public interface MkIBaseView<P extends MkIBasePresenter> {
    P createPresenter();

}
