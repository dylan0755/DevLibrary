package com.dylan.library.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

/**
 * Author: Dylan
 * Date: 2019/8/7
 * Desc:
 */
public class CompatUtils {
    public static Context mContext;

    public static void initContext(Context context) {
        mContext = context;
    }

    public static int getColor(@ColorRes int id) {
        return ContextCompat.getColor(mContext, id);
    }

    public static Drawable getDrawable(@DrawableRes int id){
        return ContextCompat.getDrawable(mContext,id);
    }

    public static String getString(@StringRes int id){
        return mContext.getResources().getString(id);
    }

}
