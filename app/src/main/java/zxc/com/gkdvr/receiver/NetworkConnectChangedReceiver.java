package zxc.com.gkdvr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import java.util.ArrayList;

import zxc.com.gkdvr.utils.MyLogger;

/**
 * Created by xiaoyunfei on 15/11/22.
 */
public class NetworkConnectChangedReceiver extends BroadcastReceiver {

    public static ArrayList<OnNetChangeListener> mListeners = new ArrayList<>();
    public static String wifiName = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            ;
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State wifiState = networkInfo.getState();
                if (wifiState == NetworkInfo.State.CONNECTED) {
                    String name = networkInfo.getExtraInfo();
                    if (name.startsWith("DVR_")) {
                        wifiName = name;
                    }
                } else if (wifiState == NetworkInfo.State.DISCONNECTED) {
                    if (wifiName.startsWith("DVR_")) {
                        netChange(wifiName + " DISCONNECTED");
                    }
                }
            }
        }
    }

    private void netChange(String message) {
        if (mListeners.size() > 0)// 通知接口完成加载
            for (OnNetChangeListener handler : mListeners) {
                handler.onNetChange(message);
            }
    }


    public interface OnNetChangeListener {
        void onNetChange(String message);
    }
}
