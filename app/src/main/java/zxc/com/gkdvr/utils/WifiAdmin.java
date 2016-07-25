package zxc.com.gkdvr.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class WifiAdmin {
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private ConnectivityManager mConnectivityManager;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfiguration;
    WifiManager.WifiLock mWifiLock;
    private int NetID;
    Context ctx;
    String networkType = "";
    List<WifiConfiguration> mConfiguredNets;
    public static final int MAX_PRIORITY = 99999999;
    public static final int EN_TYPE_WEP = 1;
    public static final int EN_TYPE_WPA = 2;
    public static final int EN_TYPE_WPA2 = 3;
    public static final int EN_TYPE_EAP = 4;

    public WifiAdmin(Context context) {
        this.ctx = context;
        this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.mWifiInfo = this.mWifiManager.getConnectionInfo();
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public String getNetworkType() {
        return this.networkType;
    }

    public void setNetworkType(String type) {
        this.networkType = type;
    }

    public void openWifi() {
        if (!this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(true);
        }

    }

    public void closeWifi() {
        if (this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(false);
        }

    }

    public int checkState() {
        return this.mWifiManager.getWifiState();
    }

    public void acquireWifiLock() {
        this.mWifiLock.acquire();
    }

    public void releaseWifiLock() {
        if (this.mWifiLock.isHeld()) {
            this.mWifiLock.acquire();
        }

    }

    public void createWifiLock() {
        this.mWifiLock = this.mWifiManager.createWifiLock("Test");
    }

    public List<WifiConfiguration> getConfiguration() {
        return this.mWifiConfiguration;
    }

    public void connectConfiguration(int index) {
        if (index <= this.mWifiConfiguration.size()) {
            this.mWifiManager.enableNetwork(((WifiConfiguration) this.mWifiConfiguration.get(index)).networkId, true);
        }
    }

    public void startScan() {
        this.mWifiManager.startScan();
        this.mWifiList = this.mWifiManager.getScanResults();
        this.mWifiConfiguration = this.mWifiManager.getConfiguredNetworks();
    }

    public List<ScanResult> getWifiList() {
        return this.mWifiList;
    }

    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < this.mWifiList.size(); ++i) {
            stringBuilder.append("Index_" + (new Integer(i + 1)).toString() + ":");
            stringBuilder.append(((ScanResult) this.mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }

        return stringBuilder;
    }

    public String getSSID() {
        return this.mWifiInfo == null ? "" : this.mWifiInfo.getSSID();
    }

    public String getMacAddress() {
        return this.mWifiInfo == null ? "NULL" : this.mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return this.mWifiInfo == null ? "NULL" : this.mWifiInfo.getBSSID();
    }

    public int getIPAddress() {
        return this.mWifiInfo == null ? 0 : this.mWifiInfo.getIpAddress();
    }

    public int getNetworkId() {
        return this.mWifiInfo == null ? 0 : this.mWifiInfo.getNetworkId();
    }

    public String getWifiInfo() {
        return this.mWifiInfo == null ? "NULL" : this.mWifiInfo.toString();
    }

    public int getNetID() {
        return this.NetID;
    }

    public boolean reconnect() {
        this.mWifiManager.disconnect();
        boolean b = true;
        return b;
    }

    public boolean isEqualSSID(String StrSrcSSID, String StrTarSSID) {
        return StrSrcSSID != null && StrTarSSID != null ? StrSrcSSID.replace("\"", "").equals(StrTarSSID.replace("\"", "")) : false;
    }

    public boolean isConnected(String tarSSID) {
        NetworkInfo mWifi = this.mConnectivityManager.getNetworkInfo(1);
        WifiInfo currentWifi = this.mWifiManager.getConnectionInfo();
        String curSSID = currentWifi.getSSID();
        return mWifi.isConnected() && curSSID != null && this.isEqualSSID(curSSID, tarSSID);
    }

    public boolean addNetworkNoDisableOther(WifiConfiguration wcg) {
        this.NetID = this.mWifiManager.addNetwork(wcg);
        this.mWifiManager.enableNetwork(this.NetID, false);
        this.mWifiManager.saveConfiguration();
        boolean b = this.mWifiManager.reconnect();
        return b;
    }

    public int getEncrptionType(String auth) {
        return !auth.contains("Enterprise") && !auth.contains("EAP") ? (auth.contains("WPA2") ? 3 : (auth.contains("WPA") ? 2 : (auth.contains("WEP") ? 1 : (auth.equals("") ? 0 : (auth.equals("[ESS]") ? 0 : 5))))) : 4;
    }

    public boolean addNetwork(WifiConfiguration wcg) {
        boolean b = false;
        this.mWifiManager.disconnect();
        this.NetID = this.mWifiManager.addNetwork(wcg);
        b = this.mWifiManager.enableNetwork(this.NetID, true);
        this.mWifiManager.saveConfiguration();
        this.mWifiManager.reconnect();
        return b;
    }

    public boolean enableNetwork(String SSID) {
        boolean state = false;
        WifiManager wm = (WifiManager) this.ctx.getSystemService(Context.WIFI_SERVICE);
        this.mWifiManager.disconnect();
        if (wm.setWifiEnabled(true)) {
            List networks = wm.getConfiguredNetworks();
            Iterator iterator = networks.iterator();

            while (iterator.hasNext()) {
                WifiConfiguration wifiConfig = (WifiConfiguration) iterator.next();
                if (this.isEqualSSID(wifiConfig.SSID, SSID)) {
                    state = wm.enableNetwork(wifiConfig.networkId, true);
                    break;
                }
            }

            wm.reconnect();
        }

        return state;
    }

    public void enableAllnetwork() {
        try {
            WifiManager e = (WifiManager) this.ctx.getSystemService(Context.WIFI_SERVICE);
            if (e.setWifiEnabled(true)) {
                List networks = e.getConfiguredNetworks();
                Iterator iterator = networks.iterator();

                while (iterator.hasNext()) {
                    WifiConfiguration wifiConfig = (WifiConfiguration) iterator.next();
                    e.enableNetwork(wifiConfig.networkId, false);
                }
            }
        } catch (Exception var5) {
            Log.e("ENABLE NETWORK FAIL", "Exception : " + var5.toString());
        }

    }

    public boolean disableNework(String SSID) {
        boolean state = false;
        WifiManager wm = (WifiManager) this.ctx.getSystemService(Context.WIFI_SERVICE);
        if (wm.setWifiEnabled(true)) {
            List networks = wm.getConfiguredNetworks();
            Iterator iterator = networks.iterator();

            while (iterator.hasNext()) {
                WifiConfiguration wifiConfig = (WifiConfiguration) iterator.next();
                if (this.isEqualSSID(wifiConfig.SSID, SSID)) {
                    state = wm.disableNetwork(wifiConfig.networkId);
                    wm.saveConfiguration();
                } else {
                    wm.enableNetwork(wifiConfig.networkId, true);
                }
            }

            wm.reconnect();
        }

        return state;
    }

    boolean disableOtherNework(String ssid) {
        boolean state = false;
        WifiManager wm = (WifiManager) this.ctx.getSystemService(Context.WIFI_SERVICE);
        if (wm.setWifiEnabled(true)) {
            List networks = wm.getConfiguredNetworks();
            Iterator iterator = networks.iterator();

            while (iterator.hasNext()) {
                WifiConfiguration wifiConfig = (WifiConfiguration) iterator.next();
                if (this.isEqualSSID(wifiConfig.SSID, ssid)) {
                    state = wm.enableNetwork(wifiConfig.networkId, true);
                    wm.saveConfiguration();
                } else {
                    wm.disableNetwork(wifiConfig.networkId);
                }
            }
        }

        return state;
    }

    public boolean checkNet() {
        NetworkInfo.State mNetworkState = NetworkInfo.State.UNKNOWN;
        return mNetworkState == NetworkInfo.State.CONNECTED;
    }

    public void disconnectWifi() {
        this.mWifiManager.disconnect();
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        if (Build.VERSION.SDK_INT >= 21) {
            config.SSID = SSID.replace("\"", "");
        } else {
            config.SSID = "\"" + SSID.replace("\"", "") + "\"";
        }

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            this.mWifiManager.removeNetwork(tempConfig.networkId);
        }

        switch (Type) {
            case 0:
                config.allowedKeyManagement.set(0);
                config.wepTxKeyIndex = 0;
                break;
            case 1:
                config.allowedKeyManagement.set(0);
                config.allowedProtocols.set(1);
                config.allowedProtocols.set(0);
                config.allowedAuthAlgorithms.set(0);
                config.allowedAuthAlgorithms.set(1);
                config.allowedPairwiseCiphers.set(2);
                config.allowedPairwiseCiphers.set(1);
                config.allowedGroupCiphers.set(0);
                config.allowedGroupCiphers.set(1);
                if (getHexKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }

                config.wepTxKeyIndex = 0;
                break;
            case 2:
                config.preSharedKey = "\"" + Password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(0);
                config.allowedGroupCiphers.set(2);
                config.allowedKeyManagement.set(1);
                config.allowedPairwiseCiphers.set(1);
                config.allowedGroupCiphers.set(3);
                config.allowedPairwiseCiphers.set(2);
                config.status = 2;
                break;
            case 3:
                config.preSharedKey = "\"" + Password + "\"";
                config.hiddenSSID = true;
                config.status = 2;
                config.allowedGroupCiphers.set(2);
                config.allowedGroupCiphers.set(3);
                config.allowedKeyManagement.set(1);
                config.allowedPairwiseCiphers.set(1);
                config.allowedPairwiseCiphers.set(2);
                config.allowedProtocols.set(1);
        }

        return config;
    }

    private static boolean getHexKey(String s) {
        if (s == null) {
            return false;
        } else {
            int len = s.length();
            if (len != 10 && len != 26 && len != 58) {
                return false;
            } else {
                for (int i = 0; i < len; ++i) {
                    char c = s.charAt(i);
                    if ((c < 48 || c > 57) && (c < 97 || c > 102) && (c < 65 || c > 70)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public WifiConfiguration IsExsits(String SSID) {
        List existingConfigs = this.mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            Iterator var4 = existingConfigs.iterator();
            while (var4.hasNext()) {
                WifiConfiguration existingConfig = (WifiConfiguration) var4.next();
                if (this.isEqualSSID(existingConfig.SSID, SSID)) {
                    return existingConfig;
                }
            }
        }

        return null;
    }

    public int GetNetWorkID(String SSID) {
        List existingConfigs = this.mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null) {
            Iterator var4 = existingConfigs.iterator();
            while (var4.hasNext()) {
                WifiConfiguration existingConfig = (WifiConfiguration) var4.next();
                if (this.isEqualSSID(existingConfig.SSID, SSID)) {
                    return existingConfig.networkId;
                }
            }
        }

        return -1;
    }

    public boolean UpgradeNetworkPriority(String SSID) {
        boolean state = false;
        List list = this.mWifiManager.getConfiguredNetworks();
        if (list != null && list.size() > 0) {
            Iterator var5 = list.iterator();

            while (var5.hasNext()) {
                WifiConfiguration CurrConfig = (WifiConfiguration) var5.next();
                if (CurrConfig.SSID != null && CurrConfig.SSID.equals(convertToQuotedString(SSID))) {
                    Log.d("robert", "SSID:" + CurrConfig.SSID + "Priority:" + CurrConfig.priority);
                    int newPri = this.getMaxPriority(SSID);
                    if (newPri >= 99999999) {
                        newPri = this.shiftPriorityAndSave();
                    }

                    CurrConfig.priority = newPri + 1;
                    this.mWifiManager.updateNetwork(CurrConfig);
                    state = this.mWifiManager.enableNetwork(CurrConfig.networkId, true);
                    this.mWifiManager.saveConfiguration();
                    break;
                }
            }
        }

        return state;
    }

    public boolean DowngradeNetworkPriority(String SSID) {
        boolean state = false;
        List list = this.mWifiManager.getConfiguredNetworks();
        if (list != null && list.size() > 0) {
            Iterator var5 = list.iterator();

            while (var5.hasNext()) {
                WifiConfiguration CurrConfig = (WifiConfiguration) var5.next();
                if (CurrConfig.SSID != null && CurrConfig.SSID.equals(convertToQuotedString(SSID))) {
                    int newPri = this.getMaxPriority(SSID);
                    if (newPri >= 99999999) {
                        newPri = this.shiftPriorityAndSave();
                    }

                    CurrConfig.priority = newPri - 3;
                    if (CurrConfig.priority < 0) {
                        CurrConfig.priority = 0;
                    }

                    this.mWifiManager.updateNetwork(CurrConfig);
                    this.mWifiManager.saveConfiguration();
                    break;
                }
            }
        }

        return state;
    }

    private WifiConfiguration getMaxPriorityConfig() {
        List configurations = this.mWifiManager.getConfiguredNetworks();
        int pri = 0;
        int MaxIndex = 0;
        int i = 0;

        for (Iterator var6 = configurations.iterator(); var6.hasNext(); ++i) {
            WifiConfiguration config = (WifiConfiguration) var6.next();
            if (config.priority > pri) {
                pri = config.priority;
                MaxIndex = i;
            }
        }

        return (WifiConfiguration) configurations.get(MaxIndex);
    }

    private int getMaxPriority(String SSID) {
        List configurations = this.mWifiManager.getConfiguredNetworks();
        int pri = 0;
        int MaxIndex = 0;
        int OurIndex = -1;
        int i = 0;

        for (Iterator var8 = configurations.iterator(); var8.hasNext(); ++i) {
            WifiConfiguration config = (WifiConfiguration) var8.next();
            if (this.isEqualSSID(config.SSID, SSID)) {
                OurIndex = i;
            } else if (config.priority > pri) {
                pri = config.priority;
                MaxIndex = i;
            }
        }

        Log.d("robert", "SSID:" + ((WifiConfiguration) configurations.get(MaxIndex)).SSID + "Max Priority:" + ((WifiConfiguration) configurations.get(MaxIndex)).priority);
        if (OurIndex >= 0) {
            Log.d("robert", "SSID:" + ((WifiConfiguration) configurations.get(OurIndex)).SSID + "Priority:" + ((WifiConfiguration) configurations.get(OurIndex)).priority);
        }

        return pri;
    }

    private void sortByPriority(List<WifiConfiguration> configurations) {
        Collections.sort(configurations, new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                return ((WifiConfiguration) lhs).priority - ((WifiConfiguration) rhs).priority;
            }

        });
    }

    private int shiftPriorityAndSave() {
        List configurations = this.mWifiManager.getConfiguredNetworks();
        this.sortByPriority(configurations);
        int size = configurations.size();

        for (int i = 0; i < size; ++i) {
            WifiConfiguration config = (WifiConfiguration) configurations.get(i);
            config.priority = i;
            this.mWifiManager.updateNetwork(config);
        }

        this.mWifiManager.saveConfiguration();
        return size;
    }

    public static String convertToQuotedString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        } else {
            int lastPos = string.length() - 1;
            return lastPos > 0 && string.charAt(0) == 34 && string.charAt(lastPos) == 34 ? string : "\"" + string + "\"";
        }
    }

    public WifiInfo getmWifiInfo() {
        return this.mWifiInfo;
    }

    public void setmWifiInfo(WifiInfo mWifiInfo) {
        this.mWifiInfo = mWifiInfo;
    }

    public boolean pingSupplicant() {
        return this.mWifiManager.pingSupplicant();
    }
}