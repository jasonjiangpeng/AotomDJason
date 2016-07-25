package zxc.com.gkdvr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.util.Timer;
import java.util.TimerTask;

import zxc.com.gkdvr.utils.Tool;
import zxc.com.gkdvr.utils.WifiAdmin;


/**
 * Created by dk on 2016/6/28.
 */
public class ScreenActionReceiver extends BroadcastReceiver {
    public static Timer netTimer;
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static boolean isTimerScheduled = false;
    public static boolean isReloadRtsp = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                if (!isTimerScheduled)
                    disconnectWifi(context);
            }
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            if (Tool.isAppOnForeground(context)) {
                isReloadRtsp = true;
            } else {
                isReloadRtsp = false;
            }
            cancelDisconn();
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            isReloadRtsp = true;
            disconnectWifi(context);
        }
    }

    public static void cancelDisconn() {
        if (netTimer != null) {
            netTimer.cancel();
            isTimerScheduled = false;
            netTimer = null;
        }
    }

    private void disconnectWifi(final Context context) {
        if (!isWifiConnectedToDVR(context)) {
            return;
        }
        isTimerScheduled = true;
        netTimer = new Timer();
        netTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new WifiAdmin(context).disconnectWifi();
                isTimerScheduled = false;
            }
        }, 20000);
    }

    public boolean isWifiConnectedToDVR(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gl_wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!wifiManager.isWifiEnabled()) {
            return false;
        }
        String device_ssid = wifiManager.getConnectionInfo().getSSID().toString().replace("\"", "");
        if (gl_wifiInfo.isConnected()) {
            if (device_ssid.contains("DVR_") || device_ssid.startsWith("UBI")) {
                NetworkConnectChangedReceiver.wifiName = device_ssid;
                return true;
            }
        }
        return false;
    }
}
