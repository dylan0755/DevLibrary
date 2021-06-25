package com.dylan.library.utils;

import android.os.SystemClock;
import android.view.View;

public class FastClickUtils {
    private static long mClickTime;
    private static int viewId;

    public static boolean filterFastClick(View view){
        if (viewId==view.getId()){
             if (SystemClock.uptimeMillis() - mClickTime<=500){
                 mClickTime=SystemClock.uptimeMillis();
                 return true;
             }
        }
        viewId=view.getId();
        mClickTime=SystemClock.uptimeMillis();
        return false;
    }


}
