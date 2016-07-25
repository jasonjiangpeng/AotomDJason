package com.libs.ffmpeg;


import zxc.com.gkdvr.utils.LibUtil;
import zxc.com.gkdvr.utils.MyLogger;

/**
 * Created by xiaoyunfei on 16/5/12.
 */
public class FFmpegPlayer {

    private static LibUtil libUtil = LibUtil.getLibUtil();
    private onVideoLostLinkListner listner;

    public FFmpegPlayer(onVideoLostLinkListner listner) {
        this.listner = listner;
    }

    static {
        libUtil.loadLibs();
    }

    public native int setMediaUri(String uri);

    public native void start();

    public native void stop();

    public native void setSurface(Object surface, int weight, int height);

    public native int getVideoWight();

    public native int getVideoheight();

    public native void takeSpanShot(Object bitMap);


    ////////////////////////////////////////////////////////////////////////////////////////////
    public void videoLostLink() {
        listner.videoLostLink();
    }

    public interface onVideoLostLinkListner {
        void videoLostLink();
    }
}