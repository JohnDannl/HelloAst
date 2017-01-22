package cnedu.ustc.jd.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by jd5737 on 2016/7/27.
 */
public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

    private int pageSize = 4;

    public MyViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    public MyViewPagerAdapter(FragmentManager fm, int pageSize){
        super(fm);
        if (pageSize > 0) {
            this.pageSize = pageSize;
        }
    }

    @Override
    public Fragment getItem(int position) {
        PageFragment pageFragment = PageFragment.newInstance(position+1);
        return pageFragment;
    }

    @Override
    public int getCount() {
        return pageSize;
    }
}
