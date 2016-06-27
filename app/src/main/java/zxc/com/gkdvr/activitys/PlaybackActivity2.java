//package zxc.com.gkdvr.activitys;
//
//import android.net.Uri;
//import android.os.Bundle;
//import java.io.File;
//
//import tv.danmaku.ijk.media.player.IMediaPlayer;
//import tv.danmaku.ijk.widget.media.AndroidMediaController;
//import tv.danmaku.ijk.widget.media.IjkVideoView;
//import zxc.com.gkdvr.R;
//import zxc.com.gkdvr.utils.Tool;
//
///**
// * Created by xiaoyunfei on 16/3/30.
// */
//public class PlaybackActivity2 extends BaseActivity {
//    private String filePath;
//    private AndroidMediaController mMediaController;
//    private IjkVideoView mVideoView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_play_back2);
//        filePath = getIntent().getExtras().getString("videopath");
//        Tool.showProgressDialog(getString(R.string.loading),false,this);
//        init();
//    }
//
//    private void init() {
//        // prefer mVideoPath
//        mMediaController = new AndroidMediaController(this, false);
//        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
//        mVideoView.setMediaController(mMediaController);
//        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(IMediaPlayer mp) {
//                finish();
//            }
//        });
//        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(IMediaPlayer mp) {
//                Tool.removeProgressDialog();
//            }
//        });
//        // prefer mVideoPath
//        if (filePath.startsWith("http://")) {
//            mVideoView.setVideoPath(filePath);
//        }else {
//            mVideoView.setVideoURI(Uri.fromFile(new File(filePath)));
//        }
//        mVideoView.start();
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mVideoView != null && mVideoView.isPlaying()) {
//            mVideoView.pause();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mVideoView != null && mVideoView.isPlaying()) {
//            mVideoView.stopPlayback();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mVideoView != null) {
//            mVideoView.release(true);
//            //  IjkMediaPlayer.native_profileEnd();
//        }
//    }
//}
