package com.dylan.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * Author: Dylan
 * Date: 2018/9/23
 * Desc:
 */

public class ContextUtils {
    public static Activity getActivity(Context context){
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        if (context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }

    public static Activity getNotFinishingActivity(Context context){
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            if (!((Activity) context).isFinishing()){
                return ((Activity) context);
            }
        }
        return null;
    }
}
