package com.dylan.library.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.dylan.library.utils.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/8/21
 * Desc:
 */
public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> titleList;

    public CommonFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment) {
        if (fragmentList == null) fragmentList = new ArrayList<>();
        fragmentList.add(fragment);
    }

    public void addTitle(String title) {
        if (titleList == null) titleList = new ArrayList<>();
        titleList.add(title);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return EmptyUtils.isNotEmpty(fragmentList) ? fragmentList.size() : 0;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return EmptyUtils.isNotEmpty(titleList) ? titleList.get(position) : "";
    }




}
