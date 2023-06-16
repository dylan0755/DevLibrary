package com.dylan.library.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.dylan.library.R;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public  abstract class ChatAdapter<T>
        extends RecyclerView.Adapter<ChatAdapter.ViewHolder<T>>{

    private List<T> mDataList;

    public ChatAdapter(List<T> list) {
      this.mDataList=list;
    }


    // 同样的RecyclerView每个子项布局在基类中也不确定，需要通过定义抽象方法交给子类实现从而告诉我
    // 这个抽象方法返回的是子项布局的layout资源id
    public abstract int getItemViewType(int position, T data,boolean isFooter);

    @Override
    public int getItemViewType(int position) {
        // 调用抽象方法
        // 之所以给这个抽象方法加上一个Message data，以便子类需要
        boolean isFooter=position==mDataList.size();
        return getItemViewType(position, !isFooter?mDataList.get(position):null,isFooter);
    }

    // 我们需要的ViewHolder是子类中的ViewHolder，每个子类都不一样
    // 所以通过抽象方法交给子类去new，然后再返回给作为基类的我
    public abstract ViewHolder<T> onCreateViewHolder(View itemView, int viewType);

    @NonNull
    @Override
    public ViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        ViewHolder<T> holder = onCreateViewHolder(itemView, viewType);
        itemView.setTag(R.id.tag_recycler_holder, holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder<T> holder, int position) {
        if (position<=mDataList.size()-1){
            T data = mDataList.get(position);
            holder.bind(data);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size()+1;
    }



    /**
     * 自定义的ViewHolder
     *
     * @param <Message> RecyclerView每个子项布局所对应的数据类型
     */
    public static abstract class ViewHolder<Message> extends RecyclerView.ViewHolder {
        // 该数据有可能在子类中用到，所以为protected
        protected Message mData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Message data) {
            // 这个mData在这里被赋值了
            mData = data;
            onBind(data);
        }

        // 不一样的子项布局，里面的控件不一样，那么在为控件渲染数据的时候操作也不不一样
        // 只能通过抽象方法交给子类完成
        protected abstract void onBind(Message data);
    }


    public List<T> getDataList() {
        return mDataList;
    }

    public void add(T data) {
        mDataList.add(data);
        notifyItemInserted(mDataList.size() - 1);
    }

    public void add(T... dataList) {
        if (dataList != null && dataList.length > 0) {
            Collections.addAll(mDataList, dataList);
            notifyItemRangeInserted(mDataList.size(), dataList.length);
        }
    }

    // 插入一堆数据，并通知这段集合更新
    public void add(Collection<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            mDataList.addAll(dataList);
            notifyItemRangeInserted(mDataList.size(), dataList.size());
        }
    }

    // 全部删除
    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    // 替换为一个新的集合
    public void replace(Collection<T> dataList) {
        mDataList.clear();
        if (dataList == null || dataList.size() == 0) {
            return;
        }

        mDataList.addAll(dataList);
        notifyDataSetChanged(); // 通知全部更新
    }

}
