package com.dylan.library.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;


/**
 * Created by Dylan on 2017/1/11.
 */

public abstract class BaseRecyclerAdapter<T,VH extends BaseRecyclerAdapter.ViewHolder> extends RecyclerView.Adapter {
    protected Context context;
    private LayoutInflater mInflater;
   private List<T> mDataList;

    public BaseRecyclerAdapter(){

    }



    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context==null){
            context=parent.getContext();
            mInflater= LayoutInflater.from(context);
        }

        return onCreateItemHolder(mInflater,parent,viewType);
    }

    public  abstract VH onCreateItemHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(VH holder,T t,int position);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        T t=mDataList.get(position);
        if (t!=null){
            onBindViewHolder(((VH)holder),t,position);
        }

    }

    public T getItem(int position) {
        T t = mDataList.get(position);
        return t;
    }



    public void bind(List<T> list) {
        if (list!=null&&list.size()>0){
            mDataList = list;
            notifyDataSetChanged();
        }
    }


    public void clear(){
        if (mDataList!=null){
            mDataList.clear();
            notifyDataSetChanged();
        }
    }

    public void loadMore(List<T> list){
        if (list!=null&&list.size()>0){
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }


    public boolean isEmpty(){
        return (mDataList==null||mDataList.size()==0)?true:false;
    }

    @Override
    public int getItemCount() {
        return mDataList==null?0:(mDataList.size());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }



    }
}
