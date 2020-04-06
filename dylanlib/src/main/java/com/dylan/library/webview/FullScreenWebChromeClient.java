package com.dylan.library.webview;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.dylan.library.exception.ELog;
import com.dylan.library.screen.ScreenUtils;


/**
 * Created by Dylan on 2017/10/18.
 */

public abstract class FullScreenWebChromeClient extends WebChromeClient {
    public FrameLayout mViewContainer;
    public boolean isFullScreen;
    public CustomViewCallback mCallBack;

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        isFullScreen = true;
        fullScreen();
        attachWebView().setVisibility(View.GONE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        if (mViewContainer == null) {
            mViewContainer = new FrameLayout(attchActivity());
            mViewContainer.setLayoutParams(params);
        }
        mViewContainer.setVisibility(View.VISIBLE);
        mViewContainer.addView(view);
        FrameLayout decorView = (FrameLayout) attchActivity().getWindow().getDecorView();
        decorView.addView(mViewContainer, params);
        mCallBack = callback;
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        try {
            isFullScreen = false;
            fullScreen();
            if (mCallBack != null) mCallBack.onCustomViewHidden();
            attachWebView().setVisibility(View.VISIBLE);
            mViewContainer.removeAllViews();
            mViewContainer.setVisibility(View.GONE);
            FrameLayout decorView = (FrameLayout) attchActivity().getWindow().getDecorView();
            decorView.removeView(mViewContainer);
            super.onHideCustomView();
        } catch (Exception e) {
            ELog.e(e);
            if (attchActivity() != null && attchActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                attchActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                ScreenUtils.restoreStatusBar(attchActivity());
            }
        }
        super.onHideCustomView();
    }


    public abstract Activity attchActivity();

    public abstract WebView attachWebView();


    private void fullScreen() {
        if (attchActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            attchActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ScreenUtils.removeStatuBar(attchActivity());
        } else {
            attchActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ScreenUtils.restoreStatusBar(attchActivity());
        }
    }


    public boolean getFullScreen() {
        return isFullScreen;
    }
}
