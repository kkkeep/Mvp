package com.m.k.mvp.load;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.m.k.mvp.Utils.SystemFacade;
import com.m.k.mvp.manager.MkManager;
import com.m.k.mvp.provider.MkApkInstallProvider;

import java.io.File;

 class NotificationListener implements MkApkLoadManager.ApkLoadListener {



    private static final String DOWNLOAD_CHANNEL_ID = "mvp_apk_download";

    private Context mContext;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

     NotificationListener(Context context) {
        this.mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        createNotificationChannel();

    }


    /**
     * 创建一个 通知 的 Channel（分类）
     *
     * 如果这个Channel 不存在就创建，存在的话不会重复创建
     */
    private void createNotificationChannel(){

        if (SystemFacade.hasO()) {

            int importance = NotificationManager.IMPORTANCE_LOW; // 设置这个通知 Channel的重要性
            NotificationChannel channel = new NotificationChannel(DOWNLOAD_CHANNEL_ID, "App升级下载", importance);
            channel.setDescription("当检测到新版本点击下载时显示的通知");
            mNotificationManager.createNotificationChannel(channel);

        }

    }

    @Override
    public void onStart(MkApkLoadManager.ApkLoadTask task) {

        mBuilder = new NotificationCompat.Builder(mContext,DOWNLOAD_CHANNEL_ID);

        mBuilder.setContentTitle(task.getShowName());
        mBuilder.setContentText("链接网络准备下载");
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        mBuilder.setPriority(NotificationCompat.PRIORITY_LOW); // 设置通知的优先级，只有在下拉时才能看得见


        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;

        mBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false) //设置通知的进度条
                .setAutoCancel(false); //// 当用户点击通知后，true e通知从任务栏里面消失，false 不消失

        Notification notification = mBuilder.build();
        mNotificationManager.notify(task.getApkUrl().hashCode(),notification);
        
    }

    @Override
    public void onProgress(MkApkLoadManager.ApkLoadTask task) {
       // mBuilder.setProgress()


        mBuilder.setContentText("正在下载 " + task.getProgress() + "%").setProgress(100,task.getProgress(),false)
                .setAutoCancel(false);

        mNotificationManager.notify(task.getApkUrl().hashCode(),mBuilder.build());
    }

    @Override
    public void onComplete(MkApkLoadManager.ApkLoadTask task) {

        mBuilder.setContentText("下载完成点击安装 ").setProgress(0,0,false).setAutoCancel(true);



        Uri uri = null;

        if (SystemFacade.hasN()) {
            uri = MkApkInstallProvider.getUriForFile(MkManager.mContext, SystemFacade.getPackageName(MkManager.mContext) + ".apk.InstallProvider", new File(task.filePath));
        } else {
            uri = Uri.fromFile(new File(task.filePath));
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext.getApplicationContext(), task.getApkUrl().hashCode(), intent, 0);

        mNotificationManager.notify(task.getApkUrl().hashCode(),mBuilder.setContentIntent(pendingIntent).build());
    }

    @Override
    public void onError(MkApkLoadManager.ApkLoadTask task) {
        mBuilder.setContentText("下载失败 ").setProgress(0,0,false).setAutoCancel(true);
        mNotificationManager.notify(task.getApkUrl().hashCode(),mBuilder.build());
    }
}
