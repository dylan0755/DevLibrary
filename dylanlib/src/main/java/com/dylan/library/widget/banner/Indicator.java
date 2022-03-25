package com.dylan.library.widget.banner;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Author: Dylan
 * Date: 2022/03/25
 * Desc:
 */
public interface Indicator extends ViewPager.OnPageChangeListener {

    /**
     * 当数据初始化完成时调用
     *
     * @param pagerCount pager数量
     */
    void initIndicatorCount(int pagerCount);

    /**
     * 返回一个View，添加到banner中
     */
    View getView();

    /**
     * banner是一个RelativeLayout，设置banner在RelativeLayout中的位置，可以是任何地方
     */
    RelativeLayout.LayoutParams getParams();
}