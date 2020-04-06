package com.dylan.library.widget.photoview.callback;

/**
 * Author: Dylan
 * Date: 2020/2/29
 * Desc:
 */
public interface OnScaleTransAnimListener {
    void onEnterAnimStart();
    void onEnterAnimProgress(int progress);
    void onEnterAnimEnd();


    void onExitAnimStart();
    void onExitAnimProgress(int progress);
    void onExitAnimEnd();
}
