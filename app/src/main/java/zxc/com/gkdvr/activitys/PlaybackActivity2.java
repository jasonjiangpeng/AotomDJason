package zxc.com.gkdvr.activitys;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.widget.media.AndroidMediaController;
import tv.danmaku.ijk.widget.media.IjkVideoView;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by xiaoyunfei on 16/3/30.
 */
public class PlaybackActivity2 extends BaseActivity implements IMediaPlayer.OnBufferingUpdateListener {
    private String filePath;
    private AndroidMediaController mMediaController;
    private IjkVideoView mVideoView;
    private RelativeLayout rootView;
    private LinearLayout progressDialog;
    private boolean isProgressShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play_back2);
        filePath = getIntent().getExtras().getString("videopath");
        Tool.showProgressDialog(getString(R.string.loading), false, this);
        init();
    }

    private void init() {
        progressDialog = (LinearLayout) View.inflate(this, R.layout.dialog, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressDialog.setLayoutParams(layoutParams);
        rootView = (RelativeLayout) findViewById(R.id.rootView);
        RelativeLayout vVideoControl = (RelativeLayout) getLayoutInflater().inflate(R.layout.view_video_control, null, false);
        ImageView imageView = (ImageView) vVideoControl.findViewById(R.id.ivBack);
        vVideoControl.findViewById(R.id.bot_layout).setVisibility(View.GONE);
        vVideoControl.findViewById(R.id.iv_main_record2).setVisibility(View.GONE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rootView.addView(vVideoControl);
        mMediaController = new AndroidMediaController(this, false);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                finish();
            }
        });
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                Tool.removeProgressDialog();
                if (filePath.startsWith("http")) {
                    mp.setOnBufferingUpdateListener(PlaybackActivity2.this);
                }
            }
        });
        // prefer mVideoPath
        if (filePath.startsWith("http://")) {
            mVideoView.setVideoPath(filePath);
        } else {
            mVideoView.setVideoURI(Uri.fromFile(new File(filePath)));
        }
        mVideoView.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.release(true);
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        MyLogger.i("-------------->bufferedPercent:" + percent);
        int current = (int) (mp.getCurrentPosition() * 100 / mp.getDuration());
        MyLogger.i("-------------->current:" + current);
        if (current >= percent) {
            if (!isProgressShow) {
                rootView.addView(progressDialog);
                isProgressShow = true;
            }
        } else {
            if (isProgressShow) {
                rootView.removeView(progressDialog);
                isProgressShow = false;
            }
        }
    }
}
