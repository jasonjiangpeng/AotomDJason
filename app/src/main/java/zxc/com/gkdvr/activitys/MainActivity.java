package zxc.com.gkdvr.activitys;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.libs.ffmpeg.FFmpegPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.Parser.ResultParser;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.fragments.PhotoFragment;
import zxc.com.gkdvr.fragments.SettingsFragment;
import zxc.com.gkdvr.receiver.NetworkConnectChangedReceiver;
import zxc.com.gkdvr.utils.Constance;
import zxc.com.gkdvr.utils.FileAccessor;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.Net.NetCallBack;
import zxc.com.gkdvr.utils.Net.NetParamas;
import zxc.com.gkdvr.utils.Net.NetUtil;
import zxc.com.gkdvr.utils.PermissionUtil;
import zxc.com.gkdvr.utils.SpinnerUtil;
import zxc.com.gkdvr.utils.Tool;
import zxc.com.gkdvr.utils.UIUtil;
import zxc.com.gkdvr.utils.WifiAdmin;

public class MainActivity extends BaseActivity implements View.OnClickListener, FFmpegPlayer.OnVideoLostLinkListner, NetworkConnectChangedReceiver.OnNetChangeListener {
    private LinearLayout lastTab = null;
    private RelativeLayout rlDuallaoyut;//视频画面
    private RelativeLayout rlBottomLayout;//底部主页面布局
    private RelativeLayout titleLayout;//顶部标题栏
    private RelativeLayout rootView;//顶部标题栏
    private FFmpegPlayer fFmpegPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageView iv_main_takepic;
    private ImageView iv_main_record;
    private ImageView iv_main_remote_pic_main;
    private ImageView iv_main_takepic2;
    private ImageView iv_main_record2;
    private ImageView iv_main_remote_pic_main2;
    private ImageView ivBackground;
    private ImageView ivVol;
    private View llVideoCamera;
    private View llImage;
    private View llSetting;
    private int tabImgs[][] = {{R.mipmap.ic_img_a, R.mipmap.photos_off},
            {R.mipmap.ic_video_a, R.mipmap.ic_video_b},
            {R.mipmap.ic_setting_a, R.mipmap.ic_setting_b}};
    private RelativeLayout vVideoControl;
    //    private VerticalSeekBar volSeekbar;FF
    private TextView tvTip;
    private TextView title;
    private boolean isRtsp = false;
    private LinearLayout record_time;
    private boolean isSurfaceCreated = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtil.init(this, 480, 800);
        setContentView(R.layout.activity_preview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        initView();
        if (!isWifiConnectedToDVR()) {
            showConnectingDialog();
        }
        onClick(llVideoCamera);
        bindEvent();
        new PermissionUtil().askforPermission(Manifest.permission_group.STORAGE);
        FileAccessor.initFileAccess();
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.dual);
        surfaceView.setZOrderOnTop(false);//设置画布  背景透明
        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        findViewById(R.id.title_left).setVisibility(View.GONE);
        fFmpegPlayer = new FFmpegPlayer(this);
        tvTip = (TextView) findViewById(R.id.tvTip);
        title = (TextView) findViewById(R.id.title_tv);
//        title.setText(getString(R.string.app_name));
        iv_main_takepic = (ImageView) findViewById(R.id.iv_main_takepic);
        ivBackground = (ImageView) findViewById(R.id.ivBackground);
        iv_main_record = (ImageView) findViewById(R.id.iv_main_record);
        iv_main_remote_pic_main = (ImageView) findViewById(R.id.iv_main_remote_pic_main);
        llVideoCamera = findViewById(R.id.llVideoCamera);
        llImage = findViewById(R.id.llImage);
        llSetting = findViewById(R.id.llSetting);
        rlDuallaoyut = (RelativeLayout) findViewById(R.id.rlDuallaoyut);
        rlBottomLayout = (RelativeLayout) findViewById(R.id.rlBottomLayout);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        findViewById(R.id.btn_full).setOnClickListener(this);
        findViewById(R.id.btn_dual).setOnClickListener(this);
        titleLayout = (RelativeLayout) findViewById(R.id.preview_title);
        record_time = (LinearLayout) findViewById(R.id.record_time);
        vVideoControl = (RelativeLayout) getLayoutInflater().inflate(R.layout.view_video_control, null, false);
        ivVol = (ImageView) vVideoControl.findViewById(R.id.ivVol);
        ivVol.setOnClickListener(this);
        vVideoControl.findViewById(R.id.iv_main_back_por).setOnClickListener(this);
        iv_main_takepic2 = (ImageView) vVideoControl.findViewById(R.id.iv_main_takepic2);
        iv_main_record2 = (ImageView) vVideoControl.findViewById(R.id.iv_main_record2);
        iv_main_remote_pic_main2 = (ImageView) vVideoControl.findViewById(R.id.iv_main_remote_pic_main2);
        vVideoControl.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
        rlDuallaoyut.addView(vVideoControl);
        vVideoControl.setVisibility(View.GONE);
        rlDuallaoyut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (isWifiConnectedToDVR()) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        showConnectingDialog();
                    }
                } else {
                    if (vVideoControl.getVisibility() == View.VISIBLE) {
                        vVideoControl.setVisibility(View.GONE);
                    } else {
                        vVideoControl.setVisibility(View.VISIBLE);

                    }
                }
            }
        });
    }

    private void showConnectingDialog() {
        new android.support.v7.app.AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.notice))
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


    private void showVideo() {
        if (!isSurfaceCreated) {
            MyLogger.e("isRtsp------------>>" + isRtsp);
            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    MyLogger.e("isShowRtsp------------>>" + isShowRtsp);
                    surfaceHolder = holder;
                    isSurfaceCreated = true;
                    if (isShowRtsp)
                        setMedia();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                }
            });
        }


    }

    private Timer timeOut;

    private void setMedia() {
        if (!isWifiConnectedToDVR()) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Tool.showProgressDialog(getString(R.string.loading), false, MainActivity.this);
            }
        });
        timeOut = new Timer();
        timeOut.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Tool.removeProgressDialog();
                        Tool.showToast(getString(R.string.load_video_fail));
                    }
                });

            }
        }, 10000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MyLogger.e("setMediaUri" + System.currentTimeMillis());
                int initResult = fFmpegPlayer.setMediaUri(Constance.RTSP_URL);
                mHandler.sendEmptyMessage(initResult);
            }
        }).start();

    }

