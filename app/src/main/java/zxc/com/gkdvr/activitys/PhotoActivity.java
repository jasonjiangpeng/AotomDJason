package zxc.com.gkdvr.activitys;

import java.io.File;
import java.util.ArrayList;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import zxc.com.gkdvr.R;
import zxc.com.gkdvr.photoview.PhotoView;
import zxc.com.gkdvr.utils.Tool;

public class PhotoActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private PhotoView iv;
    private ViewPager vp;
    private ArrayList<File> allFiles;
    private ArrayList<View> views = new ArrayList<>();

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
        iv = (PhotoView) findViewById(R.id.iv);
        vp = (ViewPager) findViewById(R.id.vp);
        String path = getIntent().getStringExtra("path");

        if (path == null) {
            allFiles = (ArrayList<File>) getIntent().getSerializableExtra("files");
            File file = (File) getIntent().getSerializableExtra("file");
            ((TextView) findViewById(R.id.title_tv)).setText(file.getName());
            for (File f : allFiles) {
                PhotoView photoView = new PhotoView(this);
                views.add(photoView);
                Glide.with(this).load(f).into(photoView);
            }
            vp.addOnPageChangeListener(this);
            vp.setAdapter(new myPagerAdapter());
            vp.setCurrentItem(allFiles.indexOf(file));
            vp.setOffscreenPageLimit(views.size());
            vp.setPageMargin(10);
            return;
        }
        File file = new File(path);
        ((TextView) findViewById(R.id.title_tv)).setText(file.getName());
        if (path.startsWith("http")) {
            iv.setVisibility(View.VISIBLE);
            Glide.with(this).load(path).into(iv);
            vp.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int position = vp.getCurrentItem();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            ((PhotoView) views.get(position)).setScaleType(ImageView.ScaleType.FIT_XY);
            if (position > 0)
                ((PhotoView) views.get(position - 1)).setScaleType(ImageView.ScaleType.FIT_XY);
            if (position < views.size() - 1)
                ((PhotoView) views.get(position + 1)).setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ((PhotoView) views.get(vp.getCurrentItem())).setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (position > 0)
                ((PhotoView) views.get(position - 1)).setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (position < views.size() - 1)
                ((PhotoView) views.get(position + 1)).setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((PhotoView) views.get(position)).setScaleType(ImageView.ScaleType.FIT_XY);
            if (position > 0)
                ((PhotoView) views.get(position - 1)).setScaleType(ImageView.ScaleType.FIT_XY);
            if (position < views.size() - 1)
                ((PhotoView) views.get(position + 1)).setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            ((PhotoView) views.get(position)).setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (position > 0)
                ((PhotoView) views.get(position - 1)).setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (position < views.size() - 1)
                ((PhotoView) views.get(position + 1)).setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class myPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position), 0);//添加页卡
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }
}
