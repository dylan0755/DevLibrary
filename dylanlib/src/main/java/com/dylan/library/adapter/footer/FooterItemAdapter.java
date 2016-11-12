package com.dylan.library.adapter.footer;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dylan.library.widget.RefreshRecyclerView;

import java.util.List;



/**
 * @author Dylan
 */
public abstract class FooterItemAdapter<T, VH extends RecyclerItemViewHolder> extends RecyclerView.Adapter {
    public static final int TYPE_ITEM = 10; //普通数据item
    public static final int TYPE_FOOTER = 11; //footer item
    protected Context context;
    private LayoutInflater mInflater;
    private RefreshRecyclerView mRecyclerView;
    private List<T> mDataList;
    private LoadStateListener mLoadStateListener;
    private String TAG;

    public FooterItemAdapter(Context context, RefreshRecyclerView recyclerView, List<T> list, LoadStateListener listener) {
        TAG=context.getClass().getSimpleName();
        if (context == null) {
            Log.e(TAG, "FooterItemAdapter: " + "context ");
            return;
        }
        if (recyclerView == null) {
            Log.e(TAG, "FooterItemAdapter: " + "recyclerView ");
            return;
        }
        this.context = context;
        mRecyclerView = recyclerView;
        mDataList = list;
        mLoadStateListener = listener;
        hideFooter();
        try {
            mInflater = LayoutInflater.from(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSpanCount(recyclerView.getLayoutManager());
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return onCreateItemViewHolder(mInflater, parent);
        } else if (viewType == TYPE_FOOTER) {
            return createFooter(context);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!(viewHolder instanceof FooterItemAdapter.FooterViewHolder)) {
            onBindItemViewHolder((VH) viewHolder, getItem(position), position);
        }

    }

    public abstract void onBindItemViewHolder(VH holder, T t, int position);

    public abstract VH onCreateItemViewHolder(LayoutInflater inflater, ViewGroup parent);


    public T getItem(int position) {
        T t = mDataList.get(position);
        return t;
    }

    public void update(List<T> list) {
        mDataList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return (mDataList.size() + 1);
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }


    public void loadmore(List<T> list) {
        mDataList.addAll(list);
        if (mRecyclerView != null) mRecyclerView.setCanLoadMore(true);
        hideFooter();
        notifyDataSetChanged();
    }

    public void showFooter() {
        if (mLoadStateListener != null) mLoadStateListener.show();
    }

    public void showNetWorkBreak(){
        if (mLoadStateListener!=null)mLoadStateListener.netWorkBreak();
    }

    public void showNoMore() {
        if (mLoadStateListener != null) {
            boolean flag = mLoadStateListener.getContentView().getVisibility() == View.VISIBLE;
            if (!flag) showFooter();
            mLoadStateListener.onLoadNothing();
        }
        if (mRecyclerView != null) mRecyclerView.setCanLoadMore(false);
    }


    public void hideFooter() {
        if (mLoadStateListener != null) mLoadStateListener.hide();
        if (mRecyclerView != null) mRecyclerView.setCanLoadMore(true);
    }


    public RecyclerItemViewHolder createFooter(Context context) {
        if (mLoadStateListener != null && mLoadStateListener.getContentView() != null) {
            return new FooterViewHolder(mLoadStateListener.getContentView());
        } else {
            return new FooterViewHolder(new View(context));
        }
    }


    private void getSpanCount(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager manager = (GridLayoutManager) layoutManager;
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (getItemViewType(position)) {
                        case TYPE_ITEM:
                            return 1;
                        case TYPE_FOOTER:
                            return manager.getSpanCount();
                        default:
                            return -1;
                    }
                }
            });

        }

    }


    class FooterViewHolder extends RecyclerItemViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
