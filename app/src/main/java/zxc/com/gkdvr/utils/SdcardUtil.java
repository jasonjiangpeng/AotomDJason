package zxc.com.gkdvr.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;

import zxc.com.gkdvr.MyApplication;

/**
 * Created by dk on 2016/6/10.
 */
public class SdcardUtil {
    /**
     * 是否有外存卡
     *
     * @return
     */
    public static boolean isExistExternalStore() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static String getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(MyApplication.getCurrentActivity(), blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static String getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(MyApplication.getCurrentActivity(), blockSize * availableBlocks);
    }
}
