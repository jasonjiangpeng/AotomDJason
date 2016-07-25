package zxc.com.gkdvr.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.kyleduo.switchbutton.SwitchButton;

import okhttp3.Call;
import okhttp3.Response;
import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.Parser.RecStatusParser;
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
public class SettingRecordActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private SwitchButton vol;
    private int choose;
    private String Ispmode = "0";
    private String Brightness = "0";
    private String Contrast = "0";
    private String Hue = "0";
    private String Saturation = "0";
    private String Sharpness = "0";
    private String Power_frequency = "0";
    private String VideoFormat = "0";
    private String Image_mirror = "0";
    private String Resolution = "0";
    private String Frames = "0";
    private String Quality = "0";
    private String Bitrate = "0";
    private String Profile = "0";
    private String BCR = "0";
    private String Rectime = "0";
    private String Volume = "0";
    private String Mute = "0";
    private String RecMute = "0";
    private String Gsensordpi = "0";
    private String RecMode = "0";
    private boolean isLoaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_record);
        initView();
        if (!isWifiConnectedToDVR()) {
            showConnectingDialog();
            return;
        }
        Tool.showProgressDialog(getString(R.string.loading), false, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLoaded)
            initdata();
    }

    private void initdata() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "getimage");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultParser parser = new ResultParser();
                            if (result.contains("OK")) {
                                Ispmode = parser.parseByKey(result, "Ispmode");
                                Brightness = parser.parseByKey(result, "Brightness");
                                Contrast = parser.parseByKey(result, "Contrast");
                                Hue = parser.parseByKey(result, "Hue");
                                Saturation = parser.parseByKey(result, "Saturation");
                                Sharpness = parser.parseByKey(result, "Sharpness");
                                Power_frequency = parser.parseByKey(result, "Power_frequency");
                                VideoFormat = parser.parseByKey(result, "VideoFormat");
                                Image_mirror = parser.parseByKey(result, "Image_mirror");
                                getRecord();
                            } else {
                                Tool.showToast(parser.parse(result));
                                Tool.removeProgressDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void getRecord() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "getrecord");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            ResultParser parser = new ResultParser();
                            if (result.contains("OK")) {
                                Resolution = parser.parseByKey(result, "Resolution");
                                Frames = parser.parseByKey(result, "Frames");
                                Quality = parser.parseByKey(result, "Quality");
                                Bitrate = parser.parseByKey(result, "Bitrate");
                                Profile = parser.parseByKey(result, "Profile");
                                BCR = parser.parseByKey(result, "BCR");
                                Rectime = parser.parseByKey(result, "Rectime");
                                getAudio();
                            } else {
                                Tool.showToast(parser.parse(result));
                                Tool.removeProgressDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void getAudio() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "getaudio");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            ResultParser parser = new ResultParser();
                            if (result.contains("OK")) {
                                Volume = parser.parseByKey(result, "Volume");
                                Mute = parser.parseByKey(result, "Mute");
                                RecMute = parser.parseByKey(result, "RecMute");
                                vol.setChecked(RecMute.equals("0"));
                                vol.setOnCheckedChangeListener(SettingRecordActivity.this);
                                getGsensor();
                            } else {
                                Tool.showToast(parser.parse(result));
                                Tool.removeProgressDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void getGsensor() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "getgsensor");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultParser parser = new ResultParser();
                            if (result.contains("OK")) {
                                Gsensordpi = parser.parseByKey(result, "Gsensordpi");
                                getRecmode();
                            } else {
                                Tool.showToast(parser.parse(result));
                                Tool.removeProgressDialog();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void getRecmode() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "system");
        paramas.put("action", "getrecmode");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            @Override
            public void onResponse(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyLogger.i(result);
                        try {
                            ResultParser parser = new ResultParser();
                            if (result.contains("OK")) {
                                RecMode = parser.parseByKey(result, "RecMode");
                                isLoaded = true;
                            } else {
                                Tool.showToast(parser.parse(result));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Tool.removeProgressDialog();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        setTitleText(getString(R.string.video_setting));
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.record_time).setOnClickListener(this);
        findViewById(R.id.record_mode).setOnClickListener(this);
        findViewById(R.id.record_resolution).setOnClickListener(this);
        findViewById(R.id.record_frames).setOnClickListener(this);
        findViewById(R.id.record_quality).setOnClickListener(this);
        findViewById(R.id.record_bit).setOnClickListener(this);
        findViewById(R.id.device_gsensor).setOnClickListener(this);
        findViewById(R.id.mirror).setOnClickListener(this);
        vol = (SwitchButton) findViewById(R.id.idNeedVol);
    }

    @Override
    public void onClick(View v) {
        if (!isWifiConnectedToDVR()) {
            showConnectingDialog();
            return;
        }
        switch (v.getId()) {
            case R.id.record_time:
                choisePosition = Integer.valueOf(Rectime);
                showSimpleChoiceDialog(getResources().getStringArray(R.array.times), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setRecord("rectime", choisePosition + "");
                    }
                });
                break;
            case R.id.record_mode:
                choisePosition = Integer.valueOf(RecMode);
                showSimpleChoiceDialog(getResources().getStringArray(R.array.record_modes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RecMode = choisePosition + "";
                        setRecordMode();
                    }
                });
                break;
            case R.id.record_resolution:
                choisePosition = Integer.valueOf(Resolution);
                showSimpleChoiceDialog(getResources().getStringArray(R.array.Resolution), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setRecord("resolution", choisePosition + "");
                    }
                });
                break;
            case R.id.record_frames:
                choisePosition = Tool.getFromSharePrefrence(this, "frames");
                showSimpleChoiceDialog(getResources().getStringArray(R.array.Frames), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setRecord("frames", choisePosition + "");
                    }
                });
                break;
            case R.id.record_quality:
                choisePosition = Tool.getFromSharePrefrence(this, "quality");
                showSimpleChoiceDialog(getResources().getStringArray(R.array.Quality), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setRecord("quality", choisePosition + "");
                    }
                });
                break;
            case R.id.record_bit:
                choisePosition = Tool.getFromSharePrefrence(this, "bitrate") - 2;
                if (choisePosition < 0) choisePosition = 0;
                showSimpleChoiceDialog(getResources().getStringArray(R.array.Bitrate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setRecord("bitrate", choisePosition + 2 + "");
                    }
                });
                break;
            case R.id.device_gsensor:
                showGsensorDialog();
                break;
            case R.id.mirror:
                choisePosition = Integer.valueOf(Image_mirror);
                showSimpleChoiceDialog(getResources().getStringArray(R.array.mirror), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPhoto(choisePosition + "");
                    }
                });
                break;
        }
    }

    private void setPhoto(final String value) {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "setimage");
        paramas.put("ispmode", Ispmode);
        paramas.put("brightness", Brightness);
        paramas.put("contrast", Contrast);
        paramas.put("hue", Hue);
        paramas.put("saturation", Saturation);
        paramas.put("sharpness", Sharpness);
        paramas.put("power_frequency", Power_frequency);
        paramas.put("videoformat", VideoFormat);
        paramas.put("image_mirror", value);
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                Tool.showToast(getString(R.string.setting_success));
                                Image_mirror = value;
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

    private void showGsensorDialog() {
        choose = Integer.valueOf(Gsensordpi);
        new AlertDialog.Builder(this).setSingleChoiceItems(getResources().getStringArray(R.array.Gsensors), choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choose = which;
                Gsensordpi = choose + "";
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
                                Gsensordpi = choose + "";
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


    private void setRecordMode() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "system");
        paramas.put("action", "setrecmode");
        paramas.put("recmode", choisePosition + "");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                Tool.showToast(getString(R.string.setting_success));
                                RecMode = choisePosition + "";
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


    private void showVolDiaog() {
        View view = View.inflate(this, R.layout.dialog_num_picker, null);
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.num);
        numberPicker.setMaxValue(13);
        numberPicker.setMinValue(1);
        numberPicker.setValue(choisePosition);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setAudio(numberPicker.getValue() + "");
            }
        }).setNegativeButton(getString(R.string.cancel), null);
        builder.create().show();
    }

    private void setAudio(final String value) {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "setaudio");
        paramas.put("volume", value);
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                Tool.showToast(getString(R.string.setting_success));
                                MainActivity.recMute = Integer.valueOf(value);
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

    private void setRecord(final String key, final String value) {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "setrecord");
        paramas.put(key, value);
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                Tool.showToast(getString(R.string.setting_success));
                                switch (key) {
                                    case "rectime":
                                        Rectime = choisePosition + "";
                                        MainActivity.recTime = Integer.valueOf(Rectime);
                                        break;
                                    case "resolution":
                                        Resolution = choisePosition + "";
                                        break;
                                }
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isWifiConnectedToDVR()) {
            Tool.showToast(getString(R.string.no_device));
            return;
        }
        setMute(isChecked ? 0 : 1);
    }

    private void setMute(final int state) {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "setaudio");
        paramas.put("recmute", String.valueOf(state));
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s;

            @Override
            public void onResponse(final String result) {
                MyLogger.i(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                Tool.showToast(getString(R.string.setting_success));
                                RecMute = String.valueOf(state);
                                Tool.saveToSharePrefrence(SettingRecordActivity.this, "recmute", state);
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
}
