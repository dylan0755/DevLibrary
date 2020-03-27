package com.dylan.library.widget.irecycler;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dylan.library.adapter.BaseRecyclerAdapter;
import com.dylan.library.exception.OnNextBussinesException;
import com.dylan.library.exception.ThrowableUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.widget.CircleIndicatorView;
import com.dylan.library.widget.irecycler.IRecyclerView;
import com.dylan.library.widget.irecycler.footer.LoadMoreFooterView;
import com.dylan.library.widget.irecycler.header.RefreshHeaderView;
import com.dylan.library.widget.irecycler.paging.IRecyclerPage;

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
        setRefreshStatus(footerView);

    }

    public boolean isCanLoadMore() {
        if (isCanLoadMore(recyclerView, footerView, mAdapter)) {
            pageNo++;
            return true;
        }
        return false;
    }

    public   void afterGetData(boolean isError, Throwable throwable, IRecyclerPage page){
        List  list=page==null?null:page.getList();
        boolean isLastPage=(page!=null&&page.isLastPage());
        afterGetData(isError,throwable,list,isLastPage);
    }

    public void afterGetData(boolean isError, Throwable throwable, List<?> list,boolean isLastPage) {
        if (isError) {
            if (throwable == null) return;
            if (throwable instanceof OnNextBussinesException) {
                ThrowableUtils.show(((OnNextBussinesException) throwable).target);
            } else {
                if (pageNo > firstPageNo) {
                    pageNo--;
                    setError(footerView);
                } else {
                    setRefreshStatus(footerView);
                }
                ThrowableUtils.show(throwable);
            }
        } else {
            if (EmptyUtils.isNotEmpty(list)) {
                if (tvEmptyView != null) tvEmptyView.setText("");
                if (pageNo == 1) {
                    mAdapter.bind(list);
                    refreshComplete(recyclerView);
                } else {
                    mAdapter.addAllAndNotifyDataChanged(list);
                    if (footerView != null) {
                        if (isLastPage){
                            setNoMore(footerView);
                        }else{
                            loadMoreComplete(footerView);
                        }

                    }
                }
            } else {
                if (pageNo == 1) {
                    refreshComplete(recyclerView);
                    mAdapter.clear();
                    if (tvEmptyView != null) tvEmptyView.setText(emptyTip);
                } else {
                    setNoMore(footerView);
                }
            }

        }

    }


    public void afterGetData(boolean isError, Throwable errorMsg, List<?> list) {
        if (isError) {
            if (errorMsg == null) return;
            if (errorMsg instanceof OnNextBussinesException) {
                ThrowableUtils.show(((OnNextBussinesException) errorMsg).target);
            } else {
                if (pageNo > firstPageNo) {
                    pageNo--;
                    setError(footerView);
                } else {
                    setRefreshStatus(footerView);
                }
                ThrowableUtils.show(errorMsg);
            }
        } else {
            if (EmptyUtils.isNotEmpty(list)) {
                if (tvEmptyView != null) tvEmptyView.setText("");
                if (pageNo == 1) {
                    mAdapter.bind(list);
                    refreshComplete(recyclerView);
                } else {
                    mAdapter.addAllAndNotifyDataChanged(list);
                    if (footerView != null) {
                        loadMoreComplete(footerView);
                    }
                }
            } else {
                if (pageNo == 1) {
                    refreshComplete(recyclerView);
                    mAdapter.clear();
                    if (tvEmptyView != null) tvEmptyView.setText(emptyTip);
                } else {
                    setNoMore(footerView);
                }
            }

        }
    }


    @Deprecated
    public void afterGetData(boolean isSucceed, Object errorMsg, List<?> list) {
        if (isSucceed) {
            if (EmptyUtils.isNotEmpty(list)) {
                if (tvEmptyView != null) tvEmptyView.setText("");
                if (pageNo == 1) {
                    mAdapter.bind(list);
                    refreshComplete(recyclerView);
                } else {
                    mAdapter.addAllAndNotifyDataChanged(list);
                    if (footerView != null) {
                        loadMoreComplete(footerView);
                    }
                }
            } else {
                if (pageNo == 1) {
                    refreshComplete(recyclerView);
                    mAdapter.clear();
                    if (tvEmptyView != null) tvEmptyView.setText(emptyTip);
                } else {
                    setNoMore(footerView);
                }
            }
        } else {
            if (errorMsg == null) return;
            if (errorMsg instanceof OnNextBussinesException) {
                ThrowableUtils.show(((OnNextBussinesException) errorMsg).target);
            } else {
                if (pageNo > firstPageNo) {
                    pageNo--;
                    setError(footerView);
                } else {
                    setRefreshStatus(footerView);
                }
                ThrowableUtils.show(errorMsg);
            }

        }
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
        CircleIndicatorView indicatorView = headerView.getIndicatorView();
        indicatorView.setInnerRingColor(innerRingColor);
        indicatorView.setOutRingColor(outRingColor);
    }


    public static boolean isCanLoadMore(IRecyclerView recyclerView, LoadMoreFooterView loadMoreFooterView, RecyclerView.Adapter adapter) {
        if (recyclerView == null || loadMoreFooterView == null ||
                adapter == null) return false;
        if (loadMoreFooterView.canLoadMore() && adapter.getItemCount() > 0) {
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager && ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() > 2) {
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                return true;
            } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                int[] lastPositions = new int[((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).getSpanCount()];
                ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPositions(lastPositions);
                int lastPosition = findMax(lastPositions);
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

    //找到数组中的最大值
    private static int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
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

}
