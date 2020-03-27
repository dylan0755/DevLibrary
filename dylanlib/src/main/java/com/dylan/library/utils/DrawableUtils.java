package com.dylan.library.utils;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

/**
 * Author: Dylan
 * Date: 2020/3/26
 * Desc:
 */
public class DrawableUtils {


    public static void resetShapeColor(View view, int backgroundColor) {
        if (view == null) return;
        GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground();
        if (gradientDrawable != null){
            gradientDrawable.mutate();
            gradientDrawable.setColor(backgroundColor);
        }
    }
}
