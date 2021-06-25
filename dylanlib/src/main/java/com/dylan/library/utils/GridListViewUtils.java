package com.dylan.library.utils;

import android.view.View;
import android.widget.AbsListView;

/**
 * Author: Dylan
 * Date: 2020/2/28
 * Desc:
 */
public class GridListViewUtils {

    //解决 ListView或GridView 直接getChildAt 返回null 的问题
    public static View getChildAt(AbsListView view, int position){
        return view.getChildAt(position-view.getFirstVisiblePosition());
    }


}
