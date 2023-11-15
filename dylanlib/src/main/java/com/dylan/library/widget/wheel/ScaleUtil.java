package com.dylan.library.widget.wheel;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.content.Context;

 class ScaleUtil {
    private static final int BASE_WIDTH = 1080;
    private static float BASE_RATIO = 1.0F;

    public ScaleUtil(Context context) {
        if (context != null) {
            boolean flag = context.getResources().getConfiguration().orientation == 1;
            int width;
            if (flag) {
                width = context.getResources().getDisplayMetrics().widthPixels;
            } else {
                width = context.getResources().getDisplayMetrics().heightPixels;
            }

            BASE_RATIO = 1.0F * (float)width / 1080.0F;
        }
    }

    public int toScaleSize(int px) {
        return (int)(BASE_RATIO * (float)px);
    }
}
