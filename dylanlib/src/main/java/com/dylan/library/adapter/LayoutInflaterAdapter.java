package com.dylan.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dylan.library.proguard.NotProguard;
import com.dylan.library.utils.EmptyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2022/4/3
 * Desc:
 */

public abstract  class LayoutInflaterAdapter<T> {
    @NotProguard
    protected Context mContext;
    private List<T> dataList;
    private int selectPos =0;
    HashMap<T, ViewHolder> holderHashMap=new HashMap<>();
    private ViewGroup mParentView;

    public void setInitSelectPosition(int position){
        this.selectPos =position;
    }
    public void setSelect(int position){
        this.selectPos =position;
        notifyDataSetChanged();
    }

    public boolean isItemSelected(int position){
        return selectPos ==position;
    }


    public void notifyDataSetChanged(){
        if (getCount()>0&& EmptyUtils.isNotEmpty(dataList)){
            for (int i=0;i<dataList.size();i++){
                ViewHolder holder=holderHashMap.get(dataList.get(i));
                if (holder==null){
                    holder=createViewHolder(mParentView,i);
                    View child = holder.itemView;
                    mParentView.addView(child,i);
                }
                onBindViewHolder(holder,dataList.get(i),i);
            }

            if (dataList.size()!=holderHashMap.size()){
                List<T> waitDelete=new ArrayList<>();
                for (T t:holderHashMap.keySet()){
                   if (!dataList.contains(t)){
                       waitDelete.add(t);
                   }
                }
                for (T t:waitDelete){
                    ViewHolder holder= holderHashMap.remove(t);
                    mParentView.removeView(holder.itemView);
                }
            }
        }else{
            if (mParentView!=null)mParentView.removeAllViews();
            holderHashMap.clear();
        }
    }

    public LayoutInflaterAdapter(List<T> dataList){
        this.dataList=dataList;
    }

    public ViewHolder createViewHolder(ViewGroup parentView, int position){
        if (mParentView==null){
            mParentView=parentView;
            mContext=mParentView.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(getLayoutId(),parentView,false);
        ViewHolder holder=new ViewHolder(view);
        holderHashMap.put(dataList.get(position),holder);
        return holder;
    }

    public Context getContext() {
        return mContext;
    }


    public List<T> getDataList() {
        return dataList;
    }

    public void clear(){
      if (EmptyUtils.isNotEmpty(dataList))dataList.clear();
      notifyDataSetChanged();
    }

    public abstract int getLayoutId();


    public abstract void onBindViewHolder(ViewHolder holder, T t, int position);

    public int getCount() {
        return (dataList == null || dataList.size() == 0) ? 0 : dataList.size();
    }



     public void  bindViewGroup(ViewGroup viewGroup){
        if (viewGroup==null)return;
        viewGroup.removeAllViews();
        int n = getCount();
        for (int i = 0; i < n; i++) {
            LayoutInflaterAdapter.ViewHolder viewHolder=createViewHolder(viewGroup,i);
            onBindViewHolder( viewHolder, getDataList().get(i), i);
            View child = viewHolder.itemView;
            viewGroup.addView(child);
        }
    }





    public  static class ViewHolder {
        public final View itemView;

        /**
         * @param itemView
         */
        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }

        public <T extends View> T findViewById(int id){
            return itemView.findViewById(id);
        }

    }



}