package zxc.com.gkdvr.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import zxc.com.gkdvr.R;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/6/24.
 */
public class VersionInfoActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_info);
        initView();
    }

    private void showWifiDialog() {
        new android.support.v7.app.AlertDialog.Builder(this).setTitle(getString(R.string.notice))
                .setMessage(getString(R.string.unable_wifi))
                .setPositiveButton(getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                        startActivity(intent);
                    }
                }).show();
    }

    private void initView() {
        ((TextView) findViewById(R.id.title_tv)).setText(getString(R.string.about_setting));
        ((TextView) findViewById(R.id.version)).setText(getString(R.string.current_version_info) + Tool.getVersionName(this));
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.check_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWifiConnectedToDVR()) {
                    showWifiDialog();
                } else {
                    Tool.showProgressDialog(getString(R.string.checking_update),false,VersionInfoActivity.this);
                    PgyUpdateManager.register(VersionInfoActivity.this, new UpdateManagerListener() {
                        @Override
                        public void onNoUpdateAvailable() {
                            Tool.removeProgressDialog();
                            Tool.showToast(getString(R.string.lastest_vresion));
                        }

                        @Override
                        public void onUpdateAvailable(String s) {
                            Tool.removeProgressDialog();
                            final AppBean appBean = getAppBeanFromString(s);
                            AlertDialog a = new AlertDialog.Builder(VersionInfoActivity.this)
                                    .setTitle(getString(R.string.update))
                                    .setMessage(getString(R.string.is_update))
                                    .setPositiveButton(getString(R.string.update_now),
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    startDownloadTask(
                                                            VersionInfoActivity.this,
                                                            appBean.getDownloadURL());
                                                }
                                            }).setNegativeButton(getString(R.string.update_later), null)
                                    .setCancelable(false).create();
                            a.setCanceledOnTouchOutside(false);
                            a.show();
                        }
                    });
                }
            }
        });

    }
}
