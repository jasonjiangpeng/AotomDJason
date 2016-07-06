//package zxc.com.gkdvr.activitys;
//
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.Display;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.widget.MediaController;
//
//
//import java.io.File;
//import java.io.IOException;
//
//import zxc.com.gkdvr.R;
//import zxc.com.gkdvr.utils.Tool;
//
//public class PlaybackActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaController.MediaPlayerControl, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {
//
//    private String filePath = "";
//    private SurfaceView videoView;
//    private SurfaceHolder holder;
//    private MediaPlayer player;
//    private MediaController mediaController;
//    private int duration, mPercent;
//    private int vWidth;
//    private int vHeight;
//    private Display currDisplay;
//    private Uri mUri;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_play_back);
//        filePath = getIntent().getStringExtra("path");
//        findViews();
//        init();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (player != null && player.isPlaying()) {
//            player.pause();
//        }
//    }
//
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (player != null) {
//            player.stop();
//            player.release();
//            player = null;
//        }
//    }
//
//    private void init() {
//        if (filePath.startsWith("http://")) {
//            mUri = Uri.parse(filePath);
//        } else {
//            mUri = Uri.fromFile(new File(filePath));
//        }
//        holder = videoView.getHolder();
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        holder.addCallback(this);
//    }
//
//    private void findViews() {
//        videoView = (SurfaceView) findViewById(R.id.video);
//    }
//
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        try {
//            player = new MediaPlayer();
//            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            player.setScreenOnWhilePlaying(true);
//            player.setOnPreparedListener(this);
//            player.setOnCompletionListener(this);
//            player.setOnErrorListener(this);
//            player.setOnVideoSizeChangedListener(this);
//            player.setOnInfoListener(this);
//            player.setOnBufferingUpdateListener(this);
//            player.setDisplay(holder);
//            mPercent = 0;
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                player.setDataSource(this, mUri, null);
//            } else {
//                player.setDataSource(mUri.toString());
//            }
//            player.prepareAsync();
//            attachMediaController();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } catch (IllegalArgumentException ex) {
//            ex.printStackTrace();
//        } finally {
//        }
//    }
//
//    private void attachMediaController() {
//        if (player != null && mediaController != null) {
//            mediaController = new MediaController(this);
//            mediaController.setMediaPlayer(this);
//            mediaController.setAnchorView(videoView);
//            mediaController.setEnabled(true);
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        finish();
//    }
//
//    @Override
//    public boolean onError(MediaPlayer mp, int what, int extra) {
//        return false;
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        player.start();
//    }
//
//    @Override
//    public void onSeekComplete(MediaPlayer mp) {
//       Tool.removeProgressDialog();
//    }
//
//    @Override
//    public void start() {
//        if (player != null)
//            player.start();
//    }
//
//    @Override
//    public void pause() {
//        if (player != null)
//            player.pause();
//    }
//
//    @Override
//    public int getDuration() {
//        return player.getDuration();
//    }
//
//    @Override
//    public int getCurrentPosition() {
//        return player.getCurrentPosition();
//    }
//
//    @Override
//    public void seekTo(int pos) {
//        Tool.showProgressDialog(getString(R.string.loading),false,this);
//        player.seekTo(pos);
//    }
//
//    @Override
//    public boolean isPlaying() {
//        return player.isPlaying();
//    }
//
//    @Override
//    public int getBufferPercentage() {
//        return mPercent;
//    }
//
//    @Override
//    public boolean canPause() {
//        return true;
//    }
//
//    @Override
//    public boolean canSeekBackward() {
//        return true;
//    }
//
//    @Override
//    public boolean canSeekForward() {
//        return true;
//    }
//
//    @Override
//    public int getAudioSessionId() {
//        return player.getAudioSessionId();
//    }
//
//    @Override
//    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//
//    }
//
//    @Override
//    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//        return false;
//    }
//
//    @Override
//    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//
//    }
//}
