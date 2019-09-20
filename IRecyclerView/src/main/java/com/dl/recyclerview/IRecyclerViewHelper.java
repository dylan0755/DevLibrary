package com.dl.recyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dl.recyclerview.footer.LoadMoreFooterView;


/**
 * Author: Dylan
 * Date: 2019/3/2
 * Desc:
 */

public class IRecyclerViewHelper {

    public static boolean isCanLoadMore(IRecyclerView recyclerView, LoadMoreFooterView loadMoreFooterView, RecyclerView.Adapter adapter) {
        if (recyclerView == null || loadMoreFooterView == null ||
                adapter == null) return false;
        if (loadMoreFooterView.canLoadMore() && adapter.getItemCount() > 0) {
            if (((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() > 2) {
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                return true;
            } else {
                loadMoreFooterView.setVisibility(View.GONE);
            }
        }
        return false;
    }

    public static void setRefreshStatus(LoadMoreFooterView loadMoreFooterView) {
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
    }

    public static void refreshComplete(IRecyclerView recyclerView){
        recyclerView.setRefreshing(false);
    }

    public static void loadMoreComplete(LoadMoreFooterView loadMoreFooterView) {
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
    }

    public static void setNoMore(LoadMoreFooterView loadMoreFooterView) {
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
    }


}
