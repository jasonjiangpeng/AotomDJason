package zxc.com.gkdvr;

import android.app.Activity;
import android.app.Application;

import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.update.PgyUpdateManager;

import java.util.Stack;

import zxc.com.gkdvr.utils.FileAccessor;

/**
 * Created by dk on 2016/6/1.
 */
public class MyApplication extends Application {
    public static Stack<Activity> activities = new Stack<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void exit() {
        try {
            for (int i = 0; i < activities.size(); i++) {
                activities.pop().finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public static Activity getCurrentActivity() {
        return activities.peek();
    }

}
