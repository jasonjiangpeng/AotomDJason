/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package zxc.com.gkdvr.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * 文件操作工具类
 * Created by Jorstin on 2015/3/17.
 */
public class FileAccessor {


    public static final String TAG = FileAccessor.class.getName();
    public static final String APPS_ROOT_DIR = getExternalStorePath() + "/"+Constance.APP_NAME;
    public static final String IMESSAGE_VIDEO = getExternalStorePath() + "/"+Constance.APP_NAME+"/video";
    public static final String IMESSAGE_IMAGE = getExternalStorePath() + "/"+Constance.APP_NAME+"/image";
    public static final String IMESSAGE_PROTECT = getExternalStorePath() + "/"+Constance.APP_NAME+"/protect";
    public static final String IMESSAGE_AVATAR = getExternalStorePath() + "/"+Constance.APP_NAME+"/avatar";
    public static final String IMESSAGE_FILE = getExternalStorePath() + "/"+Constance.APP_NAME+"/file";


    /**
     * 初始化应用文件夹目录
     */
    public static void initFileAccess() {
        File rootDir = new File(APPS_ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
        File imessageDir = new File(IMESSAGE_VIDEO);
        if (!imessageDir.exists()) {
            imessageDir.mkdir();
        }
        File imageDir = new File(IMESSAGE_IMAGE);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }File protectDir = new File(IMESSAGE_PROTECT);
        if (!protectDir.exists()) {
            protectDir.mkdir();
        }
//        File fileDir = new File(IMESSAGE_FILE);
//        if (!fileDir.exists()) {
//            fileDir.mkdir();
//        }
//        File avatarDir = new File(IMESSAGE_AVATAR);
//        if (!avatarDir.exists()) {
//            avatarDir.mkdir();
//        }
    }

    public static String readContentByFile(String path) {
        BufferedReader reader = null;
        String line = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {
                    sb.append(line.trim());
                }
                return sb.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * 获取录像文件存储目录
     * @return
     */
    public static File getVoicePathName() {
        if (!isExistExternalStore()) {
          //  ToastUtil.showMessage(R.string.media_ejected);
            return null;
        }

        File directory = new File(IMESSAGE_VIDEO);
        if (!directory.exists() && !directory.mkdirs()) {
//            ToastUtil.showMessage("Path to file could not be created");
            return null;
        }

        return directory;
    }

    /**
     * 头像
     * @return
     */
    public static File getAvatarPathName() {
        if (!isExistExternalStore()) {
//            ToastUtil.showMessage(R.string.media_ejected);
            return null;
        }

        File directory = new File(IMESSAGE_AVATAR);
        if (!directory.exists() && !directory.mkdirs()) {
//            ToastUtil.showMessage("Path to file could not be created");
            return null;
        }

        return directory;
    }



    /**
     * 获取文件目录
     * @return
     */
    public static File getFilePathName() {
        if (!isExistExternalStore()) {
//            ToastUtil.showMessage(R.string.media_ejected);
            return null;
        }

        File directory = new File(IMESSAGE_FILE);
        if (!directory.exists() && !directory.mkdirs()) {
//            ToastUtil.showMessage("Path to file could not be created");
            return null;
        }

        return directory;
    }

    /**
     * 返回图片存放目录
     * @return
     */
    public static File getImagePathName() {
        if (!isExistExternalStore()) {
//            ToastUtil.showMessage(R.string.media_ejected);
            return null;
        }

        File directory = new File(IMESSAGE_IMAGE);
        if (!directory.exists() && !directory.mkdirs()) {
//            ToastUtil.showMessage("Path to file could not be created");
            return null;
        }

        return directory;
    }

    /**
     * 获取文件名
     * @param pathName
     * @return
     */
    public static String getFileName(String pathName) {

        int start = pathName.lastIndexOf("/");
        if (start != -1) {
            return pathName.substring(start + 1, pathName.length());
        }
        return pathName;

    }

    /**
     * 外置存储卡的路径
     * @return
     */
    public static String getExternalStorePath() {
        if (isExistExternalStore()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 是否有外存卡
     * @return
     */
    public static boolean isExistExternalStore() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static String getFileUrlByFileName(String fileName) {
        return FileAccessor.IMESSAGE_IMAGE + File.separator + FileAccessor.getSecondLevelDirectory(fileName)+ File.separator + fileName;
    }

    /**
     *
     * @param filePaths
     */
    public static void delFiles(ArrayList<String> filePaths) {
        for(String url : filePaths) {
            if(!TextUtils.isEmpty(url))
                delFile(url);
        }
    }


    public static boolean delFile(String filePath){
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return true;
        }

        return file.delete();
    }

    /**
     *
     * @param fileName
     * @return
     */
    public static String getSecondLevelDirectory(String fileName) {
        if(TextUtils.isEmpty(fileName) || fileName.length() < 4) {
            return null;
        }

        String sub1 = fileName.substring(0, 2);
        String sub2 = fileName.substring(2, 4);
        return sub1 + File.separator + sub2;
    }

    /**
     *
     * @param root
     * @param srcName
     * @param destName
     */
    public static void renameTo(String root , String srcName , String destName) {
        if(TextUtils.isEmpty(root) || TextUtils.isEmpty(srcName) || TextUtils.isEmpty(destName)){
            return;
        }

        File srcFile = new File(root + srcName);
        File newPath = new File(root + destName);

        if(srcFile.exists()) {
            srcFile.renameTo(newPath);
        }
    }

    public static File getTackPicFilePath() {
        File localFile = new File(getExternalStorePath()+ "/ECSDK_Demo/.tempchat" , "temp.jpg");
        if ((!localFile.getParentFile().exists())
                && (!localFile.getParentFile().mkdirs())) {
            Log.e("hhe", "SD卡不存在");
            localFile = null;
        }
        return localFile;
    }
}
