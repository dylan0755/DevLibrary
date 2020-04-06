package com.dylan.library.callback;

import android.view.View;


/**
 * Author: Dylan
 * Date: 2018/10/19
 * Desc:
 */

public abstract class SingleClickListener implements View.OnClickListener {
    private static long lastClickTime;

    @Override
    public void onClick(View v) {
        if (isDoubleClick()) return;
        onSingleClick(v);
    }

    public abstract void onSingleClick(View v);


    private static boolean isDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 400) {
            return true;
        }
        lastClickTime = time;
        return false;

    }
}
