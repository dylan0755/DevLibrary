package com.dylan.library.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Author: Dylan
 * Date: 2021/03/03
 * Desc: 场景：ViewPager 增删 Fragment
 */
public class FragmentAddDelPagerAdapter extends FragmentStatePagerAdapter {
    protected List<Fragment> fragmentList;
    protected List<String> titleList;


    public FragmentAddDelPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }


    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //如果直接返回FragmentStatePagerAdapter.POSITION_NONE，
    // 那么所有的Fragment 都将重新创建，则
    @Override
    public int getItemPosition(@NonNull Object object) {
        int index = indexOfFragments(object);
        return index != -1 ? index : super.getItemPosition(object);
    }

    private int indexOfFragments(Object object) {
        if (object instanceof Fragment) {
            return fragmentList.indexOf(object);
        }
        return -1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }


}
