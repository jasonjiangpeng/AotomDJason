package zxc.com.gkdvr.activitys;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.NumberPicker;

import okhttp3.Call;
import okhttp3.Response;
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
public class SettingRecordActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_record);
        initView();
    }

    private void initView() {
        setTitleText(getString(R.string.video_setting));
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.record_vol).setOnClickListener(this);
        findViewById(R.id.record_time).setOnClickListener(this);
        findViewById(R.id.record_mode).setOnClickListener(this);
        findViewById(R.id.record_resolution).setOnClickListener(this);
        findViewById(R.id.record_frames).setOnClickListener(this);
        findViewById(R.id.record_quality).setOnClickListener(this);
        findViewById(R.id.record_bit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_vol:
                choisePosition = Tool.getFromSharePrefrence(this, "volume");
                if(choisePosition==0)choisePosition=1;
                showVolDiaog();
                break;
            case R.id.record_time:
                choisePosition = Tool.getFromSharePrefrence(this, "rectime");
                showSimpleChoiceDialog(getResources().getStringArray(R.array.times), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setRecord("rectime", choisePosition + "");
                    }
                });
                break;
            case R.id.record_mode:
                choisePosition = Tool.getFromSharePrefrence(this, "recmode");
                showSimpleChoiceDialog(getResources().getStringArray(R.array.record_modes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setRecordMode();
                    }
                });
                break;
            case R.id.record_resolution:
                choisePosition = Tool.getFromSharePrefrence(this, "resolution");
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
        }
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
                                Tool.saveToSharePrefrence(SettingRecordActivity.this, "recmode", choisePosition);
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

    private void setAudio(final String value){
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
                                Tool.saveToSharePrefrence(SettingRecordActivity.this, "volume", Integer.parseInt(value));
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
                                Tool.saveToSharePrefrence(SettingRecordActivity.this, key, Integer.parseInt(value));
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
