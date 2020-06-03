package com.m.k.mvp.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

/*
 * created by Cherry on 2019-06-18
 **/
@SuppressLint("MissingPermission")
public class SystemFacade {

    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }


    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasHoneycombMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
    }

    public static boolean hasIcecreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean hasIcecreamSandwichMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean hasN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean hasO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static boolean hasP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    public static boolean hasQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }


    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    public static int getDensityDpi(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.densityDpi;
    }

    /**
     * dp  转 px
     *
     * @param context
     * @param dp      传入多少dp
     * @return 返回dp 换算成px 后的值
     */
    public static int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);

    }

    /**
     * 获取手机剩余存储容量
     */
    public static long getAvailableSize() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(file.getPath());

            //获得Sdcard上每个block的size
            long blockSize = statFs.getBlockSize();
            //获取可供程序使用的Block数量
            long blockavailable = statFs.getAvailableBlocks();
            //计算标准大小使用：1024，当然使用1000也可以
            long blockavailableTotal = blockSize * blockavailable / 1024 / 1024;
            return blockavailableTotal;
        } else {
            return -1;
        }
    }

    public static int getScreenOrientation(Context context) {
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        return mConfiguration.orientation;
    }

    public static String getAppName(Context context) {
        return context.getApplicationInfo()
                .loadLabel(context.getPackageManager()).toString();
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static double getScreenInch(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        return diagonalInches;
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //Requires Permission: READ_PHONE_STATE
    public static String getSimNum(Context context) {
        if (hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager != null) {
                return manager.getSimSerialNumber();
            }
        }
        return "";
    }

    //Requires Permission: READ_PHONE_STATE OR READ_SMS
    public static String getPhoneNum(Context context) {
        try {
            if (hasPermission(context, Manifest.permission.READ_PHONE_STATE) || hasPermission(context, Manifest.permission.READ_SMS)) {
                TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (manager != null) {
                    return manager.getLine1Number();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMacAddr(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) {
                    String mac = info.getMacAddress();

                    if (TextUtils.isEmpty(mac)) {
                        return readMacAddress();
                    } else {
                        return mac;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取设备的电量
     *
     * @return int
     */
    public static int getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = context.registerReceiver(null, ifilter);
        if (intent != null) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (level == -1 || scale == -1) {
                return 0;
            }
            return (int) (100.0f * (level / (float) scale));
        }
        return 0;
    }

    /**
     * 网络运营商
     *
     * @return 0为未知，1移动，2联通，3电信
     */
    //Requires Permission: READ_PHONE_STATE
    public static int getNetProvider(Context context) {
        int providerid = 0;
        if (hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            // 返回唯一的用户ID;就是这张卡的编号神马的
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String IMSI = telephonyManager.getSubscriberId();
            if (TextUtils.isEmpty(IMSI)) {
                return providerid;
            }
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                providerid = 1;
            } else if (IMSI.startsWith("46001")) {
                providerid = 2;
            } else if (IMSI.startsWith("46003")) {
                providerid = 3;
            }
        }
        return providerid;
    }

    public static boolean hasPermission(Context context, String permission) {
        PackageManager manager = context.getPackageManager();
        return manager.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }


    public static boolean isOnInternet(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getDeviceNetType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.getType();
            }
        }
        return -1;
    }

    public static int getDeviceSubNetType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.getSubtype();
            }
        }
        return -1;
    }

    public static String getOperatorID(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            return manager.getNetworkOperator();
        }
        return null;
    }

    //Requires Permission: ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION
    public static int getCellularID(Context context) {
        if (hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) || hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager != null) {
                CellLocation cellLocation = manager.getCellLocation();
                if (GsmCellLocation.class.isInstance(cellLocation)) {
                    return ((GsmCellLocation) cellLocation).getCid();
                } else if (CdmaCellLocation.class.isInstance(cellLocation)) {
                    return ((CdmaCellLocation) cellLocation).getBaseStationId();
                }
            }
        }
        return -1;
    }

    //Requires Permission: ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION
    public static List<ScanResult> getWifiScanResult(Context context) {
        if (hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) || hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                if (wifiManager.isWifiEnabled()) {
                    return wifiManager.getScanResults();
                }
            }
        }
        return null;
    }

    public static WifiInfo getContectedWifiInfo(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                return wifiManager.getConnectionInfo();
            }
        }
        return null;
    }

    private static String readUUID(File fuuid) throws IOException {
        RandomAccessFile f = new RandomAccessFile(fuuid, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static boolean isMainThread(Context context) {
        return context.getMainLooper() == Looper.myLooper();
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean activityIsDestroy(WeakReference<Activity> mReference) {
        Activity activity = null;
        if (mReference != null) {
            activity = mReference.get();
        }
        if (activity == null) {
            return true;
        }
        boolean activityIsDstroyed;

        if (SystemFacade.hasJellyBeanMR1()) {
            activityIsDstroyed = activity.isDestroyed();
        } else {
            activityIsDstroyed = false;
        }

        if (!activity.isFinishing() && !activityIsDstroyed) {
            return false;
        }
        MkLogger.w(" Activity is Destroy");
        return true;
    }

    public static File getExternalCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        String cachePath = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable()) {
                cachePath = getExternalCacheDir(context).getPath();
                // /Android/data/com.xxx.xxx/cache
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(cachePath)) {
            return new File(cachePath + File.separator + uniqueName);   //这才是完整的路径
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static boolean isExternalStorageRemovable() {
        if (SystemFacade.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    private static File getExternalCacheDir(Context context) {
        if (SystemFacade.hasFroyo()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static String getProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = mActivityManager.getRunningAppProcesses();

        if (runningAppProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }

        return null;
    }

    private static String readMacAddress() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macSerial;
    }

    public static int getTargetSdkVersion(Context context) {
        PackageManager manager = context.getPackageManager();

        try {
            return manager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static void requestPermissions(Activity activity, String permission) {
        try {
            Class claz = Class.forName("android.support.v4.content.ActivityCompat");
            if (claz != null) {
                Method mt = claz.getMethod("requestPermissions", Activity.class, String[].class, int.class);
                if (mt != null) {
                    mt.setAccessible(true);
                    mt.invoke(null, activity, new String[]{permission}, 100);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static String getDeviceId(Context context) {
        if (hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                return telephonyManager.getDeviceId();
            }
        } else {
            MkLogger.d("SDKManager %s", "no permission");
        }
        return null;
    }


    public static int getRsId(Context context, String name) {
        int resId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return resId;
    }

    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }


    public static String getAndroidId(Context context) {
        String aid = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(aid) && aid.length() > 1) {
            return aid;
        }

        return "";
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect rectangle = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);


        return rectangle.top;
    }


    public static boolean isListEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static <T> T requiredNotNull(T t, String msg) {
        if (t == null) {
            throw new NullPointerException(msg);
        }

        return t;
    }

    /**
     * 产生一个随机的颜色
     * @return
     */
    public static int randomColor() {
        Random random = new Random();
        //0-190, 如果颜色值过大,就越接近白色,就看不清了,所以需要限定范围
        int red =random.nextInt(190);
        //0-190
        int green =random.nextInt(190);
        //0-190
        int blue =random.nextInt(190);
        //使用rgb混合生成一种新的颜色,Color.rgb生成的是一个int数
        return Color.rgb(red,green, blue);
    }



    public static boolean isValidSmsCodeNumber(String code){
        if(TextUtils.isEmpty(code)){
            return false;
        }

        if(code.trim().length() <  4 || code.trim().length() > 6){
            return false;
        }

        if(!code.matches("^[0-9]\\d*$")){
            return false;
        }


        return true;
    }

    public static boolean isValidPhoneNumber(String phoneNumber){
        if(TextUtils.isEmpty(phoneNumber)){
            return false;
        }

        if(phoneNumber.trim().length() != 11){
            return false;
        }

        if(!phoneNumber.matches("^[0-9]\\d*$")){
            return false;
        }


        return true;
    }

    public void switchInputKeybord(Context context){
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager)context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        if(imm == null){
            return;
        }
        if (imm.isActive()) {
           // imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }

    }

    public static String unicodeToString(String unicode) {
        StringBuffer sb = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int index = Integer.parseInt(hex[i], 16);
            sb.append((char) index);
        }
        return sb.toString();
    }

    public static String hidePhoneNum(String phone) {

        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

}
