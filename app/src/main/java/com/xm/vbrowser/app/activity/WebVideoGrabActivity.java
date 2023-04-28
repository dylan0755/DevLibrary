package com.xm.vbrowser.app.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dylan.common.BaseActivity;
import com.dylan.library.bean.EventBundle;
import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.utils.EditTextUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.PermissionRequestBuilder;
import com.dylan.library.utils.ToastUtils;
import com.dylan.library.webview.WebProgressView;
import com.dylan.mylibrary.R;
import com.xm.vbrowser.app.VideoSniffer;
import com.xm.vbrowser.app.entity.DetectedVideoInfo;
import com.xm.vbrowser.app.entity.VideoInfo;
import com.xm.vbrowser.app.util.FoundItemDialog;
import com.xm.vbrowser.app.util.VideoFormatUtil;
import com.xm.vbrowser.app.util.VideoSnifferLogger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Author: Dylan
 * Date: 2022/3/15
 * Desc:
 */

public class WebVideoGrabActivity extends BaseActivity {
    public final static String ACTION_DETECTED_NEW_VIDEO="actionDetectedNewVideo";
    private static final String HOME_URL = "http://go.uc.cn/page/subpage/shipin?uc_param_str=dnfrpfbivecpbtntla";
    private WebView mWebView;
    private EditText searchInput;
    private LinkedBlockingQueue<DetectedVideoInfo> detectedTaskUrlQueue = new LinkedBlockingQueue<DetectedVideoInfo>();

    private VideoSniffer videoSniffer;
    private String currentTitle = "";
    private String currentUrl = "";
    private ImageView ivCloseSearch;
    private WebProgressView webProgressView;
    private FoundItemDialog mFoundItemDialog;
    private PermissionRequestBuilder requestBuilder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_webvideo_grab;
    }


    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
       TextView  tv_title=findViewById(R.id.tv_title);
        tv_title.setText("视频下载器");
        EventBus.getDefault().register(this);
        initView();
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
            requestBuilder=new PermissionRequestBuilder(this);
            requestBuilder.addPerm(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
            requestBuilder.addPerm(Manifest.permission.READ_EXTERNAL_STORAGE,true)
                    .startRequest(100);
        }

        if (videoSniffer != null) {
            VideoSnifferLogger.setIsDebug(true);
            videoSniffer.startSniffer();
        }
    }

    private void initView() {
        webProgressView = findViewById(R.id.webViewProgressVIew);
        webProgressView.setColor(Color.parseColor("#FADB22"));
        mWebView =findViewById(R.id.mainWebView);
        searchInput =findViewById(R.id.edtLink);
        ivCloseSearch=findViewById(R.id.ivCloseSearch);
        initWebView();
        videoSniffer = new VideoSniffer(detectedTaskUrlQueue, 5, 1);
        mFoundItemDialog=new FoundItemDialog(this);

        searchInput.addTextChangedListener(new EditTextUtils.AfterTextChangedListener() {
            @Override
            public void afterTextChanged(Editable editable) {
                ivCloseSearch.setVisibility(EmptyUtils.isNotEmpty(editable.toString())?View.VISIBLE:View.GONE);
            }
        });
        ivCloseSearch.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                searchInput.setText("");
            }
        });
        EditTextUtils.addSearchAction(searchInput, new EditTextUtils.OnDoneActionCallBack() {
            @Override
            public void onKeyEvent(TextView textView, int i, KeyEvent keyEvent) {
                loadOrSearch(textView.getText().toString());
            }
        });
        String url=getIntent().getStringExtra("url");
        if (EmptyUtils.isEmpty(url))url=HOME_URL;
        loadOrSearch(url);
    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        settings.setAppCacheEnabled(true);//开启缓存
        settings.setDomStorageEnabled(true);//Dom形式缓存
        settings.setAllowFileAccess(true);
        settings.setDatabaseEnabled(true);//开启数据库缓存
        settings.setAppCacheMaxSize(Long.MAX_VALUE);//缓存阈值
        settings.setAppCachePath(getCacheDir().getPath());//缓存路径
        settings.setDatabasePath(this.getDir("database", 0).getPath());
        settings.setGeolocationDatabasePath(this.getDir("geolocation", 0).getPath());
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebChromeClient(new WebChromeClientImpl());
        mWebView.setWebViewClient(new WebViewClientImpl());

    }

    private void loadOrSearch(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        searchInput.setText(content);
        if (content.startsWith("http")||content.startsWith("https")) {
            mWebView.clearHistory();
            mWebView.loadUrl(content);
            return;
        }
        String encodedContent = "";
        try {
            encodedContent = URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mWebView.loadUrl("https://m.baidu.com/s?word=" + encodedContent);
    }






    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBundle bundle) {
        if (ACTION_DETECTED_NEW_VIDEO.equals(bundle.getAction())) {
            findViewById(R.id.ivGrabVideoFloat).setVisibility(View.VISIBLE);
            findViewById(R.id.ivGrabVideoFloat).setOnClickListener(new SingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    mFoundItemDialog.show();
                }
            });
            mFoundItemDialog.show((VideoInfo) bundle.getExtraData());
        }
    }



    class WebChromeClientImpl extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.d("WebActivity", "onReceivedTitle title=" + title);
            currentTitle = title;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            Log.d("WebActivity", "onProgressChanged progressInPercent=" + newProgress);
            if (newProgress == 100) {
                webProgressView.setProgress(100);
                webProgressView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webProgressView.setVisibility(View.GONE);
                    }
                }, 200);//0.2秒后隐藏进度条
            } else if (webProgressView.getVisibility() == View.GONE) {
                webProgressView.setVisibility(View.VISIBLE);
            }
            //设置初始进度10，这样会显得效果真一点，总不能从1开始吧
            if (newProgress < 10) {
                newProgress = 10;
            }
            //不断更新进度
            webProgressView.setProgress(newProgress);
            if (HOME_URL.equals(view.getUrl())) {
                searchInput.setText("");
            } else {
                searchInput.setText(currentUrl);
            }
        }


    }


    class WebViewClientImpl extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            currentUrl = url;
            Log.d("WebActivity", "onLoadStarted url:" + url);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            Logger.d("WebActivity", "shouldInterceptLoadRequest hint url:" + url);
            WeakReference<LinkedBlockingQueue> detectedTaskUrlQueueWeakReference = new WeakReference<LinkedBlockingQueue>(detectedTaskUrlQueue);
            LinkedBlockingQueue detectedTaskUrlQueue = detectedTaskUrlQueueWeakReference.get();
            if (detectedTaskUrlQueue != null) {
                detectedTaskUrlQueue.add(new DetectedVideoInfo(url, currentUrl, currentTitle));
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
//            Log.d("shouldOverrideUrlLoad", url);
            if (!(url.startsWith("http") || url.startsWith("https"))) {
                //非http https协议 不动作
                return true;
            }
//
//            //http https协议 在本webView中加载
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (VideoFormatUtil.containsVideoExtension(extension)) {
                detectedTaskUrlQueue.add(new DetectedVideoInfo(url, currentUrl, currentTitle));
                return true;
            }
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {

            mWebView.goBack();
        }else{
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFoundItemDialog!=null)mFoundItemDialog.setActivityBack(true);
        if (videoSniffer != null) {
            videoSniffer.stopSniffer();
        }


        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100){
            PermissionRequestBuilder.RequestReuslt requestReuslt=requestBuilder.onRequestPermissionsResult(permissions,grantResults);
            if (requestReuslt.hasRejectForceNeed) {
                ToastUtils.show("存储权限未允许");
            }
        }
    }
}

