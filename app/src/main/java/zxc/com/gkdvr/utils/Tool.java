package zxc.com.gkdvr.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.R;

/**
 * Created by dk on 2016/5/31.
 */
public class Tool {
    private static ProgressDialog dialog;

    public static void saveToSharePrefrence(Context context, String key, int value) {
        MyLogger.i(new WifiAdmin(context).getSSID());
        SharedPreferences share = context.getSharedPreferences
                (new WifiAdmin(context).getSSID(), Context.MODE_PRIVATE);
        share.edit().putInt(key, value).commit();
    }

    public static int getFromSharePrefrence(Context context, String key) {
        SharedPreferences share = context.getSharedPreferences
                (new WifiAdmin(context).getSSID(), Context.MODE_PRIVATE);
        MyLogger.i("" + share.getInt(key, 0));
        return share.getInt(key, 0);
    }

    public static void showProgressDialog(String msg, boolean cancelable, Context context) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(context);
            }
            if (dialog.isShowing()) {
                dialog.setMessage(msg);
                return;
            }
            dialog.setCancelable(cancelable);
            dialog.setMessage(msg);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgressDialog2(String msg, boolean cancelable, Context context) {
        try {
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            View view = View.inflate(context,R.layout.dialog,null);
            TextView textView = (TextView) view.findViewById(R.id.id_tv_loadingmsg);
            textView.setText(msg);
            dialog = new ProgressDialog(context, R.style.Translucent_NoTitle);
            dialog.show();
            dialog.setContentView(view);
            dialog.setCancelable(cancelable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeProgressDialog() {
        MyLogger.e("removeProgressDialog" + System.currentTimeMillis());
        try {
            if (dialog != null) {
                dialog.cancel();
                dialog = null;
                System.gc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showToast(String string) {
        Toast.makeText(MyApplication.getCurrentActivity(), string, Toast.LENGTH_SHORT).show();
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *                  其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static void getVideoThumbnail(final String videoPath, final int width, final int height,
                                         final int kind, final onVideoThumbnailLoadedListner listner) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                // 获取视频的缩略图
                bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                listner.onVideoThumbnailLoaded(bitmap);
            }
        }).start();
    }

    public interface onVideoThumbnailLoadedListner {
        void onVideoThumbnailLoaded(Bitmap bitmap);
    }

    // 在进程中去寻找当前APP的信息，判断是否在前台运行
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                return true;
        }
        return false;
    }

    public static boolean isNetconn(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();
        return isMobileConn || isWifiConn;
    }

    public static void changeDialogText(AlertDialog alertDialog){
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object alertController = mAlert.get(alertDialog);
            Field mButtonNegative = alertController.getClass().getDeclaredField("mButtonNegative");
            Field mButtonPositive = alertController.getClass().getDeclaredField("mButtonPositive");
            mButtonNegative.setAccessible(true);
            mButtonPositive.setAccessible(true);
            Button btn1 = (Button) mButtonNegative.get(alertController);
            Button btn2 = (Button) mButtonPositive.get(alertController);
            btn1.setAllCaps(false);
            btn2.setAllCaps(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

}
