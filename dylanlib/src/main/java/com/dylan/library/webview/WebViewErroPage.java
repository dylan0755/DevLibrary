package com.dylan.library.webview;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.exception.ELog;

/**
 * Created by Dylan on 2017/11/24.
 */

public class WebViewErroPage {
    private View erroView;
    private boolean showErrorPage;
    private int erroState=0;





    public void onPageFinished(final android.webkit.WebView view, final String url) {
        ViewGroup webViewContainer= (ViewGroup) view.getParent();
        if (webViewContainer==null)return;
        int childCount=webViewContainer.getChildCount();
        if (showErrorPage){
            addErrorView(view, url, webViewContainer, childCount);
            showErrorPage =false;
        }else{
            if (childCount>1){
                View lastChild=webViewContainer.getChildAt(childCount-1);
                if (lastChild instanceof ErrorLayout){
                    webViewContainer.removeView(lastChild);
                    erroState =0;
                }
            }

        }
    }

    private void addErrorView(final WebView view, final String url, ViewGroup webViewContainer, int childCount) {
        try {
            boolean hasErrorPage=false;
            for (int i=0;i<childCount;i++){
                View child=webViewContainer.getChildAt(i);
                if (child instanceof ErrorLayout){
                    hasErrorPage=true;
                    break;
                }
            }
            if (!hasErrorPage){
                if (erroView==null){
                    erroView= View.inflate(view.getContext(), R.layout.dl_webview_error,null);
                }
                webViewContainer.addView(erroView,childCount);
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
        }catch (Exception e){
            ELog.e(e);
        }

    }

    public void onReceivedTitle(final android.webkit.WebView view, String title){
        if (title!=null){
            title=title.toLowerCase();
            if (title.contains("网页无法打开")||title.contains("erro")){
                showErrorPage =true;
                ViewGroup webViewContainer= (ViewGroup) view.getParent();
                if (webViewContainer==null)return;
                int childCount=webViewContainer.getChildCount();
                if (showErrorPage){
                    addErrorView(view,view.getUrl(),webViewContainer,childCount);
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
