package zxc.com.gkdvr.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.receiver.NetworkConnectChangedReceiver;
import zxc.com.gkdvr.receiver.ScreenActionReceiver;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.Tool;
import zxc.com.gkdvr.utils.UIUtil;

/**
 * Created by dk on 2016/6/1.
 */
public class BaseActivity extends AppCompatActivity {
    public int width;
    public int height;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.activities.add(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        UIUtil.init(this, dm.widthPixels, dm.heightPixels);
    }

    void setTitleText(String title) {
        TextView t = (TextView) findViewById(R.id.title_tv);
        if (t != null) {
            t.setText(title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLogger.i(getClass().getName() + "onResume");
        ScreenActionReceiver.cancelDisconn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MyApplication.appManager.finishActivity(this);
        MyLogger.i(getClass().getName() + "onDestroy");
        MyApplication.activities.remove(this);

    }

    int choisePosition;

    public void showSimpleChoiceDialog(String[] items, final DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this).setSingleChoiceItems(items, choisePosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choisePosition = which;
            }
        }).setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(dialog, which);
                    }
                }).create().show();
    }

    public boolean isWifiConnectedToDVR() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gl_wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!wifiManager.isWifiEnabled()) {
            return false;
        }
        String device_ssid = wifiManager.getConnectionInfo().getSSID().toString().replace("\"", "");
        if (gl_wifiInfo.isConnected()) {
            if (device_ssid.startsWith("DVR") || device_ssid.startsWith("UBI")) {
                NetworkConnectChangedReceiver.wifiName = device_ssid;
                return true;
            }
        }
        return false;
    }

    public void showConnectingDialog() {
        new android.support.v7.app.AlertDialog.Builder(this).setTitle(getString(R.string.notice))
                .setMessage(getString(R.string.connecting_device))
                .setPositiveButton(getString(R.string.connecting), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                        startActivity(intent);
                    }
                }).show();
    }

    public boolean isDoubleCamera() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gl_wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!wifiManager.isWifiEnabled()) {
            return false;
        }
        String device_ssid = wifiManager.getConnectionInfo().getSSID().toString().replace("\"", "");
        if (gl_wifiInfo.isConnected()) {
            if (device_ssid.startsWith("DVR2") || device_ssid.startsWith("UBI")) {
                NetworkConnectChangedReceiver.wifiName = device_ssid;
                return true;
            }
        }
        return false;
    }

}
