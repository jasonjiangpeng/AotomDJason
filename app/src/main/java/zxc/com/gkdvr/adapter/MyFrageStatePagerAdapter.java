package zxc.com.gkdvr.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;

    public MyFrageStatePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int arg0) {

        return fragments.get(arg0);
    }

    @Override
    public int getCount() {

        return fragments == null ? 0 : fragments.size();
    }

}
