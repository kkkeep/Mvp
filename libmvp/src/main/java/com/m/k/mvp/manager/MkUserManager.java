package com.m.k.mvp.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.m.k.mvp.Utils.MkDataFileCacheUtils;
import com.m.k.mvp.Utils.SystemFacade;
import com.m.k.mvp.net.MkIUser;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class MkUserManager {

    private static final String ACTION_USER_LOGIN = "com.mr.k.libmvp.manager.user.login";
    private static final String ACTION_USER_LOGOUT = "com.mr.k.libmvp.manager.user.logout";

    private static final String ACTION_USER_CHANGE = "com.mr.k.libmvp.manager.user.change";

    public static volatile MkIUser mUser;

    private static Future<MkIUser> future;

    private static final String USER_CACHE_FILE = "user_cache_file";


    private static ReentrantLock mLock = new ReentrantLock();
    // 登录，注册 时调用

    public static void login(MkIUser user) {

        if (SystemFacade.isMainThread()) {
            throw new IllegalThreadStateException("MvpUserManager.login(IUser user) 必须调用在子线程");
        }

        saveUser(user);

        // 发一个登录广播



        sentBroadcast(ACTION_USER_LOGIN);

    }






    // 退出登录
    public synchronized static void loginOut() {

        mUser = null;
        File file = SystemFacade.getExternalCacheDir(MkManager.mContext, USER_CACHE_FILE);
        if (file.exists()) {
            file.delete();
        }

        sentBroadcast(ACTION_USER_LOGOUT);

        // 发一个登出广播
    }


    // 用户信息发送改变时，调用
    public synchronized static void userChange(MkIUser user){

        if (SystemFacade.isMainThread()) {
            throw new IllegalThreadStateException("MvpUserManager.userChange(IUser user) 必须调用在子线程");
        }
        saveUser(user);

        sentBroadcast(ACTION_USER_CHANGE);

    }



    private static void  saveUser(MkIUser user){

        mUser = user;

        File file = SystemFacade.getExternalCacheDir(MkManager.mContext, USER_CACHE_FILE);

        if (file != null && user != null) {
            MkDataFileCacheUtils.saveEncryptedDataToFile(file, user);
        }
    }


    private static void sentBroadcast(String action){
       LocalBroadcastManager broadcastManager =  LocalBroadcastManager.getInstance(MkManager.mContext);
       broadcastManager.sendBroadcast(new Intent(action));
    }



    public static UserStateChangeListener addUserStateChangeListener(@NonNull  UserStateChangeListener listener){

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ACTION_USER_LOGOUT);
        intentFilter.addAction(ACTION_USER_LOGIN);
        intentFilter.addAction(ACTION_USER_CHANGE);

        LocalBroadcastManager.getInstance(MkManager.mContext).registerReceiver(listener,intentFilter);
        return listener;
    }

    public static void removeUserStateChangeListener(@NonNull  UserStateChangeListener listener){

        LocalBroadcastManager.getInstance(MkManager.mContext).unregisterReceiver(listener);
    }



    public static <U extends MkIUser> U getUserFromSdcard(Class<U> aClass) {
        if (SystemFacade.isMainThread()) {
            throw new IllegalThreadStateException("MvpUserManager.getUserFromSdcard() 必须调用在子线程");
        }


        File file = SystemFacade.getExternalCacheDir(MkManager.mContext, USER_CACHE_FILE);

        if (file != null) {
            return MkDataFileCacheUtils.getencryptedDataFromFile(file, aClass);
        }

        return null;
    }


    static <U extends MkIUser> void init(Class<U> aClass) {

      ExecutorService executorService =   Executors.newSingleThreadExecutor();

      future = executorService.submit(new Callable<MkIUser>() {
          @Override
          public MkIUser call() throws Exception {
              try{
                  mUser = getUserFromSdcard(aClass);
                  return mUser;
              }finally {
                  executorService.shutdown();
              }

          }
      });


    }


    public  static  <T extends MkIUser> T getUser() {

        try{
            if(mUser == null && future != null){
                mUser = future.get();
                future = null;
            }
            return (T) mUser;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
        }
        return null;

    }

    public  static String getToke() {

        try {
            if(mUser == null){
                mUser = getUser();
                if(mUser == null){
                    return null;
                }
            }
            String toke = mUser.getTokenValue();
            if (toke == null) {
                loginOut();
            }

            return toke;
        }finally {

        }

    }


    public synchronized static boolean isLoginIn(){
        return getToke() != null;
    }



    public static class UserStateChangeListener extends BroadcastReceiver {

       public void onLogin(MkIUser user){

        }
       public void onLogout(){

        }

        public void onChange(){

        }

        @Override
        final public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){

                case ACTION_USER_LOGIN:{
                    onLogin(mUser);
                    break;
                }
                case ACTION_USER_LOGOUT:{
                    onLogout();
                    break;
                }

                case ACTION_USER_CHANGE:{
                    onChange();
                    break;
                }
            }

        }
    }




}
