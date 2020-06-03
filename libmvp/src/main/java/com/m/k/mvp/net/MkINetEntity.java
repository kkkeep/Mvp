package com.m.k.mvp.net;

public interface MkINetEntity<T> {

    boolean isOk();

    T getData();

    String getErrorMessage();


}
