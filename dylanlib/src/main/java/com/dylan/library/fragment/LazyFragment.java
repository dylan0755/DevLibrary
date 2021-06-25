package com.dylan.library.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: Dylan
 * Date: 2020/3/6
 * Desc:
 */
public abstract class LazyFragment extends LazyFragmentBase {
    protected View contentView;
    @LayoutRes
    public abstract int getLayoutId();

    public abstract void onFragmentCreate();

    @Override
    public abstract void firstVisibleLoad();



    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        contentView=layoutInflater.inflate(getLayoutId(), viewGroup, false);
        onFragmentCreate();
        return contentView;
    }






    public <T extends View> T findViewById(int id){
         if (contentView==null)return null;
         return contentView.findViewById(id);
    }
}
