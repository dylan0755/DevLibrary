package com.dylan.library.webview;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.dylan.library.R;


/**
 * Created by Dylan on 2017/11/24.
 *
 * WebView 的直接父布局必须是FrameLayout
 */

public class WebViewErroPage {
    private View erroView;
    private boolean showErrorPage;
    private int erroState=0;



    public void onPageFinished(final android.webkit.WebView view, final String url) {
        ViewGroup webViewContainer= (ViewGroup) view.getParent();
        if (webViewContainer==null)return;
        if (showErrorPage){
            int childCount=webViewContainer.getChildCount();
            if (childCount==1){
                if (erroView==null)erroView= View.inflate(view.getContext(), R.layout.webview_error,null);
                webViewContainer.addView(erroView,1);
                erroView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.loadUrl(url);
                    }
                });
                if (erroState==2){
                    TextView tv= (TextView) erroView.findViewById(R.id.erro);
                    tv.setText("网页无法打开");
                }else{
                    TextView tv= (TextView) erroView.findViewById(R.id.erro);
                    tv.setText("网络已断开");
                }
            }
            showErrorPage =false;
        }else{
            if (webViewContainer.getChildCount()>1){
                webViewContainer.removeViewAt(1);
                erroState =0;
            }
        }
    }

    public void onReceivedTitle(final android.webkit.WebView view, String title){
        if (title!=null){
            title=title.toLowerCase();
            if (title.contains("网页无法打开")||title.contains("erro")){
                showErrorPage =true;
                if (showErrorPage){
                    ViewGroup webViewContainer= (ViewGroup) view.getParent();
                    if (webViewContainer==null)return;
                    int childCount=webViewContainer.getChildCount();
                    if (childCount==1){
                        if (erroView==null)erroView= View.inflate(view.getContext(),R.layout.webview_error,null);
                        webViewContainer.addView(erroView,1);
                        final String currentUrl=view.getUrl();
                        erroView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                view.loadUrl(currentUrl);
                            }
                        });
                    }
                    showErrorPage =false;
                }
            }else{
                showErrorPage=false;
            }

        }
    }


    public void onReceivedError(int errorCode) {
        if (errorCode == WebViewClient.ERROR_HOST_LOOKUP || errorCode == WebViewClient.ERROR_CONNECT
                || errorCode == WebViewClient.ERROR_TIMEOUT){
            showErrorPage =true;
            erroState =1;
        }else{
            showErrorPage =true;
            erroState =2;
        }
    }





}
