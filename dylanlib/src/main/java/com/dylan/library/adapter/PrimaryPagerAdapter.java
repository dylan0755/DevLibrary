package com.dylan.library.adapter;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.dylan.library.proguard.NotProguard;

/**
 * Author: Dylan
 * Date: 2020/2/28
 * Desc:
 */
public abstract class PrimaryPagerAdapter extends PagerAdapter {
    @NotProguard
    protected PrimaryItem primaryItem;
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        if (primaryItem == null) {
            primaryItem = new PrimaryItem();
            primaryItem.position = position;
            primaryItem.object = object;
        } else {
            if (primaryItem.position != position) {
                PrimaryItemChanged(primaryItem);
                primaryItem.position = position;
                primaryItem.object = object;
            }
        }
    }

    public abstract void PrimaryItemChanged(PrimaryItem oldItem);


    public PrimaryItem getPrimaryItem() {
        return primaryItem;
    }

    public Object getPrimaryItemObject(){
        if (primaryItem!=null)return primaryItem.object;
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return false;
    }


    public  class PrimaryItem{
        public int position;
        public Object object;
    }
}
