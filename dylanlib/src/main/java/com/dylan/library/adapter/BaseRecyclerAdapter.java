package com.dylan.library.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import com.dylan.library.callback.IRecyclerAdapterDataBinder;
import com.dylan.library.exception.ThrowableUtils;
import com.dylan.library.proguard.NotProguard;
import com.dylan.library.utils.ContextUtils;
import com.dylan.library.utils.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Dylan on 2017/1/11.
 */


public abstract class BaseRecyclerAdapter<T, VH extends BaseRecyclerAdapter.ViewHolder> extends RecyclerView.Adapter implements IRecyclerAdapterDataBinder {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<T> mDataList;
    private Constructor<VH> mSubConstrutor;
    @NotProguard
    protected OnItemClickListener mItemClickListener;
    private int selectPos;





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
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
            mInflater = LayoutInflater.from(mContext);
            attachContext(mContext);
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

    public abstract
    int getLayoutId();

    public abstract void onBindViewHolder(VH holder, T t, int position);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        T t = mDataList.get(position);
        if (t != null) {
            try {
                onBindViewHolder(((VH) holder), t, position);
            }catch (Exception e){
                ThrowableUtils.show(e);
            }
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
        return (mSubConstrutor == null || mDataList == null) ? 0 : (mDataList.size());
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

    public void startActivityForResult(Intent intent,int requestCode){
        if (mContext==null)return;
        ContextUtils.getActivity(mContext).startActivityForResult(intent,requestCode);
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



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

    public interface OnItemClickListener<T> {
        void onClick(T t, int position);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mItemClickListener = listener;
    }



}
