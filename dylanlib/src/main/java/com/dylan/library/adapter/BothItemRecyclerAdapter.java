package com.dylan.library.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dylan.library.proguard.NotProguard;

import java.util.List;

public abstract class BothItemRecyclerAdapter<T>  extends RecyclerView.Adapter{
    private   static final int ITEM_DECORATION = -1;//装饰item
    private  static final int ITEM_NORMAL = 0; //内容item
    private List<T> dataList;
    private Context mContext;
    private LayoutInflater mInflater;


    public void bind(List<T> list) {
        if (list != null) {
            dataList = list;
            notifyDataSetChanged();
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (mContext == null) {
            mContext=viewGroup.getContext();
            mInflater = LayoutInflater.from(mContext);
        }


        if (viewType== ITEM_NORMAL){
            return onCreateNormalViewHolder(viewGroup,viewType);
        }else{
            return onCreateDecorationViewHolder(viewGroup,viewType);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
       if (isNormalItem(position)){
           onBindNormalViewHolder(viewHolder,dataList.get(position),position);
       }else{
           onBindDecorationViewHolder(viewHolder,position);
       }
    }




    public abstract boolean isNormalItem(int position);

    public abstract RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup viewGroup, int viewType);

    public abstract RecyclerView.ViewHolder onCreateDecorationViewHolder(ViewGroup viewGroup, int viewType);

    public abstract void onBindNormalViewHolder(RecyclerView.ViewHolder viewHolder,T t, int position);
    public abstract void onBindDecorationViewHolder(RecyclerView.ViewHolder viewHolder, int position);



    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }



    @Override
    public long getItemId(int position) {
        return position;
    }




    @Override
    public int getItemViewType(int position) {
        if (!isNormalItem(position)) return ITEM_DECORATION;
        else return ITEM_NORMAL;
    }


    public LayoutInflater getLayoutInflater(){
        return mInflater;
    }

    public Context getContext(){
        return mContext;
    }

    public List<T> getDataList(){
        return dataList;
    }

    public void startActivity(Intent intent){
        mContext.startActivity(intent);
    }

    public void startSingleTopActivity(Intent intent){
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    public void startSingleTopActivity(Class clazz){
        Intent intent=new Intent(mContext,clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }


}
