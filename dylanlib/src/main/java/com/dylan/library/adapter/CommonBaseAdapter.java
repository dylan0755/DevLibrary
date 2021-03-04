package com.dylan.library.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.dylan.library.proguard.NotProguard;
import com.dylan.library.utils.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * @author Dylan
 */

public abstract class CommonBaseAdapter<T, VH extends CommonBaseAdapter.ViewHolder> extends BaseAdapter {
    @NotProguard
    protected Context mContext;
    private LayoutInflater mInflater;
    private List<T> dataList;
    private SimpleDateFormat mDateFormatter;
    private Constructor<VH> mSubConstrutor;


    public CommonBaseAdapter() {
        initDateFormatter();
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType genericSuperclass = (ParameterizedType) type;//获得真正的父类
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


    public Context getContext() {
        return mContext;
    }

    protected Resources getResources() {
        if (mContext != null) return mContext.getResources();
        return null;
    }


    protected String getString(int strId) {
        if (mContext == null) return "";
        else return mContext.getString(strId);
    }

    public void bind(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }


    public void clear() {
        dataList = null;
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void initDateFormatter() {
        mDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public int getCount() {
        return (mSubConstrutor == null || dataList == null || dataList.size() == 0) ? 0 : dataList.size();
    }

    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mContext == null) {
            mContext = parent.getContext();
            mInflater = LayoutInflater.from(mContext);
            attachContext(mContext);
        }

        VH holder = null;
        if (convertView == null) {
            try {
                View view = mInflater.inflate(getLayoutId(), parent, false);
                holder = mSubConstrutor.newInstance(this, view);
                convertView = view;
                convertView.setTag(holder);
            } catch (Exception e) {
                Logger.e(e);
            }
        } else {
            holder = (VH) convertView.getTag();
        }
        if (getItem(position) != null) {
            onBindViewHolder(holder, getItem(position), position);
        }
        return convertView;
    }


    protected void attachContext(Context context){

    }



    public abstract @LayoutRes
    int getLayoutId();

    public abstract void onBindViewHolder(VH holder, T t, int position);


    public static class ViewHolder {


        private View convertView;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
        }

        public View findViewById(int id) {
            View view = convertView.findViewById(id);
            return view;
        }

        public View getConvertView() {
            return convertView;
        }

    }
}




