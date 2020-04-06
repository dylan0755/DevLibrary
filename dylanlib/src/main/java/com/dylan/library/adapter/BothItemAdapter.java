package com.dylan.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Dylan on 2017/3/29.
 */

public abstract class BothItemAdapter<T> extends BaseAdapter {
    public  static final int ITEM_LABEL = -1;
    public  static final int ITEM_CONTENT = 0;
    protected List<T> dataList;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public void bind(List<T> list) {
        if (list != null) {
            dataList = list;
            notifyDataSetChanged();
        }

    }

    public abstract boolean isNormalItem(int position);

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
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
            mContext=parent.getContext();
            mInflater = LayoutInflater.from(mContext);
        }

        int viewtype = getItemViewType(position);
        if (ITEM_LABEL == viewtype) {
            convertView=getViewAsTagItem(convertView, getItem(position), parent, position);
        } else {
            convertView=getViewAsNormalItem(convertView, getItem(position), parent, position);
        }
        return convertView;
    }

    public abstract View getViewAsTagItem(View convertView, T t, ViewGroup parent, int position);

    public abstract View getViewAsNormalItem(View convertView, T t, ViewGroup parent, int position);


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (!isNormalItem(position)) return ITEM_LABEL;
        else return ITEM_CONTENT;
    }


}
