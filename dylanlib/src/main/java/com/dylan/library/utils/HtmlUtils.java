package com.dylan.library.utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 * Created by Dylan on 2017/1/16.
 */

public class HtmlUtils {

    public static String headStyle= "<head><meta name=\"viewport\" " +
            "content=\"width=device-width, initial-scale=1.0\"><style>img {max-width:100%;height:auto;}</style></head>";

    public static String contentStyle="<!DOCTYPE html> <html> <head> <meta charset=\"utf-8\"> <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> <meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\"> <style> *{ box-sizing: border-box !important; } html,body{ width:100%; margin:0; padding:0; } body{ padding:0 10px; } img{ width:100% !important; height:auto !important; } </style> </head> <body> {content} <script> function setTagStyle(tagName){ var tags = document.getElementsByTagName(tagName); for(var i=0;i<tags.length;i++){ tags[i].style.width = \"100%\"; } } setTagStyle(\"div\"); setTagStyle(\"p\"); </script> </body> </html>";

    /**
     * webview加载html代码可能不会适配屏幕，放大状态，所以加个头部样式。
     * @param htmlContent
     * @return
     */
     public static String addHeadStyle(String htmlContent){
         if (htmlContent!=null&&htmlContent.isEmpty()){
             htmlContent=headStyle+htmlContent;
         }
         return htmlContent;
     }

    public static Spanned fromHtml(String inputStr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(inputStr, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(inputStr);
        }
    }

    public static String replaceHtmlContent(String htmlContent){
        return contentStyle.replace("{content}", htmlContent);
    }

}