//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            int what = msg.what;
//            MyLogger.e("what----->>" + what);
//            switch (what) {
//                case 0:
//                    MyLogger.e("setSurface" + System.currentTimeMillis());
//                    fFmpegPlayer.setSurface(surfaceHolder.getSurface(), 0, 0);
//                    MyLogger.e("setSurface  end" + System.currentTimeMillis());
//                    isRtsp = true;
//                    Tool.removeProgressDialog();
//                    break;
//                default:
//                    Tool.showToast(getString(R.string.load_video_fail));
//                    Tool.removeProgressDialog();
//                    break;
//            }
//        }
//    };

    @Override
    protected void onResume() {
        super.onResume();
        if (isWifiConnectedToDVR()) {
            ivBackground.setVisibility(View.GONE);
            syncTime();
            if (isShowRtsp)
                showVideo();
        } else {
            ivBackground.setVisibility(View.VISIBLE);
        }
    }

    protected boolean isWifiConnectedToDVR() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gl_wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!wifiManager.isWifiEnabled()) {
            return false;
        }
        String device_ssid = wifiManager.getConnectionInfo().getSSID().toString().replace("\"", "");
        if (gl_wifiInfo.isConnected()) {
            if (device_ssid.contains("DVR_")) {
                NetworkConnectChangedReceiver.wifiName = device_ssid;
                return true;
            }
        }
        return false;
    }

    private void bindEvent() {
        llVideoCamera.setOnClickListener(this);
        llImage.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        iv_main_takepic.setOnClickListener(this);
        iv_main_record.setOnClickListener(this);
        iv_main_remote_pic_main.setOnClickListener(this);
        iv_main_takepic2.setOnClickListener(this);
        iv_main_record2.setOnClickListener(this);
        iv_main_remote_pic_main2.setOnClickListener(this);
        NetworkConnectChangedReceiver.mListeners.add(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_main_takepic:
            case R.id.iv_main_takepic2:
                performClickTakePic();
                return;
            case R.id.ivBack:
            case R.id.iv_main_back_por:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return;
            case R.id.iv_main_record:
            case R.id.iv_main_record2:
                performClickRecored();
                return;
            case R.id.iv_main_remote_pic_main:
            case R.id.iv_main_remote_pic_main2:
                if (!isWifiConnectedToDVR()) {
                    Tool.showToast(getString(R.string.connect_not_ready));
                    return;
                }
//                if(isRecording){
//                    Tool.showToast(getString(R.string.pls_stop_recording));
//                    return;
//                }
                startActivity(new Intent(this, RemoteFileActivity.class));
                return;
            case R.id.btn_full:
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                return;
            case R.id.btn_dual:
                return;
            case R.id.ivVol:
                setMute();
                return;
        }
        if (lastTab != null) {
            setTabSelectColorChange(lastTab, false);
        }
        switch (v.getId()) {
            case R.id.llImage:
                title.setText(getString(R.string.photo));
                setTabSelected(0);
                v.setTag(0);
                setTabSelectColorChange((LinearLayout) v, true);
                break;
            case R.id.llVideoCamera:
                title.setText(getString(R.string.camera));
                setTabSelected(1);
                v.setTag(1);
                setTabSelectColorChange((LinearLayout) v, true);
                break;
            case R.id.llSetting:
                title.setText(getString(R.string.setting));
                setTabSelected(2);
                v.setTag(2);
                setTabSelectColorChange((LinearLayout) v, true);
                break;
        }
        lastTab = (LinearLayout) v;
    }

    private void setMute() {
        int state;
        if (isMute) {
            state = isRecording ? 1 : 0;
        } else {
            state = isRecording ? 0 : 1;
        }
        NetParamas paramas = new NetParamas();
        paramas.put("type", "param");
        paramas.put("action", "setaudio");
        paramas.put(isRecording ? "recmute" : "mute", String.valueOf(state));
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
                                isMute = !isMute;
                                changeMuteIcon();
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

    private void changeMuteIcon() {
        if (isMute) {
            ivVol.setImageDrawable(getResources().getDrawable(R.mipmap.close_vol));
        } else {
            ivVol.setImageDrawable(getResources().getDrawable(R.mipmap.open_vol));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        fFmpegPlayer.stop();
        isRtsp = false;
    }

    private void syncTime() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "system");
        paramas.put("action", "setdatetime");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        paramas.put("", dateFormat.format(new Date(System.currentTimeMillis())));
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            @Override
            public void onResponse(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyLogger.i(result);
                        if (result.contains("OK")) {
//                                Tool.showToast(result);
                        } else Tool.showToast(getString(R.string.set_time_fail));
                    }
                });
            }
        });
    }

    boolean isRecording = false;
    private boolean isMute = false;
    private int recordingTime = 0;
    private Timer recordTimer;

    //录像
    private void performClickRecored() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "system");
        paramas.put("action", "setrecstatus");
        paramas.put("recstat", isRecording ? "0" : "1");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            @Override
            public void onResponse(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MyLogger.i(result);
                        try {
                            if (result.contains("OK")) {
                                isRecording = !isRecording;
                                if (isRecording) {
                                    recordTimer = new Timer();
                                    recordTimer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            upDateRecordTime();
                                        }
                                    }, 0, 1000);
                                    Tool.showToast(getString(R.string.recording_start));
                                    iv_main_record2.setImageDrawable(getResources().getDrawable(R.mipmap.ic_record_on));
                                    iv_main_record.setImageDrawable(getResources().getDrawable(R.mipmap.ic_main_record_stop));
                                } else {
                                    recordTimer.cancel();
                                    recordingTime = 0;
                                    iv_main_record2.setImageDrawable(getResources().getDrawable(R.mipmap.ic_record_off));
                                    iv_main_record.setImageDrawable(getResources().getDrawable(R.mipmap.ic_main_record));
                                    record_time.setVisibility(View.GONE);
                                    Tool.showToast(getString(R.string.recording_end));
                                }
                            } else {
                                Tool.showToast(getString(R.string.recording_fail) + ResultParser.parse(result));
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

    private void upDateRecordTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                record_time.setVisibility(View.VISIBLE);
                tvTip.setText(new SimpleDateFormat("mm:ss").format(new Date(recordingTime)));
                recordingTime += 1000;
            }
        });

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //拍照
    private void performClickTakePic() {
        NetParamas paramas = new NetParamas();
        paramas.put("type", "snapshot");
        NetUtil.get(Constance.BASE_URL, paramas, new NetCallBack() {
            String s = null;

            @Override
            public void onResponse(final String result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            s = ResultParser.parse(result);
                            if (s.equalsIgnoreCase("ok")) {
                                Tool.showToast(getString(R.string.taking_photo_success));
                            } else Tool.showToast(getString(R.string.taking_photo_fail));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Tool.removeProgressDialog();
                        }
                    }
                });

            }
        }, getResources().getString(R.string.photoing), false);
    }

    public void setTabSelectColorChange(LinearLayout root, boolean isSelected) {
        ((ImageView) root.getChildAt(0)).setImageResource(isSelected ? tabImgs[((int) root.getTag())][0] : tabImgs[((int) root.getTag())][1]);
        ((TextView) root.getChildAt(1)).setTextColor(isSelected ? getResources().getColor(R.color.text_check_color) : 0xffAFAFAF);
    }


    private Fragment lastFragment;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Fragment f = null;
            MyLogger.e("what)---------->>" + msg.what);
            switch (msg.what) {
                case -1:
                    timeOut.cancel();
                    Tool.showToast(getString(R.string.load_video_fail));
                    Tool.removeProgressDialog();
                    break;
                case 0:
                    MyLogger.e("setSurface" + System.currentTimeMillis());
                    fFmpegPlayer.setSurface(surfaceHolder.getSurface(), 0, 0);
                    MyLogger.e("setSurface  end" + System.currentTimeMillis());
                    isRtsp = true;
                    timeOut.cancel();
                    Tool.removeProgressDialog();
                    break;
                case 3:
                    f = new PhotoFragment();
                    isShowRtsp = false;
//                    NativeImageLoader.getInstance().evictAll();//清除缓存
                    break;
                case 4:
                    if (!isShowRtsp && !isRtsp) {
                        setMedia();
                    }
                    isShowRtsp = true;
                    break;
                case 5:
                    f = new SettingsFragment();
                    isShowRtsp = false;
                    break;
            }
            switchFragment(lastFragment, f, msg.arg1);
            System.gc();
        }
    };

    private void setTabSelected(final int position) {
        mHandler.sendEmptyMessage(position + 3);
    }

    private boolean isShowRtsp = true;

    public void switchFragment(Fragment from, Fragment to, int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (to == null) {
            if (lastFragment == null)
                return;
            transaction.remove(lastFragment);
        } else {
            transaction.replace(R.id.flContent, to);
        }
        transaction.commit();
        lastFragment = to;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (isShowRtsp) {
                vVideoControl.setVisibility(View.VISIBLE);
                rlBottomLayout.setVisibility(View.GONE);
                titleLayout.setVisibility(View.GONE);
                rlDuallaoyut.setPadding(0, 0, 0, 0);
                rlDuallaoyut.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                rlDuallaoyut.setVisibility(View.GONE);
            }
            findViewById(R.id.btn_full).setVisibility(View.GONE);
            record_time.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            record_time.setPadding(0, Tool.dp2px(this, 10), 0, 0);
            record_time.setGravity(Gravity.CENTER);
            rootView.removeView(record_time);
            rlDuallaoyut.addView(record_time);
        } else {
            findViewById(R.id.btn_full).setVisibility(View.VISIBLE);
            rlBottomLayout.setVisibility(View.VISIBLE);
            rlDuallaoyut.setVisibility(View.VISIBLE);
            vVideoControl.setVisibility(View.GONE);
            titleLayout.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Tool.dp2px(this, 200));
            layoutParams.setMargins(0, Tool.dp2px(this, 180), 0, 0);
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams1.setMargins(0, Tool.dp2px(this, 100), 0, 0);
            record_time.setGravity(Gravity.CENTER);
            record_time.setLayoutParams(layoutParams1);
            rlDuallaoyut.setLayoutParams(layoutParams);
            rlDuallaoyut.removeView(record_time);
            rootView.addView(record_time);
        }
    }


