package com.dylan.library.utils;

import android.graphics.Rect;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;

/**
 * Author: Dylan
 * Date: 2020/2/17
 * Desc:
 */
public class RecyclerViewHelper {


    /**
     * 通过反射获取RecyclerView的item的topInset
     *
     * @param layoutParams
     * @return
     */
    private static int getRecyclerViewItemTopInset(RecyclerView.LayoutParams layoutParams) {
        try {
            Field field = RecyclerView.LayoutParams.class.getDeclaredField("mDecorInsets");
            field.setAccessible(true);
            // 开发者自定义的滚动监听器
            Rect decorInsets = (Rect) field.get(layoutParams);
            return decorInsets.top;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }




    public static boolean isOnBottom(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(1);
    }


    public static  boolean isOnTop(RecyclerView recyclerView){
        return !recyclerView.canScrollVertically(-1);
    }


    public static boolean isOnRight(RecyclerView recyclerView){
        return !recyclerView.canScrollHorizontally(1);
    }

    public static boolean isOnLeft(RecyclerView recyclerView){
        return !recyclerView.canScrollHorizontally(-1);
    }

    //将某个item置顶
    public static void toStickFromPosition(RecyclerView recyclerView,int position,int offset){
        if (recyclerView==null)return;
        recyclerView.scrollToPosition(position);
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            LinearLayoutManager mLayoutManager =(LinearLayoutManager) recyclerView.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, offset);
        }
    }
}

