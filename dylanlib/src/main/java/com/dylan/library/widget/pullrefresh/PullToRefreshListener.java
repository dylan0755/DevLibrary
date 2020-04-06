package com.dylan.library.widget.pullrefresh;

/**
 * Created by cjj on 2015/8/4.
 * 刷新回调接口
 */
public interface PullToRefreshListener {

    //下拉中
    void onPulling(float fraction);

    void onFractionChanged(float fraction);

    //刷新中。。。
    void onRefresh(float fraction);

    void onRefreshCompleted();
    //缩回去
    void onMaxScrollBack();
}
