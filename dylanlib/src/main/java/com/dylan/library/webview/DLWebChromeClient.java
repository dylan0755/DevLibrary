package com.dylan.library.webview;

import android.app.Activity;
import android.webkit.WebView;

/**
 * Created by Dylan on 2017/10/19.
 */

public abstract class DLWebChromeClient extends FileChooserWebChromeClient {
    public DLWebChromeClient(WebViewFileChooser selector) {
        super(selector);
    }

    @Override
    public abstract Activity attchActivity();

    @Override
    public abstract WebView attachWebView();
}
