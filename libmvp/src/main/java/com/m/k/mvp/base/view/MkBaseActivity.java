package com.m.k.mvp.base.view;

import android.widget.Toast;

import androidx.annotation.StringRes;

import com.m.k.mvp.widget.MkLoadingView;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

public abstract class MkBaseActivity extends RxAppCompatActivity implements BaseView {
    private MkLoadingView loadingView;

    @Override
    public MkLoadingView getLoadingView() {
        return loadingView;
    }

    @Override
    public void setLoadingView(MkLoadingView view) {
        this.loadingView = view;

    }





    @Override
    public int getLayoutId() {
        return 0;
    }

    protected void showToast(@StringRes int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    protected void showToast( String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();

    }


}
