package com.dylan.library.callback;

import android.support.v4.view.ViewPager;

/**
 * Author: Administrator
 * Date: 2020/7/1
 * Desc:
 */
public  abstract class SimplePagerSelectedListener implements ViewPager.OnPageChangeListener {

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }


    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public abstract void onPageSelected(int i);


}
