package com.dankal.mylibrary.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.dankal.mylibrary.R;
import com.dylan.library.graphics.WebViewImageSelector;

/**
 * Created by Dylan on 2017/9/22.
 */

public class WebViewImageSelectorActivity extends Activity {
    private WebView webView;
    private WebViewImageSelector mSelector;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewimageselector);
        webView= (WebView) findViewById(R.id.webview);
        mSelector=new WebViewImageSelector();
        String html= "<!DOCTYPE html>\n" +
                "\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "    <head>\n" +
                "\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "\n" +
                "            <title>camera</title>\n" +
                "\n" +
                "    </head>\n" +
                "\n" +
                "    <body>\n" +
                "\n h5调用本地相册和摄像头" +
                "        <input type=\"file\" accept=\"image/*\" id=\"capture\" capture=\"camera\">\n" +
                "\n" +
                "            <script type=\"text/javascript\">\n" +
                "\n" +
                "                function drawOnCanvas(file) {\n" +
                "\n" +
                "                    var reader = new FileReader();\n" +
                "\n" +
                "                    reader.onload = function (e) {\n" +
                "\n" +
                "                        var dataURL = e.target.result, canvas = document.querySelector('canvas'),\n" +
                "\n" +
                "                        ctx = canvas.getContext('2d'),\n" +
                "\n" +
                "                        img = new Image();\n" +
                "\n" +
                "                        img.onload = function() {\n" +
                "\n" +
                "                            var square = 320; canvas.width = square; canvas.height = square;\n" +
                "\n" +
                "                            var context = canvas.getContext('2d'); context.clearRect(0, 0, square, square);\n" +
                "\n" +
                "                            var imageWidth;\n" +
                "\n" +
                "                            var imageHeight;\n" +
                "\n" +
                "                            var offsetX = 0;\n" +
                "\n" +
                "                            var offsetY = 0;\n" +
                "\n" +
                "                            if (this.width > this.height) {\n" +
                "\n" +
                "                                imageWidth = Math.round(square * this.width / this.height);\n" +
                "\n" +
                "                                imageHeight = square; offsetX = - Math.round((imageWidth - square) / 2);\n" +
                "\n" +
                "                            } else { imageHeight = Math.round(square * this.height / this.width);\n" +
                "\n" +
                "                                imageWidth = square;  offsetY = - Math.round((imageHeight - square) / 2);\n" +
                "\n" +
                "                            }\n" +
                "\n" +
                "                            context.drawImage(this, offsetX, offsetY, imageWidth, imageHeight);\n" +
                "\n" +
                "                            var base64 = canvas.toDataURL('image/jpeg',0.5); $('#j_thumb').val(base64.substr(22)); };\n" +
                "\n" +
                "                        img.src = dataURL; };\n" +
                "\n" +
                "                    reader.readAsDataURL(file);\n" +
                "\n" +
                "                };\n" +
                "\n" +
                "            document.querySelector('input[type=file]').onchange = function () { var file = input.files[0]; drawOnCanvas(file); };\n" +
                "\n" +
                "                </script>\n" +
                "\n" +
                "    </body>\n" +
                "\n" +
                "</html>";
        webView.loadDataWithBaseURL(null,html,"text/html","UTF-8",null);
        webView.setWebChromeClient(new WebViewImageSelector.WebChromeClient() {
            @Override
            public Activity attachActivity() {
                return WebViewImageSelectorActivity.this;
            }
            @Override
            public WebViewImageSelector attachSelector() {
                return mSelector;
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSelector.onActivityResult(this,requestCode,resultCode,data);
    }
}
