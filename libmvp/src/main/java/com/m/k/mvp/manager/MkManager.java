package com.m.k.mvp.manager;

import android.content.Context;

import com.m.k.mvp.net.MkIUser;
import com.m.k.mvp.net.MkDataService;
import com.m.k.mvp.net.MkNetConfig;

public class MkManager {


    public static Context mContext;


    public static void init(Context context, MkNetConfig netConfig, Class<? extends MkIUser> userClass){

        mContext = context.getApplicationContext();


        MkDataService.init(netConfig);

        MkUserManager.init(userClass);

    }


}
