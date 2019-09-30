package com.dylan.library.widget.irecycler;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dylan.library.adapter.BaseRecyclerAdapter;
import com.dylan.library.exception.OnNextBussinesException;
import com.dylan.library.exception.ThrowableUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.widget.CircleIndicatorView;
import com.dylan.library.widget.irecycler.footer.LoadMoreFooterView;
import com.dylan.library.widget.irecycler.header.RefreshHeaderView;

import java.util.List;


/**
 * Author: Dylan
 * Date: 2019/3/2
 * Desc:
 */

public class IRecyclerHelper {
    private int firstPageNo = 1;
    private int pageNo = firstPageNo;
    private IRecyclerView recyclerView;
    private RefreshHeaderView headerView;
    private LoadMoreFooterView footerView;
    private TextView tvEmptyView;
    private BaseRecyclerAdapter mAdapter;
    private CharSequence emptyTip = "暂无数据";


    public int getPageNo() {
        return pageNo;
    }

    public void bind(IRecyclerView recyclerView, BaseRecyclerAdapter adapter, TextView tvEmptyView) {
        this.recyclerView = recyclerView;
        this.tvEmptyView = tvEmptyView;
        footerView = (LoadMoreFooterView) recyclerView.getLoadMoreFooterView();
        headerView = (RefreshHeaderView) recyclerView.getRefreshHeaderView();
        mAdapter = adapter;
    }

    public void setEmptyTip(CharSequence charSequence) {
        if (charSequence == null) return;
        emptyTip = charSequence;
    }

    public void setFirstPageNo(int pageNo) {
        firstPageNo = pageNo;
    }

    public void setRefreshStatus() {
        pageNo = 1;
        IRecyclerHelper.setRefreshStatus(footerView);

    }

    public boolean isCanLoadMore() {
        if (IRecyclerHelper.isCanLoadMore(recyclerView, footerView, mAdapter)) {
            pageNo++;
            return true;
        }
        return false;
    }

    public void afterGetData(boolean isSucceed, Object errorMsg, List<?> list) {
        if (isSucceed) {
            if (EmptyUtils.isNotEmpty(list)) {
                if (tvEmptyView != null) tvEmptyView.setText("");
                if (pageNo == 1) {
                    mAdapter.bind(list);
                    IRecyclerHelper.refreshComplete(recyclerView);
                } else {
                    mAdapter.addAllAndNotifyDataChanged(list);
                    if (footerView != null) {
                        IRecyclerHelper.loadMoreComplete(footerView);
                    }
                }
            } else {
                if (pageNo == 1) {
                    IRecyclerHelper.refreshComplete(recyclerView);
                    mAdapter.clear();
                    if (tvEmptyView != null) tvEmptyView.setText(emptyTip);
                } else {
                    IRecyclerHelper.setNoMore(footerView);
                }
            }
        } else {
            if (errorMsg == null) return;
            if (errorMsg instanceof OnNextBussinesException) {
                ThrowableUtils.show(((OnNextBussinesException) errorMsg).target);
            } else {
                if (pageNo > firstPageNo) {
                    pageNo--;
                    IRecyclerHelper.setError(footerView);
                } else {
                    IRecyclerHelper.setRefreshStatus(footerView);
                }
                ThrowableUtils.show(errorMsg);
            }

        }
    }


    public void setNoMoreTextColor(int colorValue) {
        if (footerView == null) return;
        if (footerView.getNoMoreTextView() != null)
            footerView.getNoMoreTextView().setTextColor(colorValue);
    }


    public void setLoadingTextColor(int colorValue) {
        if (footerView == null) return;
        if (footerView.getLoadingTextView() != null)
            footerView.getLoadingTextView().setTextColor(colorValue);
    }

    public void setErrorTextColor(int colorValue) {
        if (footerView == null) return;
        if (footerView.getErrorTextView() != null)
            footerView.getErrorTextView().setTextColor(colorValue);
    }


    public void setNoMoreTextSize(int sp) {
        if (footerView == null) return;
        if (footerView.getNoMoreTextView() != null)
            footerView.getNoMoreTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }


    public void setLoadingTextSize(int sp) {
        if (footerView == null) return;
        if (footerView.getLoadingTextView() != null)
            footerView.getLoadingTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }


    public void setErrorTextSize(int sp) {
        if (footerView == null) return;
        if (footerView.getErrorTextView() != null)
            footerView.getErrorTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }

    public void setRefreshTextSize(int sp) {
        if (headerView == null) return;
        if (headerView.getRefreshTextView() == null) return;
        headerView.getRefreshTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }


    public ProgressBar getLoadingProgressBar() {
        if (footerView == null) return null;
        return footerView.getLoadingProgressBar();
    }


    public void tintLoadingProgressBar(int colorValue) {
        if (footerView == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getLoadingProgressBar().setIndeterminateTintList(ColorStateList.valueOf(colorValue));
            getLoadingProgressBar().setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
        }

    }


    public void setRefreshTextViewColor(int color) {
        if (headerView == null) return;
        if (headerView.getRefreshTextView() == null) return;
        headerView.getRefreshTextView().setTextColor(color);
    }


    public void setRefreshCircleIndicatorViewColor(int innerRingColor, int outRingColor) {
        if (headerView == null) return;
        if (headerView.getIndicatorView() == null) return;
        CircleIndicatorView indicatorView = headerView.getIndicatorView();
        indicatorView.setInnerRingColor(innerRingColor);
        indicatorView.setOutRingColor(outRingColor);
    }


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
        if (loadMoreFooterView == null) return;
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
    }

    public static void refreshComplete(IRecyclerView recyclerView) {
        recyclerView.setRefreshing(false);
    }

    public static void loadMoreComplete(LoadMoreFooterView loadMoreFooterView) {
        if (loadMoreFooterView == null) return;
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
    }

    public static void setNoMore(LoadMoreFooterView loadMoreFooterView) {
        if (loadMoreFooterView == null) return;
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
    }

    public static void setError(LoadMoreFooterView loadMoreFooterView) {
        if (loadMoreFooterView == null) return;
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
    }


}
