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


}
