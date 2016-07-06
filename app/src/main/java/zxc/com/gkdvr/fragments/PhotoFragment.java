package zxc.com.gkdvr.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import zxc.com.gkdvr.R;
import zxc.com.gkdvr.adapter.MyFrageStatePagerAdapter;
import zxc.com.gkdvr.utils.Net.NetParamas;
import zxc.com.gkdvr.utils.Tool;
import zxc.com.gkdvr.utils.UIUtil;

/**
 * Created by dk on 2016/5/27.
 */
public class PhotoFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    private android.support.design.widget.TabLayout tableLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private MyFrageStatePagerAdapter mAdapter;
    private int[][] tabImg = {{R.mipmap.ic_tab_img_a, R.mipmap.ic_tab_img_b}, {R.mipmap.ic_tab_video_a, R.mipmap.ic_tab_video_b},
            {R.mipmap.ic_tab_lock_a, R.mipmap.ic_tab_lock_b}};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photos, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tableLayout = (TabLayout) view.findViewById(R.id.tlTab);
        viewPager = (ViewPager) view.findViewById(R.id.mMainViewPager);
        initTabLayout();
        initViewPager();
    }

    private void initTabLayout() {
        tableLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        tableLayout.setSelectedTabIndicatorHeight(UIUtil.getTranslateHeight(6));
        tableLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
    }

    private void initViewPager() {
        fragments = new ArrayList<>();
        fragments.add(new LocalFileFragment().setType("image"));
        fragments.add(new LocalFileFragment().setType("video"));
        fragments.add(new LocalFileFragment().setType("protect"));
        mAdapter = new MyFrageStatePagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        viewPager.setOffscreenPageLimit(2);
        tableLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。f
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
