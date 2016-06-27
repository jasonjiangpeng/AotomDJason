package zxc.com.gkdvr.entity;

import java.io.File;
import java.io.Serializable;

/**
 * Created by dk on 2016/6/6.
 */
public class ListImage implements Serializable{
    private ImageEntity file;
    private int type;

    public ImageEntity getFile() {
        return file;
    }

    public void setFile(ImageEntity file) {
        this.file = file;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ListImage() {
    }
    public ListImage(ImageEntity file, int type) {
        this.file = file;
        this.type = type;

    }
}
