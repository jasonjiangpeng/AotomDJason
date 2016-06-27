package zxc.com.gkdvr.entity;

import java.io.Serializable;

/**
 * Created by dk on 2016/6/6.
 */
public class VideoEntity implements Serializable {
    private int videoid;
    private String videotitle;
    private String videoname;
    private int videostatus;
    private int videoduration;

    public int getVideoid() {
        return videoid;
    }

    public void setVideoid(int videoid) {
        this.videoid = videoid;
    }

    public String getVideotitle() {
        return videotitle;
    }

    public void setVideotitle(String videotitle) {
        this.videotitle = videotitle;
    }

    public String getVideoname() {
        return videoname;
    }

    public void setVideoname(String videoname) {
        this.videoname = videoname;
    }

    public int getVideostatus() {
        return videostatus;
    }

    public void setVideostatus(int videostatus) {
        this.videostatus = videostatus;
    }

    public int getVideoduration() {
        return videoduration;
    }

    public void setVideoduration(int videoduration) {
        this.videoduration = videoduration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoEntity that = (VideoEntity) o;

        return videoname != null ? videoname.equals(that.videoname) : that.videoname == null;

    }

    @Override
    public int hashCode() {
        return videoname != null ? videoname.hashCode() : 0;
    }
}
