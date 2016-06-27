package zxc.com.gkdvr;

import android.app.Activity;
import android.app.Application;

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
        for (int i = 0; i < activities.size(); i++) {
            activities.pop().finish();
        }
        System.exit(0);
    }

    public static Activity getCurrentActivity() {
        return activities.peek();
    }

}
