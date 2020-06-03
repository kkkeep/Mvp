package com.m.k.mvp.load;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

 class ApkLoadService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        MkApkLoadManager.ApkLoadTask apkLoadTask = new MkApkLoadManager.ApkLoadTask();
        apkLoadTask.setApkUrl(intent.getStringExtra(MkApkLoadManager.KEY_APK_URL));

        String name = intent.getStringExtra(MkApkLoadManager.KEY_FILE_NAME);
        String version = intent.getStringExtra(MkApkLoadManager.KEY_FILE_VERSION);


        apkLoadTask.setNeedMonitorStatus(intent.getBooleanExtra(MkApkLoadManager.KEY_MOITOR_STATUS,false));

        apkLoadTask.setApkSaveFileName(name + "_" + apkLoadTask.getApkUrl().hashCode() + "_" + version +".apk");

        apkLoadTask.setShowName(name + "_"+ version +".apk");

        MkApkLoadManager.loadAndInstall(apkLoadTask,new NotificationListener(this){
            @Override
            public void onStart(MkApkLoadManager.ApkLoadTask task) {
                super.onStart(task);

                sendBroadcast(task, MkApkLoadManager.STATU_START);
            }

            @Override
            public void onComplete(MkApkLoadManager.ApkLoadTask task) {
                super.onComplete(task);

                sendBroadcast(task, MkApkLoadManager.STATU_COMPLETE);

                stopSelf();
            }

            @Override
            public void onProgress(MkApkLoadManager.ApkLoadTask task) {
                super.onProgress(task);
                sendBroadcast(task, MkApkLoadManager.STATU_PROGRESS);
            }

            @Override
            public void onError(MkApkLoadManager.ApkLoadTask task) {
                super.onError(task);
                sendBroadcast(task, MkApkLoadManager.STATU_ERROR);
                stopSelf();
            }
        });

        return super.onStartCommand(intent, flags, startId);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    private void sendBroadcast(MkApkLoadManager.ApkLoadTask task, String status){
        if(task.isNeedMonitorStatus()){


            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);

            Intent intent = new Intent(MkApkLoadManager.ACTION_LOAD_APK_STATUS);

            Bundle bundle = new Bundle();
            bundle.putSerializable(MkApkLoadManager.APK_DATA,task);
            intent.putExtras(bundle);
            intent.putExtra(MkApkLoadManager.APK_LOAD_STATUS,status);

            manager.sendBroadcast(intent);

        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
