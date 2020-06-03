package com.m.k.mvp.exception;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.IOException;

/*
 * created by Cherry on 2020-01-10
 **/
public class MkResultException extends IOException {

    public static final int SERVER_ERROR = 0X100;
    String msg;
    public Throwable source;
    public int code;


    public static MkResultException newServerException(){

        return new MkResultException(SERVER_ERROR);
    }

    public MkResultException(int code ) {
        super("");
        this.code = code;
    }




    public MkResultException(String message) {
        super(message);
        msg = message;
    }

    public MkResultException(Throwable cause) {
        super(cause);
        source = cause;
    }


    @Nullable
    @Override
    public String getMessage() {

        if(!TextUtils.isEmpty(msg)){ // 我们特殊制定了异常消息
            return msg;
        }

        if(source != null && source instanceof IOException){ // 一般指网络异常
            return "网络异常";
        }

        if(code == SERVER_ERROR){ // 服务器错误导致
            return "服务器小哥遇到了点问题，请稍后再试";
        }

        // 自己代码发生了bug
        return "程序出错";

    }
}
