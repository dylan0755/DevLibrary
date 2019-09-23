package com.dylan.mylibrary.ui.progressx5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dylan.library.utils.Logger;
import com.dylan.library.webview.WebProgressView;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Author: Dylan
 * Date: 2019/8/28
 * Desc:
 */
public class ProgressX5WebView extends WebView {
    private Handler handler;
    private WebView mWebView;
    private WebProgressView progressBar;//进度条的矩形(进度线)

    public ProgressX5WebView(Context context) {
        this(context,null);
    }

    public ProgressX5WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        //实例化进度条
        progressBar = new WebProgressView(context);
        //设置进度条的size
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //刚开始时候进度条不可见
        progressBar.setVisibility(GONE);
        //把进度条添加到webView里面
        addView(progressBar);
        //初始化handle
        handler = new Handler();
        mWebView = this;
        initWebSettings(this);
        setWebViewClient(new MyWebClient());
        setWebChromeClient(new MyWebChromeClient());
    }


    // 初始化设置
    @SuppressLint("SetJavaScriptEnabled")
    public static void initWebSettings(WebView webView) {
        WebSettings mSettings = webView.getSettings();
        mSettings.setJavaScriptEnabled(true);//开启javascript
        mSettings.setDomStorageEnabled(true);//开启DOM
        mSettings.setDefaultTextEncodingName("utf-8");//设置字符编码
        mSettings.setAllowFileAccess(true);//设置支持文件流
        mSettings.setSupportZoom(false);//支持缩放
        mSettings.setDisplayZoomControls(false);
        mSettings.setBuiltInZoomControls(true);//支持缩放
        mSettings.setUseWideViewPort(true);//调整到适合webview大小
        mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mSettings.setLoadWithOverviewMode(true);//调整到适合webview大小
        mSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mSettings.setSupportMultipleWindows(true);
        mSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);//屏幕自适应网页,如果没有这个,在低分辨率的手机上显示可能会异常
        mSettings.setRenderPriority(WebSettings.RenderPriority.NORMAL);//提高网页加载速度,暂时阻塞图片加载,然后网页加载好了,再进行加载图片
        mSettings.setBlockNetworkImage(true);
        mSettings.setAppCacheEnabled(true);//开启缓存机制
        //缓存模式，根据cache-control决定是否从网络上取数据
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setAppCacheMaxSize(Long.MAX_VALUE);//缓存阈值
        webView.getSettings().setAppCachePath(webView.getContext().getCacheDir().getPath());//缓存路径
        webView.getSettings().setDatabasePath(webView.getContext().getDir("database", 0).getPath());
        webView.getSettings().setGeolocationDatabasePath(webView.getContext().getDir("geolocation", 0).getPath());
    }

    //自定义WebChromeClient
    private class MyWebChromeClient extends WebChromeClient {
        //进度改变的回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setProgress(100);
                handler.postDelayed(runnable, 200);//0.2秒后隐藏进度条
            } else if (progressBar.getVisibility() == GONE) {
                progressBar.setVisibility(VISIBLE);
            }
            //设置初始进度10，这样会显得效果真一点，总不能从1开始吧
            if (newProgress < 10) {
                newProgress = 10;
            }
            //不断更新进度
            progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }

    private class MyWebClient extends WebViewClient {
        //加载过程中拦截加载的地址url
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return true;
        }



        //页面加载过程中,加载资源回调的方法
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        //页面加载完成回调的方法
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //关闭图片加载阻塞
            view.getSettings().setBlockNetworkImage(false);
            Logger.e("getContentWidth "+view.getContentWidth()+" mWebView.getContentHeight "+view.getContentHeight());
        }

        //页面开始加载调用的方法
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }



        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            requestFocus();
            requestFocusFromTouch();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    //刷新界面(此处为加载完成后进度消失)
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
        }
    };

}
