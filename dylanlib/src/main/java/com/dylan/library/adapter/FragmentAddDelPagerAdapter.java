package com.dylan.library.adapter;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.dylan.library.utils.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2021/03/03
 * Desc: 场景：ViewPager 增删 Fragment
 */
public class FragmentAddDelPagerAdapter extends FragmentStatePagerAdapter {
    private FragmentManager mFragmentManager;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();

    public FragmentAddDelPagerAdapter(FragmentManager fm) {
        super(fm);
        this.mFragmentManager = fm;
    }

    public void bindFragment(List<Fragment> fragments,List<String> titles) {
        if (EmptyUtils.isEmpty(fragments)) return;
        this.mFragments.clear();
        this.mFragments.addAll(fragments);
        mTitles.clear();
        mTitles.addAll(titles);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int arg0) {
        return mFragments.get(arg0);//
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return EmptyUtils.isNotEmpty(mTitles)?mTitles.get(position):"";
    }

    @Override
    public int getCount() {
        return EmptyUtils.isNotEmpty(mFragments)? mFragments.size():0;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        if (!((Fragment) object).isAdded() || !mFragments.contains(object)) {
            return PagerAdapter.POSITION_NONE;
        }
        return mFragments.indexOf(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Fragment instantiateItem = ((Fragment) super.instantiateItem(container, position));
        Fragment item = mFragments.get(position);
        if (instantiateItem == item) {
            return instantiateItem;
        } else {
            //如果集合中对应下标的fragment和fragmentManager中的对应下标的fragment对象不一致，那么就是新添加的，所以自己add进入；这里为什么不直接调用super方法呢，因为fragment的mIndex搞的鬼，以后有机会再补一补。
            mFragmentManager.beginTransaction().add(container.getId(), item).commitNowAllowingStateLoss();
            return item;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        //如果getItemPosition中的值为PagerAdapter.POSITION_NONE，就执行该方法。
        if (mFragments.contains(fragment)) {
            super.destroyItem(container, position, fragment);
            return;
        }
        //自己执行移除。因为mFragments在删除的时候就把某个fragment对象移除了，所以一般都得自己移除在fragmentManager中的该对象。
        mFragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss();
    }


}
