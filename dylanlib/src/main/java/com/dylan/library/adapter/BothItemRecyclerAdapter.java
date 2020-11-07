package com.dylan.library.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dylan.library.callback.IRecyclerAdapterDataBinder;

import java.util.ArrayList;
import java.util.List;

public abstract class BothItemRecyclerAdapter<T> extends RecyclerView.Adapter implements IRecyclerAdapterDataBinder {
    private static final int ITEM_SECOND = -1;//装饰item
    private static final int ITEM_ONE = 0; //内容item
    private List<T> mDataList;
    private Context mContext;
    private LayoutInflater mInflater;


    public void bind(List<T> list) {
        if (list != null) {
            mDataList = list;
            notifyDataSetChanged();
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
            mInflater = LayoutInflater.from(mContext);
        }


        if (viewType == ITEM_ONE) {
            return onCreateOneViewHolder(viewGroup, viewType);
        } else {
            return onCreateSecondViewHolder(viewGroup, viewType);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (isOneItem(position)) {
            onBindOneViewHolder(viewHolder, mDataList.get(position), position);
        } else {
            onBindSecondViewHolder(viewHolder,mDataList.get(position), position);
        }
    }


    public abstract boolean isOneItem(int position);

    public abstract RecyclerView.ViewHolder onCreateOneViewHolder(ViewGroup viewGroup, int viewType);

    public abstract RecyclerView.ViewHolder onCreateSecondViewHolder(ViewGroup viewGroup, int viewType);

    public abstract void onBindOneViewHolder(RecyclerView.ViewHolder viewHolder, T t, int position);

    public abstract void onBindSecondViewHolder(RecyclerView.ViewHolder viewHolder, T t, int position);


    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        if (!isOneItem(position)) return ITEM_SECOND;
        else return ITEM_ONE;
    }


    public LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public void startActivity(Intent intent) {
        mContext.startActivity(intent);
    }

    public void startSingleTopActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }

    public void startSingleTopActivity(Class clazz) {
        Intent intent = new Intent(mContext, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(intent);
    }


    @Override
    public void hookBind(List list) {
        bind(list);
    }

    @Override
    public void hookAddAllAndNotifyDataChanged(List list) {
        addAllAndNotifyDataChanged(list);
    }

    @Override
    public int hookGetItemCount() {
        return getItemCount();
    }

    @Override
    public void hookClear() {
        clear();
    }


    public void clear() {
        mDataList = null;
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

    public void loadMore(List<T> list) {
        if (list != null && list.size() > 0) {
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }
}
