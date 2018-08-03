package com.dylan.library.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by Dylan on 2018/2/6.
 */

public abstract  class CheckBoxListAdapter<T,VH extends CommonBaseAdapter.ViewHolder> extends CommonBaseAdapter<T,VH> {
    private HashMap<Integer, Boolean> checkMap;
    public CheckBoxListAdapter(){
        checkMap  = new HashMap<>();
    }

    @Override
    public abstract int getLayoutId();

    @Override
    public void onBinderItem(VH holder, T t, int position) {
        onBindItem(holder,t,position);
        if (hasCheck(position)){
            onCheckState(holder,t,position);
        }else{
            onUnCheckState(holder,t,position);
        }
    }


    public abstract void onBindItem(VH holder, T t, int position);
    public abstract void onCheckState(VH holder, T t, int position);
    public abstract void onUnCheckState(VH holder, T t, int position);


    protected void toCheck(int position){
        checkMap.put(position,true);
    }

    protected void toUnCheck(int position){
        checkMap.put(position,false);
    }

    private  boolean hasCheck(int position){
        if (!checkMap.containsKey(position))return false;
        return checkMap.get(position);
    }


}
