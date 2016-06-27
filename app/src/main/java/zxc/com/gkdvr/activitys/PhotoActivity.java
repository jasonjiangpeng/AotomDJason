package zxc.com.gkdvr.activitys;

import java.io.File;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import zxc.com.gkdvr.R;
import zxc.com.gkdvr.photoview.PhotoView;
import zxc.com.gkdvr.utils.Tool;

public class PhotoActivity extends BaseActivity {
    private PhotoView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String path = getIntent().getStringExtra("path");
        File file = new File(path);
        ((TextView)findViewById(R.id.title_tv)).setText(file.getName());
        if (path == null) {
            finish();
        }
        iv = (PhotoView) findViewById(R.id.iv);
        if (path.startsWith("http"))
            Glide.with(this).load(path).centerCrop().into(iv);
        else
            iv.setImageURI(Uri.fromFile(new File(path)));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            findViewById(R.id.title_bar).setVisibility(View.GONE);
            iv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
        }else{
            findViewById(R.id.title_bar).setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Tool.dp2px(this,250));
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            iv.setLayoutParams(params);
        }
    }
}
