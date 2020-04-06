package com.dylan.library.adapter;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.TableLayout;


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
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
    }

    public void addFragment(Fragment fragment,String title){
        addFragment(fragment);
        addTitle(title);
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    public void addFragments(List<Fragment> fragments){
        fragmentList=fragments;
    }

    public void addTitle(String title) {
        titleList.add(title);
    }

    public void addTitles(List<String> titleList){
      this.titleList=titleList;
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


    public List<Fragment> getFragmentList(){
        return fragmentList;
    }

    public List<String> getTitleList(){
        return titleList;
    }


    public void addTabWithName(TabLayout tabLayout){
      if (tabLayout!=null){
          if (EmptyUtils.isNotEmpty(titleList)){
              for (String title:titleList){
                  tabLayout.addTab(tabLayout.newTab().setText(title));
              }
          }
      }
    }


}
