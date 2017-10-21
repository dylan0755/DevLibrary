package com.dylan.library.webview;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebView;


/**
 * Created by Dylan on 2017/9/22.
 */

public abstract class FileChooserWebChromeClient extends FullScreenWebChromeClient {
    private WebViewFileChooser mSelector;

    public FileChooserWebChromeClient(WebViewFileChooser selector){
        mSelector=selector;
    }
    // android >5.0以上
    @Override
    public boolean onShowFileChooser(WebView webView,
                                     ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        mSelector.setFilePathCallback(filePathCallback);
        mSelector.select(attchActivity());
        return true;
    }
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        mSelector.setUploadFile(uploadMsg);
        mSelector.select(attchActivity());
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        mSelector.setUploadFile(uploadMsg);
        mSelector.select(attchActivity());
    }
    // android >4.11
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        mSelector.setUploadFile(uploadMsg);
        mSelector.select(attchActivity());
    }










}