//    public boolean isConnected() {
//        return ((MessageService) service).isSocketAlive();
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else showExitDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.notice)).setMessage(getString(R.string.notice_exit))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new WifiAdmin(MainActivity.this).disconnectWifi();
                        MyApplication.exit();
                    }
                }).show();
    }


    //-----------------------------------------------------------------------------------------
    //---------------------------------------------------------------
    private String[] modeSet = {"FHD@30fps High", "FHD@30fps MID", "FHD@30fps LOW", "HD@30fps HIGH", "HD@30fps MID", "HD@30fps LOW", "MD@30fps HIGH", "MD@30fps MID", "MD@30fps LOW"};
    private Spinner mode_set_spinner;
    private boolean gl_mode_first = true;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkConnectChangedReceiver.mListeners.remove(this);
        fFmpegPlayer.stop();
        fFmpegPlayer = null;
    }

    @Override
    public void OnVideoLostLink() {
        fFmpegPlayer.stop();
        showConnectingDialog();
    }

    @Override
    public void onNetChange(String message) {
        if (message.contains("DISCONNECTED")) {
            Tool.showToast(getString(R.string.device_disconnection));
            fFmpegPlayer.stop();
            NetworkConnectChangedReceiver.mListeners.remove(this);
            ivBackground.setVisibility(View.VISIBLE);
            showConnectingDialog();
            if (isRecording) {
                recordTimer.cancel();
            }
        }
    }
}
