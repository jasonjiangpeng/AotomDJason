package zxc.com.gkdvr.entity;

import java.io.File;
import java.io.Serializable;

/**
 * 作者: DongZhi 2016/2/29.
 * 保佑以下代码无bug...
 */
public class ListFile implements Serializable {
    private File file;
    private String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    public ListFile() {

    }

    public ListFile(File file, String type) {
        this.file = file;
        this.type = type;

    }
}