package zxc.com.gkdvr.entity;

import java.io.Serializable;

/**
 * Created by dk on 2016/6/6.
 */
public class ImageEntity implements Serializable {
    private int imageid;
    private String imagetitle;
    private String imagename;
    private int imagestatus;

    public int getImageid() {
        return imageid;
    }

    public void setImageid(int imageid) {
        this.imageid = imageid;
    }

    public String getImagetitle() {
        return imagetitle;
    }

    public void setImagetitle(String imagetitle) {
        this.imagetitle = imagetitle;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public int getImagestatus() {
        return imagestatus;
    }

    public void setImagestatus(int imagestatus) {
        this.imagestatus = imagestatus;
    }
}
