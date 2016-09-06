package zxc.com.gkdvr.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.Parser.ResultParser;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.utils.Constance;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.Net.NetCallBack;
import zxc.com.gkdvr.utils.Net.NetParamas;
import zxc.com.gkdvr.utils.Net.NetUtil;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/6/10.
 */
public class SettingDeviceActivity extends BaseActivity implements View.OnClickListener {
    private NetParamas paramas;
    private TextView version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_device);
        initView();
        if (!isWifiConnectedToDVR()) {
            showConnectingDialog();
            return;
        }
        getVersion();
    }

    private void initView() {
        setTitleText(getString(R.string.device_setting));
        version = (TextView) findViewById(R.id.version);
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.device_reset).setOnClickListener(this);
        findViewById(R.id.about).setOnClickListener(this);
        findViewById(R.id.sdcard_format).setOnClickListener(this);
    }

    private void formatSD() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "system");
        paramas.put("action", "Formattf");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                Tool.showToast(getString(R.string.format_sd_success));
                            } else Tool.showToast(getString(R.string.format_sd_fail));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Tool.removeProgressDialog();
                        }
                    }
                });
            }
        }, getString(R.string.Submiting), true);
    }

    @Override
    public void onClick(View v) {
        if (!isWifiConnectedToDVR()) {
            showConnectingDialog();
            return;
        }
        switch (v.getId()) {
            case R.id.device_reset:
                AlertDialog a1 = new AlertDialog.Builder(this).setMessage(getString(R.string.alert_reset)).setNegativeButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                restore();
                            }
                        }).show();
                Tool.changeDialogText(a1);
                break;
            case R.id.about:
                //getVersion();
                break;
            case R.id.sdcard_format:
                AlertDialog a2 = new AlertDialog.Builder(SettingDeviceActivity.this).setMessage(getString(R.string.alert_format_SD)).setNegativeButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                formatSD();
                            }
                        }).show();
                Tool.changeDialogText(a2);
                break;
        }
    }

    private void getVersion() {
        paramas = new NetParamas();
        paramas.put("type", "system");
        paramas.put("action", "getversion");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                MyApplication.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                String v = ResultParser.parseVersion(result);
//                                Tool.showToast(getString(R.string.version) + v.substring(0, v.length() - 1));
                                version.setText(v.substring(0, v.length() - 1));
                            } else {
                                Tool.showToast(s);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Tool.removeProgressDialog();
                        }
                    }
                });
            }
        }, getString(R.string.loading), true);
    }


    private void syncTime() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "system");
        paramas.put("action", "setdatetime");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        MyLogger.i(dateFormat.format(new Date(System.currentTimeMillis())));
        paramas.put("", dateFormat.format(new Date(System.currentTimeMillis())));
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            @Override
            public void onResponse(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyLogger.i(result);
                        Tool.removeProgressDialog();
                        if (result.contains("OK"))
                            Tool.showToast(getString(R.string.set_time_success));
                        else Tool.showToast(getString(R.string.set_time_fail));
                    }
                });
            }
        }, getString(R.string.Submiting), true);
    }

    private void restore() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "system");
        paramas.put("action", "Restore");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                MyApplication.getCurrentActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MyLogger.i(result);
                            s = ResultParser.parse(result);
                            if (result.contains("OK")) {
                                Tool.showToast(getString(R.string.restore_suceess));
                                Tool.showToast(getString(R.string.device_restoring));
                            } else Tool.showToast(getString(R.string.restore_fail));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Tool.removeProgressDialog();
                        }
                    }
                });
            }
        }, getString(R.string.loading), true);
    }


}
