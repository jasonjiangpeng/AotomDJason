package zxc.com.gkdvr.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.R;

/**
 * Created by dk on 2016/6/2.
 */
public class PermissionUtil {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasPermisson(final String permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWriteContactsPermission = MyApplication.getCurrentActivity().checkSelfPermission(permissions);
            if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void askforPermission(String permission) {
        MyApplication.getCurrentActivity().requestPermissions(new String[]{permission},
                REQUEST_CODE_ASK_PERMISSIONS);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void askforStoragePermission() {
        int hasWriteContactsPermission = MyApplication.getCurrentActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!MyApplication.getCurrentActivity().shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showMessageOKCancel(Manifest.permission_group.STORAGE,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyApplication.getCurrentActivity().requestPermissions(new String[]
                                                {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MyApplication.getCurrentActivity())
                .setMessage(message)
                .setPositiveButton(MyApplication.getCurrentActivity().getString(R.string.ensure), okListener)
                .setNegativeButton(MyApplication.getCurrentActivity().getString(R.string.cancel), null)
                .create()
                .show();
    }
}
