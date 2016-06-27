///*
// * Copyright (C) 2013 yixia.com
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package zxc.com.gkdvr.activitys;
//
//import android.animation.ObjectAnimator;
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//import io.vov.vitamio.LibsChecker;
//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.widget.VideoView;
//import zxc.com.gkdvr.R;
//import zxc.com.gkdvr.utils.MyLogger;
//import zxc.com.gkdvr.view.CustomMediaController;
//
//public class VideoViewActivity extends Activity {
//    public static final String VIDEO_PATH = "videopath";
//    private VideoView mVideoView;
//    private LinearLayout mLoadingLayout;
//    private ImageView mLoadingImg;
//    private ObjectAnimator mOjectAnimator;
//    /**
//     * 当前进度
//     */
//    private Long currentPosition = (long) 0;
//    private String mVideoPath = "";
//    /**
//     * setting
//     */
//    private boolean needResume;
//
//    /**
//     * 视频播放控制界面
//     */
//    CustomMediaController mediaController;
//
//    @Override
//    public void onCreate(Bundle icicle) {
//        super.onCreate(icicle);
//        if (!LibsChecker.checkVitamioLibs(this)) return;
//        setContentView(R.layout.activity_video_play_layout);
//        getDataFromIntent();
//        initviews();
//        initVideoSettings();
//    }
//
//
//    private void getDataFromIntent() {
//        Intent Intent = getIntent();
//        if (Intent != null && Intent.getExtras().containsKey(VIDEO_PATH)) {
//            mVideoPath = Intent.getExtras().getString(VIDEO_PATH);
//        }
//    }
//
//    private void initviews() {
//        mVideoView = (VideoView) findViewById(R.id.surface_view);
//        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_LinearLayout);
//        mLoadingImg = (ImageView) findViewById(R.id.loading_image);
//    }
//
//    private void initVideoSettings() {
//        mVideoView.requestFocus();
//        mVideoView.setBufferSize(1024 * 1024);
//        mVideoView.setVideoChroma(MediaPlayer.VIDEOCHROMA_RGB565);
//        mVideoView.setMediaController(mediaController);
//        mVideoView.setVideoPath(mVideoPath);
//    }
//
//    public void onResume() {
//        super.onResume();
//        preparePlayVideo();
//    }
//
//    private void preparePlayVideo() {
//        startLoadingAnimator();
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                // TODO Auto-generated method stub
//                stopLoadingAnimator();
//
//                if (currentPosition > 0) {
//                    mVideoView.seekTo(currentPosition);
//                } else {
//                    mediaPlayer.setPlaybackSpeed(1.0f);
//                }
//                startPlay();
//            }
//        });
//        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
//                switch (arg1) {
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                        //开始缓存，暂停播放
//                        MyLogger.i("开始缓存");
//                        startLoadingAnimator();
//                        if (mVideoView.isPlaying()) {
//                            stopPlay();
//                            needResume = true;
//                        }
//                        break;
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                        //缓存完成，继续播放
//                        stopLoadingAnimator();
//                        if (needResume) startPlay();
//                        MyLogger.i("缓存完成");
//                        break;
//                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
//                        //显示 下载速度
//                        MyLogger.i("download rate:" + arg2);
//                        break;
//                }
//                return true;
//            }
//        });
//        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//            }
//        });
//        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
////                LogUtils.i(LogUtils.LOG_TAG, "what=" + what);
//                return false;
//            }
//        });
//        mVideoView.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//            @Override
//            public void onSeekComplete(MediaPlayer mp) {
//            }
//        });
//        mVideoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//            @Override
//            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                MyLogger.i("percent" + percent);
//            }
//        });
//    }
//
//    @NonNull
//    private void startLoadingAnimator() {
//        if (mOjectAnimator == null) {
//            mOjectAnimator = ObjectAnimator.ofFloat(mLoadingImg, "rotation", 0f, 360f);
//        }
//        mLoadingLayout.setVisibility(View.VISIBLE);
//
//        mOjectAnimator.setDuration(1000);
//        mOjectAnimator.setRepeatCount(-1);
//        mOjectAnimator.start();
//    }
//
//    private void stopLoadingAnimator() {
//        mLoadingLayout.setVisibility(View.GONE);
//        mOjectAnimator.cancel();
//    }
//
//    private void startPlay() {
//        mVideoView.start();
//    }
//
//    private void stopPlay() {
//        mVideoView.pause();
//    }
//
//    public void onPause() {
//        super.onPause();
//        currentPosition = mVideoView.getCurrentPosition();
//        mVideoView.pause();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//        if (mVideoView != null) {
//            mVideoView.stopPlayback();
//            mVideoView = null;
//        }
//    }
//
//    /**
//     * 获取视频当前帧
//     *
//     * @return
//     */
//    public Bitmap getCurrentFrame() {
//        if (mVideoView != null) {
//            MediaPlayer mediaPlayer = mVideoView.getmMediaPlayer();
//            return mediaPlayer.getCurrentFrame();
//        }
//        return null;
//    }
//
//    /**
//     * 快退(每次都快进视频总时长的1%)
//     */
//    public void speedVideo() {
//        if (mVideoView != null) {
//            long duration = mVideoView.getDuration();
//            long currentPosition = mVideoView.getCurrentPosition();
//            long goalduration = currentPosition + duration / 10;
//            if (goalduration >= duration) {
//                mVideoView.seekTo(duration);
//            } else {
//                mVideoView.seekTo(goalduration);
//            }
//            //T.showToastMsgShort(this, StringUtils.generateTime(goalduration));
//        }
//    }
//
//    /**
//     * 快退(每次都快退视频总时长的1%)
//     */
//    public void reverseVideo() {
//        if (mVideoView != null) {
//            long duration = mVideoView.getDuration();
//            long currentPosition = mVideoView.getCurrentPosition();
//            long goalduration = currentPosition - duration / 10;
//            if (goalduration <= 0) {
//                mVideoView.seekTo(0);
//            } else {
//                mVideoView.seekTo(goalduration);
//            }
//            // T.showToastMsgShort(this, StringUtils.generateTime(goalduration));
//        }
//    }
//
//    /**
//     * 设置屏幕的显示大小
//     */
//    public void setVideoPageSize(int currentPageSize) {
//        if (mVideoView != null) {
//            mVideoView.setVideoLayout(currentPageSize, 0);
//        }
//    }
//}
