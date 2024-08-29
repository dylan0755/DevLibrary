package com.dylan.library.utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Dylan on 2017/1/16.
 */

public class HtmlUtils {

    public static String headStyle = "<head><meta name=\"viewport\" " +
            "content=\"width=device-width, initial-scale=1.0\"><style>img {max-width:100%;height:auto;}</style></head>";

    public static String contentStyle = "<!DOCTYPE html> <html> <head> <meta charset=\"utf-8\"> <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> <meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\"> <style> *{ box-sizing: border-box !important; } html,body{ width:100%; margin:0; padding:0; } body{ padding:0 10px; } img{ width:100% !important; height:auto !important; } </style> </head> <body> {content} <script> function setTagStyle(tagName){ var tags = document.getElementsByTagName(tagName); for(var i=0;i<tags.length;i++){ tags[i].style.width = \"100%\"; } } setTagStyle(\"div\"); setTagStyle(\"p\"); </script> </body> </html>";

    /**
     * webview加载html代码可能不会适配屏幕，放大状态，所以加个头部样式。
     *
     * @param htmlContent
     * @return
     */
    public static String addHeadStyle(String htmlContent) {
        if (htmlContent != null && htmlContent.isEmpty()) {
            htmlContent = headStyle + htmlContent;
        }
        return htmlContent;
    }

    public static Spanned fromHtml(String inputStr) {
        if (inputStr == null) return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(inputStr, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(inputStr);
        }
    }

    public static String replaceHtmlContent(String htmlContent) {
        return contentStyle.replace("{content}", htmlContent);
    }

    private final static String CREATE_CUSTOM_SHEET =
            "if (typeof(document.head) != 'undefined' && typeof(customSheet) == 'undefined') {"
                    + "var customSheet = (function() {"
                    + "var style = document.createElement(\"style\");"
                    + "style.appendChild(document.createTextNode(\"\"));"
                    + "document.head.appendChild(style);"
                    + "return style.sheet;"
                    + "})();"
                    + "}";

    public static void injectCssWhileOnPageFinish(WebView webView, String... cssRules) {
        StringBuilder jsUrl = new StringBuilder("javascript:");
        jsUrl
                .append(CREATE_CUSTOM_SHEET)
                .append("if (typeof(customSheet) != 'undefined') {");
        int cnt = 0;
        for (String cssRule : cssRules) {
            jsUrl
                    .append("customSheet.insertRule('")
                    .append(cssRule)
                    .append("', ")
                    .append(cnt++)
                    .append(");");
        }
        jsUrl.append("}");

        webView.loadUrl(jsUrl.toString());
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void loadImgSrc(WebView view) {
        view.evaluateJavascript(loadImgSrcJs(),
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        try {
                            JSONArray jsonArray = new JSONArray(value);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String url = jsonArray.getString(i);
                                if (url.startsWith("data:image/")) {

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private static String loadImgSrcJs(){
        return  "(function() { " +
                "   var imgs = document.getElementsByTagName('img'); " +
                "   var l = imgs.length; " +
                "   var imgUrls = []; " +
                "   for (var i = 0; i < l; i++) { " +
                "       imgUrls.push(imgs[i].src); " +
                "   } " +
                "   return imgUrls; " +
                "})();";
    }

}
