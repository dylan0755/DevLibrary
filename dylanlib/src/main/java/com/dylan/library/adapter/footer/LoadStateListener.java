package com.dylan.library.adapter.footer;

import android.view.View;

/**
 * Created by Dylan on 2016/10/25.
 */
public interface LoadStateListener {
    void onLoading(); //正在加载
    void onLoadNothing();  //已加载全部
    void hide();
    void show();
    void netWorkBreak();
    View getContentView();
}
