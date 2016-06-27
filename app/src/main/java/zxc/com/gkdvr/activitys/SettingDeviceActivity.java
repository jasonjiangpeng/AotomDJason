package zxc.com.gkdvr.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.NumberPicker;

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
    private int choose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_device);
        initView();
    }

    private void initView() {
        setTitleText(getString(R.string.device_setting));
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.device_reset).setOnClickListener(this);
        findViewById(R.id.device_time).setOnClickListener(this);
        findViewById(R.id.device_gsensor).setOnClickListener(this);
        findViewById(R.id.about).setOnClickListener(this);
        findViewById(R.id.sdcard_format).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingDeviceActivity.this).setMessage(getString(R.string.alert_format_SD)).setNegativeButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                formatSD();
                            }
                        }).create().show();

            }
        });
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
        switch (v.getId()) {
            case R.id.device_reset:
                new AlertDialog.Builder(this).setMessage(getString(R.string.alert_reset)).setNegativeButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                restore();
                            }
                        }).create().show();
                break;
            case R.id.device_time:
                syncTime();
                break;
            case R.id.device_gsensor:
                showGsensorDialog();
                break;
            case R.id.about:
                startActivity(new Intent(this,VersionInfoActivity.class));
                break;
        }
    }

    private void showGsensorDialog() {
        choose = Tool.getFromSharePrefrence(this,"gsensordpi");
        new AlertDialog.Builder(this).setSingleChoiceItems(getResources().getStringArray(R.array.Gsensors), choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choose = which;
            }
        }).setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setGsensor();
                    }
                }).create().show();
    }

    private void setGsensor() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "setgsensor");
        paramas.put("gsensordpi", choose + "");
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
                                Tool.showToast(getString(R.string.setting_success));
                                Tool.saveToSharePrefrence(SettingDeviceActivity.this,"gsensordpi",choose);
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
        }, getString(R.string.Submiting), true);
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
                        if (result.contains("OK")) Tool.showToast(getString(R.string.set_time_success));
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
