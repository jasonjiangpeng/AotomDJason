package zxc.com.gkdvr.entity;

import java.io.Serializable;

/**
 * Created by dk on 2016/6/6.
 */
public class ListVideo implements Serializable{
    private VideoEntity file;
    private int type;

    public VideoEntity getFile() {
        return file;
    }

    public void setFile(VideoEntity file) {
        this.file = file;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ListVideo() {
    }
    public ListVideo(VideoEntity file, int type) {
        this.file = file;
        this.type = type;

    }
}
