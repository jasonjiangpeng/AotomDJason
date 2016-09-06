package zxc.com.gkdvr.utils;

import android.util.Log;

import java.util.Hashtable;

public class MyLogger {
    private final static boolean logFlag = true;
    public final static String tag = "[" + Constance.APP_NAME + "]";
    private static String userName = "dk";

    public MyLogger(String userName) {
        this.userName = userName;
    }

    /**
     * 获取方法名
     *
     * @return
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(MyLogger.class.getName())) {
                continue;
            }
            return "@" + userName + "@ " + "[ "
                    + Thread.currentThread().getName() + ": "
                    + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }
        return null;
    }

    public static void i(String str) {
        if (logFlag) {
            String name = getFunctionName();
            if (name != null) {
                Log.i(tag, name + " - " + str);
            } else {
                Log.i(tag, str.toString());
            }
        }
    }

    public static void d(String str) {
        if (logFlag) {
            String name = getFunctionName();
            if (name != null) {
                Log.d(tag, name + " - " + str);
            } else {
                Log.d(tag, str.toString());
            }
        }
    }

    public static void v(String str) {
        if (logFlag) {
            String name = getFunctionName();
            if (name != null) {
                Log.v(tag, name + " - " + str);
            } else {
                Log.v(tag, str.toString());
            }
        }
    }

    public static void w(String str) {
        if (logFlag) {
            String name = getFunctionName();
            if (name != null) {
                Log.w(tag, name + " - " + str);
            } else {
                Log.w(tag, str.toString());
            }
        }
    }

    public static void e(String str) {
        if (logFlag) {
            String name = getFunctionName();
            if (name != null) {
                Log.e(tag, name + " - " + str);
            } else {
                Log.e(tag, str.toString());
            }
        }
    }


}
