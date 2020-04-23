package com.dylan.library.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.dylan.library.callback.IRecyclerAdapterDataBinder;
import com.dylan.library.proguard.NotProguard;
import com.dylan.library.utils.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dylan on 2017/1/11.
 */


public  abstract class BaseRecyclerAdapter<T,VH extends BaseRecyclerAdapter.ViewHolder> extends RecyclerView.Adapter implements IRecyclerAdapterDataBinder {
    @NotProguard
    protected Context mContext;
    private LayoutInflater mInflater;
    private List<T> mDataList;
    private Constructor<VH> mSubConstrutor;
    @NotProguard
    protected OnItemClickListener mClickListener;

    public BaseRecyclerAdapter() {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType genericSuperclass = (ParameterizedType) type;
            Type[] types = genericSuperclass.getActualTypeArguments();
            Class<VH> claszz = (Class<VH>) types[1];
            //因为是内部类，反射要传入外部类对象
            try {
                mSubConstrutor = claszz.getDeclaredConstructor(getClass(), View.class);
                mSubConstrutor.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Logger.e(e);
            }
        }
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
            mInflater = LayoutInflater.from(mContext);
        }
        View convertView = mInflater.inflate(getLayoutId(), parent, false);
        try {
            VH holder = mSubConstrutor.newInstance(this, convertView);
            return holder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract @LayoutRes
    int getLayoutId();

    public abstract void onBindViewHolder(VH holder, T t, int position);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        T t = mDataList.get(position);
        if (t != null) {
            onBindViewHolder(((VH) holder), t, position);
        }

    }

    public T getItem(int position) {
        T t = mDataList.get(position);
        return t;
    }

    public Context getContext() {
        return mContext;
    }

    public void bind(List<T> list) {
        mDataList = list;
        notifyDataSetChanged();
    }

    public void addAllAndNotifyDataChanged(List<T> list) {
        if (mDataList == null) {
            mDataList = list;
        } else {
            mDataList.addAll(list);
        }
        notifyDataSetChanged();
    }


    public void notifyItemRangeChanged(List<T> list) {
        if (list != null) {
            if (mDataList == null) {
                mDataList = new ArrayList<>();
                mDataList.addAll(list);
                notifyItemRangeChanged(0, list.size());
            } else {
                int start = getDataList().size();
                getDataList().addAll(list);
                super.notifyItemRangeChanged(start, list.size());
            }
        }

    }



    public void clear(){
        mDataList=null;
        notifyDataSetChanged();
    }

    public void loadMore(List<T> list) {
        if (list != null && list.size() > 0) {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public List<T> getDataList() {
        return mDataList;
    }


    public boolean isEmpty() {
        return (mDataList == null || mDataList.size() == 0) ? true : false;
    }

    @Override
    public int getItemCount() {
        return (mSubConstrutor == null || mDataList == null) ? 0 : (mDataList.size());
    }

    @Override
    public void hookBind(List list) {
        bind(list);
    }


    @Override
    public void hookAddAllAndNotifyDataChanged(List list){
            addAllAndNotifyDataChanged(list);
    }
    @Override
    public void hookClear(){
        clear();
    }
    @Override
    public int hookGetItemCount(){
        return getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

    public interface OnItemClickListener<T> {
        void onClick(T t,int position);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener){
         mClickListener=listener;
    }


}
