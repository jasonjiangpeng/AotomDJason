package zxc.com.gkdvr.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.libs.ffmpeg.FFmpegPlayer;

import zxc.com.gkdvr.R;
import zxc.com.gkdvr.utils.Constance;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/6/8.
 */
public class VideoPlayActivity extends BaseActivity implements FFmpegPlayer.OnVideoLostLinkListner {
    private FFmpegPlayer fFmpegPlayer;
    private String path;
    private SurfaceView surfaceView;
    private boolean isPlaying= false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        fFmpegPlayer = new FFmpegPlayer(this);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        path = getIntent().getStringExtra("path");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isPlaying) fFmpegPlayer.start();
        else surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                Tool.removeProgressDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            fFmpegPlayer.setMediaUri(path);
                            fFmpegPlayer.setSurface(holder.getSurface(), 0, 0);
                            isPlaying = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        fFmpegPlayer.stop();
    }

    @Override
    public void OnVideoLostLink() {

    }
}
