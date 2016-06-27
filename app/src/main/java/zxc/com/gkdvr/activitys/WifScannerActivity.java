//package zxc.com.gkdvr.activitys;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.ServiceConnection;
//import android.content.SharedPreferences;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiConfiguration;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.PowerManager;
//import android.preference.PreferenceManager;
//import android.support.v7.app.AlertDialog;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.dash.R;
//import com.database.SQLite;
//import com.database.SQLiteCMD;
//import com.network.WifiAdmin;
//import com.service.MessageService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class WifScannerActivity extends Activity {
//    private ListView wifi_list;
//    private WifiManager wifiManager;
//    private PowerManager.WakeLock mWakeLock;
//
//    private WifiAdmin wifiAdmin;
//    public boolean isrefresh = false;
//    protected ListAdapter mListAdapter;
//    private int gl_wifiConfigIndex = -1;
//    private List<ScanResult> scans;
//    private EditText password_input;
//    private ProgressDialog pDialog;
//    private WifiReceiver receiverWifi;
//    private boolean isConnectDevice = false;
//    private boolean isBinded = false;
//    private boolean isScan = false;
//
//    private SharedPreferences prefs;
//    private Service service;
//    private String device_name = "";
//    private int gl_connect_count;
//    private String gl_from = "";
//    private Handler ui_Handler;
//    private static boolean gl_isConnectdevice = false;
//    private long gl_lastClick = 0l;
//    private SQLiteCMD sql = null;
//    private Button btnNext;
//    private TextView title_text;
//    private Button btn_confirm;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wif_scanner);
//        parserIntent(getIntent());
//        initUI();
//        initData();
//        initEvent();
//
//    }
//
//
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        Log.d("Allen", "dialog scan++++++++++++++++++++++");
//        ui_Handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                scan();
//            }
//        }, 300);
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiverWifi);
//        doUnbind();
//    }
//
//    private void doBind() {
//        Intent intent = new Intent();
//        intent.setClass(WifScannerActivity.this, MessageService.class);
//        bindService(intent, conn, Context.BIND_AUTO_CREATE);
//    }
//
//    private void doUnbind() {
//        if (isBinded) {
//            unbindService(conn);
//            isBinded = false;
//        }
//    }
//
//    private ServiceConnection conn = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder binder) {
//            isBinded = true;
//            service = ((MessageService.ServiceBinder) binder).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            isBinded = false;
//        }
//    };
//
//    private void acquireWakeLock() {
//        if (mWakeLock == null) {
//            PowerManager mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//            mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, this.getClass().getCanonicalName());
//            mWakeLock.acquire();
//        }
//    }
//
//    private void releaseWakeLock() {
//        if (mWakeLock != null && mWakeLock.isHeld()) {
//            mWakeLock.release();
//            mWakeLock = null;
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        // TODO Auto-generated method stub
//        super.onStart();
//        gl_isConnectdevice = false;
//        gl_lastClick = 0l;
//        acquireWakeLock();
//    }
//
//    @Override
//    protected void onStop() {
//        // TODO Auto-generated method stub
//        super.onStop();
//        releaseWakeLock();
//    }
//
//    private void initUI() {
//        wifi_list = (ListView) findViewById(R.id.wifi_list);
//        btnNext = (Button) findViewById(R.id.btnNext);
//        title_text = (TextView)findViewById(R.id.title_text);
//        btn_confirm = (Button)findViewById(R.id.btn_confirm);
//        password_input = (EditText) findViewById(R.id.password_input);
//        wifi_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                wifiAdmin.enableAllnetwork();
//                ImageView iv = (ImageView) view.findViewById(R.id.is_connect);
//                mListAdapter.setConnectPosition(position);
//                iv.setVisibility(View.VISIBLE);
//                mListAdapter.notifyDataSetChanged();
//                gl_wifiConfigIndex = position;
//
//                String tmpPwd = sql.mSearch(SQLite.DATABASE_DEVICE_SSID, scans.get(position).SSID, SQLite.DATABASE_DEVICE_PWD);
//                Log.d("Allen", "tmpPwd =" + tmpPwd);
//
//                password_input.setText(tmpPwd);
//            }
//        });
//    }
//
//
//    protected void parserIntent(Intent intent) {
//        try {
//            gl_from = intent.getExtras().getString("from", "splash");
//        } catch (Exception e) {
//            gl_from = "splash";
//        }
//
//
//    }
//
//
//    protected void initData() {
//        title_text.setText("Wifi设备列表");
//        btn_confirm.setText("刷新");
//        sql = new SQLiteCMD(this);
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        wifiAdmin = new WifiAdmin(WifScannerActivity.this);
//        receiverWifi = new WifiReceiver();
//        IntentFilter mFilter = new IntentFilter();
//        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(receiverWifi, mFilter);
//        ui_Handler = new Handler();
//        if (!gl_from.equals("splash")) {
//            doBind();
//        }
//
//    }
//
//
//    protected void initEvent() {
//        findViewById(R.id.title_icon).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        btn_confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                gl_wifiConfigIndex = -1;
//                scan();
//            }
//        });
//
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nextStep();
//            }
//        });
//    }
//
//    private void nextStep() {
//        if (System.currentTimeMillis() - gl_lastClick < 1000) {
//            return;
//
//        }
//        gl_lastClick = System.currentTimeMillis();
//        if (gl_wifiConfigIndex < 0) {
//            Toast.makeText(WifScannerActivity.this, R.string.toast_choose_device, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        gl_connect_count = 0;
//        int authType = getAuth(scans.get(gl_wifiConfigIndex).capabilities.replace("[ESS]", ""));
//        String pwd = password_input.getText().toString();
//
//        if (authType > 0 && pwd.equals("")) {
//            Toast.makeText(WifScannerActivity.this, R.string.toast_pwd_empty, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (pwd.length() != 5) {
//            Toast.makeText(WifScannerActivity.this, R.string.toast_pwd_length_error, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        isConnectDevice = true;
//
//        openProgressDialog(getString(R.string.connecting), getString(R.string.please_wait));
//        WifiConfiguration w = wifiAdmin.CreateWifiInfo(scans.get(gl_wifiConfigIndex).SSID, pwd,authType);
//        wifiAdmin.addNetwork(w);
//        ui_Handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                if (!gl_isConnectdevice) {
//                    closeProgressDialog();
//                    Toast.makeText(WifScannerActivity.this,
//                            getString(R.string.connect_fail),
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        }, 10000);
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (wifiManager.isWifiEnabled()) {
//            wifiAdmin.enableAllnetwork();
//        }
//    }
//
//    private void scan() {
//        gl_isConnectdevice = false;
//        if (!wifiManager.isWifiEnabled()) {
//            // wifiManage.setWifiEnabled(true);
//            showCustomDialog(getString(R.string.device_scan_failure), getString(R.string.please_open_wifi), true);
//            return;
//        }
//        wifiManager.startScan();
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                setTitle("正在扫描设备");
//            }
//        });
//
//        isScan = true;
//
//        Log.d("Allen", "dialog scan-----------------------");
//        ui_Handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                setTitle("WI-FI列表");
//            }
//        }, 3000);
//    }
//
//    private void openProgressDialog(String title, String msg) {
//        if (pDialog != null) {
//            return;
//        }
//
//        pDialog = new ProgressDialog(this);
//        pDialog.setTitle(title);
//        pDialog.setMessage(msg);
//        pDialog.setIndeterminate(true);
//        pDialog.show();
//    }
//
//    private void closeProgressDialog() {
//        if (pDialog != null && pDialog.isShowing()) {
//            pDialog.dismiss();
//
//        }
//        pDialog = null;
//    }
//
//    class WifiReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0) == WifiManager.ERROR_AUTHENTICATING) {
//                showWifiErrorDialog();
//            }
//            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
//                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
//
//                switch (wifiState) {
//                    case WifiManager.WIFI_STATE_DISABLED:
//                        break;
//                    case WifiManager.WIFI_STATE_DISABLING:
//                        break;
//                    case WifiManager.WIFI_STATE_ENABLED:
//                        scan();
//                        break;
//                    //
//                }
//            }
//            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//
//                if (!isScan) {
//                    return;
//                }
//                isScan = false;
//                List<ScanResult> scanResults = wifiManager.getScanResults();
//                scans = new ArrayList<ScanResult>();
//                for (ScanResult scanResult : scanResults) {
//                    if (isValidUID(scanResult.SSID)) {
//                        scans.add(scanResult);
//                    }
//                    // if(!scanResult.SSID.equals(""))
//                    // scans.add(scanResult);
//                }
//                mListAdapter = new ListAdapter(scans, WifScannerActivity.this, 0);
//
//                wifi_list.setAdapter(mListAdapter);
//                mListAdapter.notifyDataSetChanged();
//
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        setTitle("WI-FI列表");
//                    }
//                });
//                closeProgressDialog();
//            } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//                WifiManager mWifiManager = (WifiManager) WifScannerActivity.this.getSystemService(Context.WIFI_SERVICE);
//                WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
//
//                if (isConnectDevice) {
//                    if (gl_wifiConfigIndex == -1) {
//                        return;
//                    }
//                    if (scans != null && mWifiInfo.getSSID() != null && mWifiInfo.getSSID().equals(scans.get(gl_wifiConfigIndex).SSID)) {
//                        closeProgressDialog();
//
//                        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                        //
//                        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//                        if (mWifi.isConnected()) {
//
//                        } else {
//
//                            return;
//                        }
//                        isConnectDevice = false;
//
//                        if (sql.mSearch(SQLite.DATABASE_DEVICE_SSID, scans.get(gl_wifiConfigIndex).SSID, SQLite.DATABASE_DEVICE_SSID).equals("")) {
//                            sql.savaDevice(scans.get(gl_wifiConfigIndex).SSID, password_input.getText().toString(), 1);
//                        } else {
//                            sql.updatePwd(scans.get(gl_wifiConfigIndex).SSID.replace("\"", ""), password_input.getText().toString());
//                        }
//
////						if (!device_name.equals("")) {
//                        if (gl_from.equals("setting")) {
//                            prefs.edit().putString("DEVICE_SSID", scans.get(gl_wifiConfigIndex).SSID).commit();
//                            prefs.edit().putString("DEVICE_PWD", password_input.getText().toString()).commit();
//                            prefs.edit().putInt("DEVICE_AUTHTYPE", 1).commit();
//                            if (service != null) {
//                                ((MessageService) service).reconnect();
//                            }
//
//                            Toast.makeText(WifScannerActivity.this, getString(R.string.setting_command_success), Toast.LENGTH_SHORT).show();
//                        } else {
//                            prefs.edit().putString("DEVICE_SSID", scans.get(gl_wifiConfigIndex).SSID).commit();
//                            prefs.edit().putString("DEVICE_PWD", password_input.getText().toString()).commit();
//                            prefs.edit().putInt("DEVICE_AUTHTYPE", 1).commit();
//                            gl_isConnectdevice = true;
//                            WifScannerActivity.this.setResult(Activity.RESULT_OK);
//                            finish();
//                        }
//                    } else if (scans != null && mWifiInfo.getSSID() != null && mWifiInfo.getSSID().equals("\"" + scans.get(gl_wifiConfigIndex).SSID + "\"")) {
//                        closeProgressDialog();
//
//                        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                        //
//                        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//                        if (mWifi.isConnected()) {
//
//                        } else {
//
//                            return;
//                        }
//                        isConnectDevice = false;
//
//                        if (sql.mSearch(SQLite.DATABASE_DEVICE_SSID, scans.get(gl_wifiConfigIndex).SSID, SQLite.DATABASE_DEVICE_SSID).equals("")) {
//                            sql.savaDevice(scans.get(gl_wifiConfigIndex).SSID, password_input.getText().toString(), 1);
//                        } else {
//                            sql.updatePwd(scans.get(gl_wifiConfigIndex).SSID.replace("\"", ""), password_input.getText().toString());
//                        }
//
//                        if (gl_from.equals("setting")) {
//                            prefs.edit().putString("DEVICE_SSID", scans.get(gl_wifiConfigIndex).SSID).commit();
//                            prefs.edit().putString("DEVICE_PWD", password_input.getText().toString()).commit();
//                            prefs.edit().putInt("DEVICE_AUTHTYPE", 1).commit();
//                            if (service != null) {
//                                ((MessageService) service).reconnect();
//                            }
//
//                            Toast.makeText(WifScannerActivity.this, getString(R.string.setting_command_success), Toast.LENGTH_SHORT).show();
//                        } else {
//                            prefs.edit().putString("DEVICE_SSID", scans.get(gl_wifiConfigIndex).SSID).commit();
//                            prefs.edit().putString("DEVICE_PWD", password_input.getText().toString()).commit();
//                            prefs.edit().putInt("DEVICE_AUTHTYPE", 1).commit();
//                            gl_isConnectdevice = true;
//                            WifScannerActivity.this.setResult(Activity.RESULT_OK);
//                            finish();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private class ListAdapter extends BaseAdapter {
//
//        private List<ScanResult> scans;
//
//        private LayoutInflater inflater;
//
//        private int mIndex = -1;
//
//        int connectPosition = -1;
//
//        public ListAdapter(List<ScanResult> scans, Context context) {
//            this.scans = scans;
//            inflater = LayoutInflater.from(context);
//
//        }
//
//        public ListAdapter(List<ScanResult> scans, Context context, int index) {
//            this(scans, context);
//            mIndex = index;
//            connectPosition = -1;
//        }
//
//        @Override
//        public int getCount() {
//            return scans.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return scans.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        public void setConnectPosition(int position) {
//            connectPosition = position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ImgView imgView;
//            if (convertView == null) {
//                imgView = new ImgView();
//                convertView = inflater.inflate(R.layout.item_wifi_list, null);
//                imgView.tv = (TextView) convertView.findViewById(R.id.wifi_name);
//                imgView.iv = (ImageView) convertView.findViewById(R.id.is_connect);
//                convertView.setTag(imgView);
//            } else {
//                imgView = (ImgView) convertView.getTag();
//            }
//
//            if (connectPosition == position) {
//                imgView.iv.setVisibility(View.VISIBLE);
//            } else {
//                imgView.iv.setVisibility(View.INVISIBLE);
//            }
//            imgView.tv.setText(scans.get(position).SSID);
//            return convertView;
//        }
//
//        class ImgView {
//            TextView tv;
//            ImageView iv;
//        }
//    }
//
//    protected boolean isValidUID(String ssid) {
//        // if (ssid.length() == 0 || ssid.length() > 12) {
//        if (ssid.length() == 0) {
//            return false;
//        }
//        if (!ssid.startsWith("SMARP_")) {
//            return false;
//        }
//
//		/*
//         * for (int i = 6; i < ssid.length(); i++) { char c = ssid.charAt(i); if
//		 * (c < '0' || c > '9') { return false; } }
//		 */
//        return true;
//    }
//
//    public static int getAuth(String auth) {
//        if (auth.contains("Enterprise")) {
//            return 4;
//        } else if (auth.contains("EAP")) {
//            return 4;
//        } else if (auth.contains("WPA2")) {
//            return 3;
//        } else if (auth.contains("WPA")) {
//            return 2;
//        } else if (auth.contains("WEP")) {
//            return 1;
//        } else if (auth.equals("")) {
//            return 0;
//        } else if (auth.equals("[ESS]")) {
//            return 0;
//        }
//        return 5;
//    }
//
//    private void showCustomDialog(String title, String msg, boolean errorTag) {
//
//        new AlertDialog.Builder(this).setTitle(title)
//                .setMessage(msg)
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                })
//                .setPositiveButton("现在开启", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//                        if(!wm.isWifiEnabled())
//                        {
//                            wm.setWifiEnabled(true);
//                        }
//                    }
//                })
//                .show();
//    }
//
//    public void showWifiErrorDialog() {
//        if (pDialog.isShowing() && !isFinishing()) {
//            pDialog.dismiss();
//        }
//        new AlertDialog.Builder(this).setTitle(R.string.wifi_not_open)
//                .setMessage(R.string.please_open_wifi)
//                .setPositiveButton(android.R.string.ok, null)
//                .show();
//    }
//
//}
