package zxc.com.gkdvr.utils;

import java.io.File;
import java.util.Calendar;

public class FileUtil {
    /**
     * 根据文件名取得远程文件时间
     *
     * @param file
     * @return
     */
    public static long getRemoteFileTime(File file) {
        try {
            String name = file.getName();
            int index = name.lastIndexOf(".");
            if (index > 0) {
                name = name.substring(0, index);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(
                    Integer.parseInt(name.substring(0,4)),
                    Integer.parseInt(name.substring(4,6)) - 1,
                    Integer.parseInt(name.substring(6,8)),
                    Integer.parseInt(name.substring(8,10)),
                    Integer.parseInt(name.substring(10,12)),
                    Integer.parseInt(name.substring(12,14))
            );
            return calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static long getRemoteFileTime(String name) {
        try {
            int index = name.lastIndexOf(".");
            if (index > 0) {
                name = name.substring(0, index);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(
                    Integer.parseInt(name.substring(0,4)),
                    Integer.parseInt(name.substring(4,6)) - 1,
                    Integer.parseInt(name.substring(6,8)),
                    Integer.parseInt(name.substring(8,10)),
                    Integer.parseInt(name.substring(10,12)),
                    Integer.parseInt(name.substring(12,14))
            );
            return calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
