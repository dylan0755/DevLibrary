package com.dylan.library.widget;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author Dylan
 */
public class RefreshRecyclerView extends RecyclerView {
    private String TAG = "RefreshRecyclerView ->";
    private double origY;
    private int totaldy;
    private int distance;
    private int mTotalCount;
    private int mLastVisibleItemPosition;
    private boolean hasMeasure = false;
    private int mMaxScrollRange;
    private int mLoadPositionOffset;
    private static int DefaultPositionOffset;
    private boolean scrollToTop;
    private boolean canloadmore=true;

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {

        super(context, attrs);
        addOnScrollListener(new SrollListener());
        setVerticalScrollBarEnabled(true);

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                origY = (int) event.getRawY();
                distance = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                distance = (int) (event.getRawY() - origY);
                if (distance > 0) {
                    int state = getScrollState();
                    if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
                        if (totaldy == 0) {
                            if (mRefreshListener != null) {
                                mRefreshListener.pullToRefresh();
                            }
                        }
                    }
                }
                break;

        }

        return super.dispatchTouchEvent(event);
    }


    class SrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            cacul(recyclerView);
            if (!hasMeasure) {
                mMaxScrollRange = computeVerticalScrollRange() - computeVerticalScrollExtent();
                if (mTotalCount >= 1) {
                    if (mLoadPositionOffset == 0) {
                        double itemHeight = recyclerView.getLayoutManager().getChildAt(0).getMeasuredHeight();
                        mLoadPositionOffset = (int) itemHeight / 2;
                        DefaultPositionOffset = mLoadPositionOffset;
                    }
                }
                hasMeasure = true;
            }
            //当不滚动的时候
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (scrollToTop) {
                    totaldy = 0;
                    scrollToTop = false;
                    return;
                }

                if (distance < 0
                        && mLastVisibleItemPosition == mTotalCount - 1
                        && totaldy != 0
                        ) {
                    //回调
                    if (canloadmore) {
                        if (mRefreshListener != null) {
                            mRefreshListener.pullToLoadMore();
                        }
                        if (mLoadMoreListener != null) mLoadMoreListener.pullToLoadMore();
                        //是否正在加载，等加载完成再响应上拉，有可能没有网络所以没有加载
                        canloadmore = !isNetworkConnected(getContext());//正在回调即访问网络中，不允许连续触发回调
                    }
                }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            totaldy += dy;
        }
    }

    public void cacul(RecyclerView recyclerView) {
        LayoutManager manager = recyclerView.getLayoutManager();
        mLastVisibleItemPosition = getLastVisibleItemPosition(getLayoutManager());
        mTotalCount = getLayoutManager().getItemCount();
    }


    //计算最后一条可见item的位置
    public int getLastVisibleItemPosition(LayoutManager manager) {
        int position = 0;
        if (manager instanceof GridLayoutManager) {
            position = ((GridLayoutManager) manager).findLastVisibleItemPosition();
        } else if (manager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        }

        return position;
    }

    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }


    public void setLoadPositionOffset(int loadingPositionOffset) {
        mLoadPositionOffset = loadingPositionOffset;
    }


    private RecyclerViewRefershListener mRefreshListener;

    public interface RecyclerViewRefershListener {
        void pullToRefresh();//下拉刷新

        void pullToLoadMore();//上拉加载更多
    }

    public void setRecyclerViewRefershListener(RecyclerViewRefershListener listener) {
        mRefreshListener = listener;
    }

    private LoadMoreListener mLoadMoreListener;

    public interface LoadMoreListener {
        void pullToLoadMore();
    }

    public void setLoadMoreListener(LoadMoreListener listener) {
        mLoadMoreListener = listener;
    }


    public void setCanLoadMore(boolean bl) {
            canloadmore = bl;
    }


    public int getDefaultPositionOffset() {
        return DefaultPositionOffset;
    }


    /**
     * 都置为0 ，为方便计算 totaldy的值
     */
    @Override
    public void smoothScrollToPosition(int position) {
        position = 0;
        scrollToTop = true;
        super.smoothScrollToPosition(position);
    }

    @Override
    public void scrollToPosition(int position) {
        position = 0;
        scrollToTop = true;
        super.scrollToPosition(position);
    }



    public static boolean isNetworkConnected(Context context) {
        try{
            if (context != null) {
                ConnectivityManager mConnectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
