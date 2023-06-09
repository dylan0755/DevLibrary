package com.dylan.library.utils;

import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

/**
 * Author: Dylan
 * Date: 2020/3/25
 * Desc:
 */
public class ViewUtils {

    public static void setClickListener(View.OnClickListener onClickListener, View... views) {
        for (View view : views) {
            view.setOnClickListener(onClickListener);
        }
    }

    public static boolean isOnBottom(View scrollTarget) {
        if (scrollTarget instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) scrollTarget;
            return scrollView.getChildAt(0).getMeasuredHeight() == scrollView.getScrollY() + scrollView.getHeight();
        }
        return !scrollTarget.canScrollVertically(1);
    }

    public static boolean isOnTop(View scrollTarget) {
        if (scrollTarget instanceof ScrollView) {
            return ((ScrollView) scrollTarget).getScrollY() <= 0;
        }
        return !scrollTarget.canScrollVertically(-1);
    }

    public static boolean isOnRight(View scrollTarget) {
        return !scrollTarget.canScrollHorizontally(1);
    }

    public static boolean isOnLeft(View scrollTarget) {
        return !scrollTarget.canScrollHorizontally(-1);
    }


    public static void logViewLocationInfo(View view) {
        Log.e(ViewUtils.class.getSimpleName(),
                " getX()：" + view.getX()
                        + " getY()：" + view.getY()
                        + " getLeft(): " + view.getLeft()
                        + " getWidth(): " + view.getWidth()
                        + " getTranslationX(): " + view.getTranslationX()
                        + " getTranslationY(): " + view.getTranslationY()
                        + " getScaleX(): " + view.getScaleX()
                        + " getScaleY(): " + view.getScaleY()
                        + " getScrollX()" + view.getScrollX()
                        + " getScrollY()" + view.getScrollY()
        );
    }

    public static void rotate(View view,int angle){
        view.animate().rotation(angle).start();
    }
}
