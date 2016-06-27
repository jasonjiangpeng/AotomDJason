package zxc.com.gkdvr.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import zxc.com.gkdvr.MyApplication;

/**
 * Created by dk on 2016/6/2.
 */
public class PermissionUtil {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @TargetApi(Build.VERSION_CODES.M)
    public void askforPermission(final String permissions) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            int hasWriteContactsPermission = MyApplication.getCurrentActivity().checkSelfPermission(permissions);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                if (!MyApplication.getCurrentActivity().shouldShowRequestPermissionRationale(permissions)) {
                    showMessageOKCancel(permissions,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MyApplication.getCurrentActivity().requestPermissions(new String[]{permissions},
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            });
                    return;
                }
                MyApplication.getCurrentActivity().requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MyApplication.getCurrentActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
