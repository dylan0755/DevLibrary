package com.dylan.mylibrary.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dylan.library.utils.EmptyUtils;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.progressx5.ProgressX5WebView;


/**
 * Author: Dylan
 * Date: 2019/8/27
 * Desc:
 */
public class ProgressWebViewActivity extends AppCompatActivity {
    ProgressX5WebView mWebView;
    private String mWebUrl="https://world.taobao.com/";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progressweb);
        mWebView=findViewById(R.id.webView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(0);
        }
        if (EmptyUtils.isNotEmpty(mWebUrl)) mWebView.loadUrl(mWebUrl);
    }













    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mWebView) {
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }
}
