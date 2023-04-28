package com.dylan.mylibrary.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.dylan.mylibrary.R;
import com.dylan.library.webview.DLWebChromeClient;
import com.dylan.library.webview.WebViewErroPage;
import com.dylan.library.webview.WebViewFileChooser;

/**
 * Created by Dylan on 2017/9/22.
 *
 * android:configChanges="orientation|keyboardHidden|screenSize"
 */

public class WebViewActivity extends Activity {
    private WebView webView;
    private WebViewFileChooser mFileChooser;
    private DLWebChromeClient mWebViewClient;
    private WebViewErroPage erroPage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewimageselector);
        webView= (WebView) findViewById(R.id.webview);
        erroPage=new WebViewErroPage();
        mFileChooser =new WebViewFileChooser();
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        WebSettings webSetting = webView.getSettings();
        String userAgent = webSetting.getUserAgentString();
        userAgent = userAgent + " BanShou/2.2.0" ;
        webSetting.setUserAgentString(userAgent);
        webSetting.setAllowFileAccess(true);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
        mWebViewClient=new DLWebChromeClient(mFileChooser) {
            @Override
            public Activity attchActivity() {
                return WebViewActivity.this;
            }

            @Override
            public WebView attachWebView() {
                return webView;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                erroPage.onReceivedTitle(view,title);
            }
        };
        webView.setWebChromeClient(mWebViewClient);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                erroPage.onPageFinished(view,url);
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                erroPage.onReceivedError(error.getErrorCode());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                erroPage.onReceivedError(errorCode);
            }
        });
        webView.loadUrl("http://s.ibanshou.cn/");
        Toast.makeText(this,"集成视频全屏播放和文件选择功能",Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFileChooser.onActivityResult(this,requestCode,resultCode,data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if (mWebViewClient.isFullScreen){
                mWebViewClient.onHideCustomView();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
