package zxc.com.gkdvr.activitys;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.libs.ffmpeg.FFmpegPlayer;

import java.util.Timer;
import java.util.TimerTask;

import zxc.com.gkdvr.R;
import zxc.com.gkdvr.utils.Constance;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/6/8.
 */
public class VideoPlayActivity extends BaseActivity implements View.OnClickListener, FFmpegPlayer.onVideoLostLinkListner {
    private FFmpegPlayer fFmpegPlayer;
    private String path;
    private SurfaceView surfaceView;
    private boolean isPlaying = false;
    private SurfaceHolder surfaceHolder;
    private Handler mHandler = new Handler();
    private Timer timeOut;
    private RelativeLayout vVideoControl;
    private ImageView ivVol;
    private ImageView iv_main_takepic2;
    private ImageView iv_main_record2;
    private ImageView iv_main_remote_pic_main2;
    private FrameLayout rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        path = getIntent().getStringExtra("path");
        rootView = (FrameLayout) findViewById(R.id.rootView);
        fFmpegPlayer = new FFmpegPlayer(this);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        vVideoControl = (RelativeLayout) getLayoutInflater().inflate(R.layout.view_video_control, null, false);
        vVideoControl.findViewById(R.id.change_camera).setOnClickListener(this);
        vVideoControl.findViewById(R.id.ivVol).setOnClickListener(this);
        vVideoControl.findViewById(R.id.iv_main_back_por).setOnClickListener(this);
        iv_main_takepic2 = (ImageView) vVideoControl.findViewById(R.id.iv_main_takepic2);
        iv_main_record2 = (ImageView) vVideoControl.findViewById(R.id.iv_main_record2);
        iv_main_remote_pic_main2 = (ImageView) vVideoControl.findViewById(R.id.iv_main_remote_pic_main2);
        vVideoControl.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rootView.addView(vVideoControl);
        vVideoControl.setVisibility(View.GONE);
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vVideoControl.getVisibility() == View.VISIBLE) {
                    vVideoControl.setVisibility(View.GONE);
                } else {
                    vVideoControl.setVisibility(View.VISIBLE);

                }
            }
        });
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                surfaceHolder = holder;
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

    private void setMedia() {
        MyLogger.i("setMedia");
        if (!isWifiConnectedToDVR()) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Tool.showProgressDialog(getString(R.string.loading), false, VideoPlayActivity.this);
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
                        MyLogger.i("timeOut");
                        Tool.showToast(getString(R.string.load_video_fail));
                        fFmpegPlayer.stop();
                    }
                });
            }
        }, 20000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int initResult = fFmpegPlayer.setMediaUri(path);
                mHandler.sendEmptyMessage(initResult);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_camera:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fFmpegPlayer.stop();
    }

    @Override
    public void videoLostLink() {

    }
}
