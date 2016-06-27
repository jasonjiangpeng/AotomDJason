package zxc.com.gkdvr.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

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

    private void initView() {
        ((TextView) findViewById(R.id.title_tv)).setText(getString(R.string.about_setting));
        ((TextView) findViewById(R.id.version)).setText(getString(R.string.current_version_info) + Tool.getVersionName(this));
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
