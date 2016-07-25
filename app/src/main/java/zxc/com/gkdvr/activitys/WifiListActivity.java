package zxc.com.gkdvr.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import zxc.com.gkdvr.R;
import zxc.com.gkdvr.utils.Constance;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.PermissionUtil;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/7/20.
 */
public class WifiListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private WifiManager wifiManager;
    private List<ScanResult> data = new ArrayList<>();
    private TextView right;
    private ListView listView;
    private List<WifiConfiguration> wificonfigList = new ArrayList<>();
    private NetBroadcastReceiver receiver;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_wifi_list);
        right = ((TextView) findViewById(R.id.title_right));
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        ((TextView) findViewById(R.id.title_tv)).setText(getString(R.string.device_list));
        right.setText(getString(R.string.refresh));
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        receiver = new NetBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(Constance.ACTION_NET_CONN);
        intentFilter.setPriority(1000);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private synchronized void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtil.hasPermisson(Manifest.permission.ACCESS_WIFI_STATE)) {
                new PermissionUtil().askforPermission(Manifest.permission.ACCESS_WIFI_STATE);
            }
            if (!PermissionUtil.hasPermisson(Manifest.permission.ACCESS_FINE_LOCATION )) {
                new PermissionUtil().askforPermission(Manifest.permission.ACCESS_FINE_LOCATION );
            }
            if (!PermissionUtil.hasPermisson(Manifest.permission.ACCESS_COARSE_LOCATION )) {
                new PermissionUtil().askforPermission(Manifest.permission.ACCESS_COARSE_LOCATION );
            }
        }
        Tool.showProgressDialog(getString(R.string.scaning), false, this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        openWifi();
        wifiManager.startScan();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Tool.removeProgressDialog();
                List<ScanResult> list = wifiManager.getScanResults();
                if (list == null) {
                    Tool.showToast(getString(R.string.wifi_not_open));
                    finish();
                } else {
                    data.clear();
                    for (ScanResult result : list) {
                        if (result.SSID.startsWith("UBI") || result.SSID.startsWith("DVR"))
                            data.add(result);
                    }
                    MyLogger.i(data.toString());
                    listView.setAdapter(new MyAdapter());
                }
            }
        }, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                    List<ScanResult> scanResults = wifiManager.getScanResults();
                    MyLogger.i(scanResults.toString());
                    //list is still empty
                }
                break;
        }
    }

    /**
     * 打开WIFI
     */
    private void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!wifiManager.isWifiEnabled()) {
            Tool.showToast(getString(R.string.wifi_not_open));
            initData();
        }
        ScanResult result = data.get(position);
        int i = isConfigured("\"" + result.SSID + "\"");
        if (i != -1) {
            if (ConnectWifi(i)) {
                Tool.showProgressDialog(getString(R.string.wifi_connecting), true, this);
            } else {
                Tool.showToast(getString(R.string.wifi_connecting_fail));
            }
        } else {
            i = AddWifiConfig(result.SSID, "12345678");
            if (i != -1) {
                if (ConnectWifi(i)) {
                    Tool.showProgressDialog(getString(R.string.wifi_connecting), true, this);
                } else {
                    Tool.showToast(getString(R.string.wifi_connecting_fail));
                }
            } else {
                Tool.showToast(getString(R.string.wifi_connecting_fail));
            }
        }
    }

    //添加指定WIFI的配置信息,原列表不存在此SSID
    public int AddWifiConfig(String ssid, String pwd) {
        WifiConfiguration wifiCong = new WifiConfiguration();
        wifiCong.SSID = "\"" + ssid + "\"";//\"转义字符，代表"
        wifiCong.preSharedKey = "\"" + pwd + "\"";//WPA-PSK密码
        wifiCong.hiddenSSID = false;
        wifiCong.status = WifiConfiguration.Status.ENABLED;
        return wifiManager.addNetwork(wifiCong);
    }

    public boolean ConnectWifi(int wifiId) {
        boolean isConnect = false;
        wificonfigList = wifiManager.getConfiguredNetworks();
        int id = 0;
        for (int i = 0; i < wificonfigList.size(); i++) {
            WifiConfiguration wifi = wificonfigList.get(i);
            id = wifi.networkId;
            if (id == wifiId) {
//                while (!(wifiManager.enableNetwork(wifiId, true))) {
//                }
                if (wifiManager.enableNetwork(id, true)) {
                    return true;
                }
            }
        }
        if (wifiManager.enableNetwork(wifiId, true)) {
            return true;
        }
        return isConnect;
    }

    public int isConfigured(String SSID) {
        wificonfigList = wifiManager.getConfiguredNetworks();
        for (int i = 0; i < wificonfigList.size(); i++) {
            if (wificonfigList.get(i).SSID.equals(SSID)) {
                return wificonfigList.get(i).networkId;
            }
        }
        return -1;
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            view = View.inflate(WifiListActivity.this, R.layout.item_wifi_list, null);
            ScanResult scanResult = data.get(position);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(scanResult.SSID);
            return view;
        }
    }


    class NetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MyLogger.i("onReceive");
            abortBroadcast();
            Tool.removeProgressDialog();
            finish();
        }
    }

}
