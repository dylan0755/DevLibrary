package com.dylan.library.widget.irecycler;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.dylan.library.adapter.BaseRecyclerAdapter;
import com.dylan.library.callback.IRecyclerAdapterDataBinder;
import com.dylan.library.exception.OnNextBussinesException;
import com.dylan.library.exception.ThrowableUtils;
import com.dylan.library.utils.ArrayUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.RecyclerViewHelper;
import com.dylan.library.widget.CircleRingProgressView;
import com.dylan.library.widget.irecycler.footer.LoadMoreFooterView;
import com.dylan.library.widget.irecycler.header.RefreshHeaderView;
import com.dylan.library.widget.irecycler.paging.IRecyclerPage;
import com.hjq.toast.Toaster;

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
    private View emptyView;
    private IRecyclerAdapterDataBinder mAdapterBinder;
    private CharSequence emptyTip = "暂无数据";
    private RecyclerView.AdapterDataObserver recyclerDataObserver;


    public int getPageNo() {
        return pageNo;
    }

    public void bind(IRecyclerView recyclerView, IRecyclerAdapterDataBinder adapterBinder, TextView tvEmptyView) {
        this.recyclerView = recyclerView;
        this.tvEmptyView = tvEmptyView;
        footerView = (LoadMoreFooterView) recyclerView.getLoadMoreFooterView();
        headerView = (RefreshHeaderView) recyclerView.getRefreshHeaderView();
        mAdapterBinder = adapterBinder;
        registerAdapterDataChanged();
    }

    public void bind(IRecyclerView recyclerView, IRecyclerAdapterDataBinder adapterBinder, View emptyView) {
        this.recyclerView = recyclerView;
        this.emptyView = emptyView;
        footerView = (LoadMoreFooterView) recyclerView.getLoadMoreFooterView();
        headerView = (RefreshHeaderView) recyclerView.getRefreshHeaderView();
        mAdapterBinder = adapterBinder;
        registerAdapterDataChanged();
    }


    private void registerAdapterDataChanged() {
        if (mAdapterBinder instanceof BaseRecyclerAdapter) {
            BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) mAdapterBinder;
            recyclerDataObserver = new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    if (mAdapterBinder.isEmpty()){
                        showEmptyView();
                    }else{
                        hideEmptyView();
                    }

                }
            };
            adapter.registerAdapterDataObserver(recyclerDataObserver);
        }
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
        setRefreshStatus(footerView);

    }


    public void completeRefresh() {
        if (recyclerView != null) refreshComplete(recyclerView);
    }

    public void completeLoadMore() {
        if (footerView != null) loadMoreComplete(footerView);
    }

    public boolean isCanLoadMore() {
        if (isCanLoadMore(recyclerView, footerView, mAdapterBinder)) {
            pageNo++;
            return true;
        }
        return false;
    }

    public void afterGetData(Throwable throwable, IRecyclerPage page) {
        if (page == null) return;
        List<?> list = page.getList();
        boolean isLastPage = page.isLastPage();
        afterGetData(throwable, page.isSucceed(), page.getFailureMsg(), list, isLastPage);
    }

    public void afterGetData(Throwable throwable, boolean isSucceed, String failureMsg, List<?> list) {
        afterGetData(throwable, isSucceed, failureMsg, list, false);
    }

    public void afterGetData(Throwable throwable, boolean isSucceed, String failureMsg, List<?> list, boolean isLastPage) {
        if (throwable != null) {
            if (throwable instanceof OnNextBussinesException) {
                ThrowableUtils.show(((OnNextBussinesException) throwable).target);
            } else {
                if (pageNo > firstPageNo) {
                    pageNo--;
                    setError(footerView);
                } else {
                    completeRefresh();
                    completeLoadMore();
                }
                ThrowableUtils.show(throwable);
            }
        } else {
            if (EmptyUtils.isNotEmpty(list)) {
                if (tvEmptyView != null) tvEmptyView.setText("");
                if (emptyView != null) emptyView.setVisibility(View.GONE);
                if (pageNo == 1) {
                    mAdapterBinder.hookBind(list);
                    refreshComplete(recyclerView);
                } else {
                    mAdapterBinder.hookAddAllAndNotifyDataChanged(list);
                    if (footerView != null) {
                        if (isLastPage) {
                            setNoMore(footerView);
                        } else {
                            loadMoreComplete(footerView);
                        }

                    }
                }
            } else {
                if (pageNo == 1) {
                    refreshComplete(recyclerView);
                    mAdapterBinder.hookClear();
                    showEmptyView();
                } else {
                    setNoMore(footerView);
                }

                if (!isSucceed) {
                    if (EmptyUtils.isNotEmpty(failureMsg)) {
                        Toaster.show(failureMsg);
                    }
                }
            }

        }

    }


    private void showEmptyView() {
        if (tvEmptyView != null) tvEmptyView.setText(emptyTip);
        if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView(){
        if (tvEmptyView != null) tvEmptyView.setText("");
        if (emptyView != null) emptyView.setVisibility(View.GONE);
    }


    public void setNoMoreText(String text) {
        if (footerView != null) footerView.getNoMoreTextView().setText(text);
    }


    public void setLoadingText(String text) {
        if (footerView != null) footerView.getLoadingTextView().setText(text);
    }


    public void setErrorTextColor(String text) {
        if (footerView != null) footerView.getErrorTextView().setText(text);
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
        CircleRingProgressView indicatorView = headerView.getIndicatorView();
        indicatorView.setRingBackgroundColor(innerRingColor);
        indicatorView.setRingProgressColor(outRingColor);
    }

    public void scrollToTop() {
        if (recyclerView == null) return;
        toStickFromPosition(recyclerView, 0, 0);
    }

    public void toStickFromPosition(int position, int offset) {
        if (recyclerView == null) return;
        toStickFromPosition(recyclerView, position, offset);
    }


    public static boolean isCanLoadMore(IRecyclerView recyclerView, LoadMoreFooterView loadMoreFooterView, IRecyclerAdapterDataBinder adapterBinder) {
        if (recyclerView == null || loadMoreFooterView == null ||
                adapterBinder == null) return false;
        if (loadMoreFooterView.canLoadMore() && adapterBinder.hookGetItemCount() > 0) {
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager && ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() > 2) {
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                return true;
            } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                int[] lastPositions = new int[((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).getSpanCount()];
                ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPositions(lastPositions);
                int lastPosition = ArrayUtils.findMax(lastPositions);
                boolean isBottom = lastPosition == recyclerView.getLayoutManager().getItemCount() - 1;

                if (isBottom) {
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                }
                return isBottom;
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


    public static void invalidateSpanAssignments(IRecyclerView recyclerView, final int state) {
        if ((recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager)) return;
        final StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
        if (manager == null) return;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (state != 0) {
                    manager.invalidateSpanAssignments();
                    recyclerView.invalidateItemDecorations();//刷新 item 之间的间隔
                } else {  //停止的时候
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        manager.invalidateSpanAssignments();
                        recyclerView.invalidateItemDecorations();//刷新 item 之间的间隔
                    }
                }
            }
        });
    }

    public static void toStickFromPosition(RecyclerView recyclerView, int position, int offset) {
        RecyclerViewHelper.toStickFromPosition(recyclerView, position, offset);
    }


    public void release() {
        if (recyclerDataObserver != null) {
            if (mAdapterBinder instanceof BaseRecyclerAdapter) {
                BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) mAdapterBinder;
                adapter.unregisterAdapterDataObserver(recyclerDataObserver);
            }
        }
    }

}
