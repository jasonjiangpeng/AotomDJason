package zxc.com.gkdvr.utils;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.HashMap;

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
        MyLogger.i(""+share.getInt(key, 0));
        return share.getInt(key, 0);
    }

    public static void showProgressDialog(String msg, boolean cancelable, Context context) {
        try {
            if (dialog == null) {
                dialog = new ProgressDialog(context);
            }
            if (dialog.isShowing()) {
                dialog.cancel();
            }
            dialog.setCancelable(cancelable);
            dialog.setMessage(msg);
            dialog.show();
            MyLogger.e("showProgressDialog" + System.currentTimeMillis());
        } catch (WindowManager.BadTokenException e) {
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

    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    public interface onVideoThumbnailLoadedListner {
        abstract void onVideoThumbnailLoaded(Bitmap bitmap);
    }

}
