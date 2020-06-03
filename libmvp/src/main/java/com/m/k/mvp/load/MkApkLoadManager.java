package com.m.k.mvp.load;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.m.k.mvp.BuildConfig;
import com.m.k.mvp.Utils.MkLogger;
import com.m.k.mvp.Utils.SystemFacade;
import com.m.k.mvp.manager.MkManager;
import com.m.k.mvp.net.MkDataService;
import com.m.k.mvp.provider.MkApkInstallProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MkApkLoadManager {


    static final String KEY_APK_URL = "APK_URL";
    static final String KEY_FILE_NAME  = "APK_FILE_NAME";
    static final String KEY_FILE_VERSION  = "APK_FILE_VERSION";
    static final String KEY_MOITOR_STATUS = "APK_STATUS";

    private static final String APK_LOAD_DIR = "mvp_apk_dir";

    public static final String ACTION_LOAD_APK_STATUS = BuildConfig.LIBRARY_PACKAGE_NAME+".apk.load.status.action";
    public static final String APK_LOAD_STATUS ="STATUS";
    public static final String APK_DATA ="DATA";
    public static final String STATU_START = "START";
    public static final String STATU_PROGRESS = "PROGRESS";
    public static final String STATU_COMPLETE = "COMPLETE";
    public static final String STATU_ERROR = "ERROR";


    public static void startLoadApk(Context context,String apkUrl) {

       startLoadApk(context,apkUrl,"",null);


    }

    public static void startLoadApk(Context context,String apkUrl,String versionId) {
        startLoadApk(context,apkUrl,versionId,null);
    }


    public static void startLoadApk(Context context,String apkUrl, ApkLoadListener listener) {
        startLoadApk(context,apkUrl,"",listener);


    }

    public static void startLoadApk(Context context,String apkUrl,String versionId,ApkLoadListener listener) {


        String fileName = SystemFacade.getAppName(context);

        Intent intent = new Intent(context,ApkLoadService.class);
        intent.putExtra(KEY_APK_URL,apkUrl);
        intent.putExtra(KEY_FILE_NAME,fileName);
        intent.putExtra(KEY_FILE_VERSION,versionId);
        intent.putExtra(KEY_MOITOR_STATUS,listener != null);

        context.startService(intent);


        if(listener != null){
            IntentFilter intentFilter = new IntentFilter(MkApkLoadManager.ACTION_LOAD_APK_STATUS);

            LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    MkApkLoadManager.ApkLoadTask task = (MkApkLoadManager.ApkLoadTask) intent.getExtras().getSerializable(MkApkLoadManager.APK_DATA);

                    String status = intent.getStringExtra(MkApkLoadManager.APK_LOAD_STATUS);


                    switch (status){

                        case STATU_START:{
                            listener.onStart(task);

                            break;
                        }
                        case STATU_PROGRESS:{
                            listener.onProgress(task);
                            break;
                        }

                        case STATU_COMPLETE:{

                            listener.onComplete(task);
                            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);

                            break;
                        }

                        case STATU_ERROR:{

                            listener.onError(task);
                            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
                            break;
                        }

                    }




                }
            },intentFilter);
        }



    }

     static void loadAndInstall(ApkLoadTask task, ApkLoadListener listener) {



        Observable<ResponseBody> observable = MkDataService.getMvpApiService().downloadFile(task.apkUrl);


        observable.map(new Function<ResponseBody, InputStream>() {
            @Override
            public InputStream apply(ResponseBody responseBody) throws Exception {
                task.totalLength = responseBody.contentLength();

                return responseBody.byteStream();
            }
        })

                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {
                        writeToFile(task, inputStream, listener);
                    }
                })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<InputStream>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        handStartLoad(task, listener);
                    }

                    @Override
                    public void onNext(InputStream inputStream) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        task.msg = e.getMessage();
                        handError(task, listener);
                    }

                    @Override
                    public void onComplete() {
                        task.progress = 100;
                        handLoadComplete(task, listener);
                    }
                });


    }


    private static void writeToFile(ApkLoadTask task, InputStream inputStream, ApkLoadListener listener) {

        File dir = SystemFacade.getExternalCacheDir(MkManager.mContext, APK_LOAD_DIR);

        if (!dir.exists()) {
            dir.mkdir();
        }





        File apkFile = new File(dir, task.fileName);

        task.filePath = apkFile.getAbsolutePath();

        // 文件存在，并且已经完整下载下来
        if(apkFile.exists() && apkFile.length() == task.totalLength){
           handLoadComplete(task,listener);
            return;

        }


        // 在下载之前把之前的所有已经下载的apk 全部删除
            deleteOldApks(dir);


        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(apkFile);

            byte[] buffer = new byte[1024 * 4];
            int len = 0;

            long writeLength = 0;

            int oldProgress = 0;

            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);

                writeLength += len;


                task.progress = (int) (writeLength * 100 / task.totalLength);

                if(task.progress - oldProgress >= 1){
                    oldProgress = task.progress;

                    handProgress(task, listener);
                }
            }

            Thread.sleep(200);

            outputStream.flush();


        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    // 在下载之前把之前的所有已经下载的apk 全部删除
    private static void deleteOldApks(File dir){

        if(dir == null){
            return;
        }

        File oldFiles [] = dir.listFiles();

        if(oldFiles != null){
            for(File oldFile : oldFiles){
                oldFile.delete();
            }
        }

    }

    private static void handStartLoad(ApkLoadTask task, ApkLoadListener listener) {
        if (listener != null) {
            listener.onStart(task);
        }
    }

    private static void handProgress(ApkLoadTask task, ApkLoadListener listener) {
        if (listener != null) {
            MkLogger.d("progress = %s",task.getProgress());
            listener.onProgress(task);
        }
    }


    private static void handLoadComplete(ApkLoadTask task, ApkLoadListener listener) {
        installApk(task);
        if (listener != null) {
            MkLogger.d("完成");
            listener.onComplete(task);
        }
    }


    private static void handError(ApkLoadTask task, ApkLoadListener listener) {
        if (listener != null) {
            listener.onError(task);
        }
    }


    public static void installApk(ApkLoadTask task) {



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

        MkManager.mContext.startActivity(intent);

    }



    public static interface ApkLoadListener {

        void onStart(ApkLoadTask task);

        void onProgress(ApkLoadTask task);

        void onComplete(ApkLoadTask task);

        void onError(ApkLoadTask task);



    }


    public static class ApkLoadTask implements Serializable {

        private String fileName;

        private String showName;

        private String apkUrl;
        private String msg;
        public String filePath;
        private int progress;

        private long totalLength;

        private boolean isNeedMonitorStatus = true;


        public String getShowName() {
            return showName;
        }

         void setShowName(String showName) {
            this.showName = showName;
        }

         void setApkSaveFileName(String apkName) {
            this.fileName = apkName;
        }

         String getApkUrl() {
            return apkUrl;
        }

         void setApkUrl(String apkUrl) {
            this.apkUrl = apkUrl;
        }

        public String getMsg() {
            return msg;
        }


        public int getProgress() {
            return progress;
        }

         boolean isNeedMonitorStatus() {
            return isNeedMonitorStatus;
        }

         void setNeedMonitorStatus(boolean needMonitorStatus) {
            isNeedMonitorStatus = needMonitorStatus;
        }
    }


    public static class ApkInstallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("Test","-----------------");
            File dir = SystemFacade.getExternalCacheDir(MkManager.mContext, APK_LOAD_DIR);
            deleteOldApks(dir);

        }
    }
}
