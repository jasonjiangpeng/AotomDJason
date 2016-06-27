package zxc.com.gkdvr.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.receiver.NetworkConnectChangedReceiver;
import zxc.com.gkdvr.utils.MyLogger;
import zxc.com.gkdvr.utils.Tool;

/**
 * Created by dk on 2016/6/1.
 */
public class BaseActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MyApplication.appManager.addActivity(this);
        MyApplication.activities.add(this);
    }

    void setTitleText(String title) {
        TextView t = (TextView) findViewById(R.id.title_tv);
        if (t != null) {
            t.setText(title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLogger.i(getClass().getName() + "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MyApplication.appManager.finishActivity(this);
        MyLogger.i(getClass().getName() + "onDestroy");
        MyApplication.activities.remove(this);
    }

    int choisePosition;

    public void showSimpleChoiceDialog(String[] items, final DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this).setSingleChoiceItems(items, choisePosition, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choisePosition = which;
            }
        }).setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onClick(dialog, which);
                    }
                }).create().show();
    }

}
