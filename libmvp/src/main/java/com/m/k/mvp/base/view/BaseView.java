package com.m.k.mvp.base.view;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import com.m.k.mvp.widget.MkLoadingView;

import org.jetbrains.annotations.NotNull;

 interface BaseView {



    default <V extends View> V findAndSetClick(@IdRes int id, View.OnClickListener clickListener){

        V v = findViewById(id);

        v.setOnClickListener(clickListener);

        return v;
    }


    MkLoadingView getLoadingView();


    void setLoadingView(MkLoadingView view);

    default void showPopLoadingView() {
        showPopLoadingView(getDefaultContainer());
    }

    default void showPopLoadingView(@IdRes int containerId) {
        showPopLoadingView(findViewById(containerId));
    }


    default void showPopLoadingView(@NotNull ViewGroup viewGroup) {
        showLoadingView(MkLoadingView.MODE_TRANSPARENT_BACKGROUND, viewGroup);
    }


    default void showFullLoadingView() {
        showFullLoadingView(getDefaultContainer());
    }

    default void showFullLoadingView(@IdRes int containerId) {
        showFullLoadingView(findViewById(containerId));
    }


    default void showFullLoadingView(@NotNull ViewGroup viewGroup) {
        showLoadingView(MkLoadingView.MODE_WHITE_BACKGROUND, viewGroup);
    }


    default void closeLoadingView() {
        MkLoadingView loadingView = getLoadingView();
        if (loadingView != null) {
            loadingView.close();
        }

    }


    default void onError() {
        MkLoadingView loadingView = getLoadingView();
        if (loadingView != null) {
            loadingView.onError();
        }
    }

    default void onError(MkLoadingView.OnRetryListener listener) {

        MkLoadingView loadingView = getLoadingView();
        if (loadingView != null) {
            loadingView.onError(listener);
        }
    }

    default void onError(String error, MkLoadingView.OnRetryListener listener) {
        MkLoadingView loadingView = getLoadingView();
        if (loadingView != null) {
            loadingView.onError(error, listener);
        }
    }

    default void onError(String error, @DrawableRes int errorIcon, MkLoadingView.OnRetryListener listener) {
        MkLoadingView loadingView = getLoadingView();
        if (loadingView != null) {
            loadingView.onError(error, errorIcon, listener);
        }
    }

    <T extends View> T findViewById(@IdRes int id);


    int getLayoutId();

    default ViewGroup getDefaultContainer() {
        if (this instanceof Activity) {
            return ((Activity) this).findViewById(android.R.id.content);
        } else {
            return (ViewGroup) ((MkBaseFragment) this).getView();
        }
    }


    default void showLoadingView(int mode, ViewGroup group) {
        MkLoadingView loadingView = MkLoadingView.inject(group);
        setLoadingView(loadingView);
        loadingView.showMode(mode);
    }

}
