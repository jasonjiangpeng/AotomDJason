package zxc.com.gkdvr.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

import zxc.com.gkdvr.MyApplication;
import zxc.com.gkdvr.R;
import zxc.com.gkdvr.adapter.MyFrageStatePagerAdapter;
import zxc.com.gkdvr.fragments.RemoteFileFragment;
import zxc.com.gkdvr.utils.Tool;
import zxc.com.gkdvr.utils.UIUtil;

/**
 * Created by dk on 2016/6/2.
 */
public class RemoteFileActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {
    private android.support.design.widget.TabLayout tableLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private MyFrageStatePagerAdapter mAdapter;
    private int[][] tabImg = {{R.mipmap.ic_tab_img_a, R.mipmap.ic_tab_img_b}, {R.mipmap.ic_tab_video_a, R.mipmap.ic_tab_video_b}
            , {R.mipmap.ic_tab_lock_a, R.mipmap.ic_tab_lock_b}};
    private int initedNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_file);
        Tool.showProgressDialog(getString(R.string.loading), false, this);
        initView();
    }

    private void initView() {
        setTitleText(getString(R.string.remote_file));
        findViewById(R.id.title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tableLayout = (TabLayout) findViewById(R.id.tlTab);
        viewPager = (ViewPager) findViewById(R.id.mMainViewPager);
        initTabLayout();
        initViewPager();
    }

    private void initTabLayout() {
        tableLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        tableLayout.setSelectedTabIndicatorHeight(UIUtil.getTranslateHeight(6));
        tableLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
    }

    public void initFinish() {
        initedNum++;
        if (initedNum >= 3) {
            Tool.removeProgressDialog();
        }
    }

    private void initViewPager() {
        fragments = new ArrayList<>();
        fragments.add(new RemoteFileFragment().setType(0));
        fragments.add(new RemoteFileFragment().setType(1));
        fragments.add(new RemoteFileFragment().setType(2));
        mAdapter = new MyFrageStatePagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        viewPager.setOffscreenPageLimit(2);
        tableLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
        tableLayout.setOnTabSelectedListener(this);
        for (int i = 0; i < tabImg.length; i++) {
            if (i == 0) {
                tableLayout.getTabAt(i).setIcon(tabImg[i][0]).setTag(i);
            } else {
                tableLayout.getTabAt(i).setIcon(tabImg[i][1]).setTag(i);

            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem((int) tab.getTag());
        for (int i = 0; i < tableLayout.getTabCount(); i++) {
            TabLayout.Tab tab2 = tableLayout.getTabAt(i);
            if (tab2 == null)
                continue;
            if (i == (int) tab.getTag()) {
                tab.setIcon(tabImg[i][0]);
            } else {
                tab2.setIcon(tabImg[i][1]);
            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
}
