package com.dylan.library.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.dylan.library.callback.IRecyclerAdapterDataBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Dylan on 2017/1/11.
 */


public abstract class RecycleViewBindingAdapter<T> extends RecyclerView.Adapter implements IRecyclerAdapterDataBinder {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<T> mDataList;
    protected OnItemClickListener mItemClickListener;
    private int selectPos;





    public RecycleViewBindingAdapter() {

    }


    protected void attachContext(Context context){

    }


    public void setInitSelectItemPos(int pos){
        this.selectPos = pos;
    }

    public void setSelectItemPos(int selectPos) {
        this.selectPos = selectPos;
        notifyDataSetChanged();
    }

    public int getSelectItemPos(){
        return selectPos;
    }


    public boolean isItemSelected(int position){
        return selectPos==position;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
            mInflater = LayoutInflater.from(mContext);
            attachContext(mContext);
        }
        return getViewHolder(mInflater,parent);
    }

    public abstract ItemViewHolder getViewHolder(LayoutInflater layoutInflater,ViewGroup parent);

    public abstract void onBindViewHolder(ItemViewHolder holder, T t, int position);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        T t = mDataList.get(position);
        if (t != null) {
            onBindViewHolder((ItemViewHolder) holder, t, position);
        }

    }

    public T getItem(int position) {
        T t = mDataList.get(position);
        return t;
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




    public void clear() {
        mDataList = null;
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

    @Override
    public boolean isEmpty() {
        return (mDataList == null || mDataList.size() == 0) ? true : false;
    }



    @Override
    public int getItemCount() {
        return (mDataList == null) ? 0 : (mDataList.size());
    }


    public Context getContext() {
        return mContext;
    }

    public LayoutInflater getLayoutInflater() {
        return mInflater;
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
    public void hookClear() {
        clear();
    }

    @Override
    public int hookGetItemCount() {
        return getItemCount();
    }


    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(getDataList(), fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void onItemDismiss(int position) {
        getDataList().remove(position);
        notifyItemRemoved(position);
    }



    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ViewBinding binding;
        public ItemViewHolder(ViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public ViewBinding getBinding() {
            return binding;
        }
    }

    public interface OnItemClickListener<T> {
        void onClick(T t, int position);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mItemClickListener = listener;
    }




}
