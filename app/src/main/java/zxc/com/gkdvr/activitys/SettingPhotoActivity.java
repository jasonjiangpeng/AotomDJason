package zxc.com.gkdvr.activitys;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.SeekBar;

import zxc.com.gkdvr.Parser.ResultParser;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.utils.Constance;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.Net.NetCallBack;
import zxc.com.gkdvr.utils.Net.NetParamas;
import zxc.com.gkdvr.utils.Net.NetUtil;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/6/11.
 */
public class SettingPhotoActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_photo);
        setTitleText(getString(R.string.photo_setting));
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }

    private void initView() {
        findViewById(R.id.photo_Brightness).setOnClickListener(this);
        findViewById(R.id.photo_Contrast).setOnClickListener(this);
        findViewById(R.id.photo_frequency).setOnClickListener(this);
        findViewById(R.id.photo_Hue).setOnClickListener(this);
        findViewById(R.id.photo_Saturation).setOnClickListener(this);
        findViewById(R.id.photo_Sharpness).setOnClickListener(this);
        findViewById(R.id.VideoFormat).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_Brightness:
                choiceProgress = Tool.getFromSharePrefrence(this,"brightness");
                showSeekbarDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPhoto("brightness", choiceProgress + "");
                    }
                });
                break;
            case R.id.photo_Contrast:
                choiceProgress = Tool.getFromSharePrefrence(this,"contrast");
                showSeekbarDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPhoto("contrast", choiceProgress + "");
                    }
                });
                break;
            case R.id.photo_frequency:
                choisePosition = Tool.getFromSharePrefrence(this,"power_frequency");
                showSimpleChoiceDialog(getResources().getStringArray(R.array.frequency), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPhoto("power_frequency", choisePosition + "");
                    }
                });
                break;
            case R.id.photo_Hue:
                choiceProgress = Tool.getFromSharePrefrence(this,"hue");
                showSeekbarDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPhoto("hue", choiceProgress + "");
                    }
                });
                break;
            case R.id.photo_Saturation:
                choiceProgress = Tool.getFromSharePrefrence(this,"saturation");
                showSeekbarDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPhoto("saturation", choiceProgress + "");
                    }
                });
                break;
            case R.id.photo_Sharpness:
                choiceProgress = Tool.getFromSharePrefrence(this,"sharpness");
                showSeekbarDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPhoto("sharpness", choiceProgress + "");
                    }
                });
                break;
            case R.id.VideoFormat:
                choisePosition = Tool.getFromSharePrefrence(this,"videoformat");
                showSimpleChoiceDialog(getResources().getStringArray(R.array.VideoFormat), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPhoto("videoformat", choisePosition + "");
                    }
                });
                break;
        }
    }

    int choiceProgress;

    private void showSeekbarDialog(final DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(255);
        seekBar.setProgress(choiceProgress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                choiceProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        builder.setView(seekBar);
        builder.setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onClick(dialog, which);
            }
        });
        builder.create().show();
    }

    private void setPhoto(final String key,final String value) {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "setimage");
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
                                Tool.saveToSharePrefrence(SettingPhotoActivity.this,key,Integer.parseInt(value));
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
