package com.m.k.mvp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.m.k.mvp.manager.MkManager;

public class SpUtils {

    private static final String GLOBAL_SP_NAME = "mvp_config";



    private static SharedPreferences getGlobalSp(){
        SharedPreferences sharedPreferences = MkManager.mContext.getSharedPreferences(GLOBAL_SP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static  void save (String key,Object value){


        SharedPreferences.Editor editor = getGlobalSp().edit();

        editor.putString(key,value.toString());

        editor.commit();
       // editor.apply();

    }

    public static boolean getBoolean(String key){

        String value =  getGlobalSp().getString(key,"false");

        if(value.equals("true")){
            return true;
        }else{
            return false;
        }
    }
}
