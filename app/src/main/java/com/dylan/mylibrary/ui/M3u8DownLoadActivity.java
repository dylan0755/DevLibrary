package com.dylan.mylibrary.ui;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.exception.ELog;
import com.dylan.library.io.FileUtils;
import com.dylan.library.io.IOCloser;
import com.dylan.library.m3u8.download.M3u8DownLoader;
import com.dylan.library.m3u8.download.M3u8DownloadFactory;
import com.dylan.library.m3u8.listener.DownloadListener;
import com.dylan.library.utils.PermissionRequestBuilder;
import com.dylan.library.utils.ToastUtils;
import com.dylan.mylibrary.R;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Author: Dylan
 * Date: 2022/3/17
 * Desc:
 */

public class M3u8DownLoadActivity extends AppCompatActivity {
    EditText edtLink;
    TextView tvInfo;
    Button tvDownLoad;
    ScrollView scrollView;
    private PermissionRequestBuilder requestBuilder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3u8_download);
        tvInfo=findViewById(R.id.tvInfo);
        edtLink=findViewById(R.id.edtLink);
        edtLink.setText("http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8");
        tvDownLoad=findViewById(R.id.tvDownLoad);
        scrollView=findViewById(R.id.scrollView);
        requestBuilder=new PermissionRequestBuilder(this);
        requestBuilder.addPerm(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
        final boolean needRequest= requestBuilder.addPerm(Manifest.permission.READ_EXTERNAL_STORAGE,true)
                .startRequest(100);
        tvDownLoad.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (!needRequest)
                tvDownLoad.setEnabled(false);
                testM3u8DownLoad();
            }
        });
    }

    private void testM3u8DownLoad(){
        String url= M3u8DownLoader.M3U8URL;
        String outPutDir= Environment.getExternalStorageDirectory().toString()+"/ticibao/m3u8";
        final StringBuilder stringBuilder=new StringBuilder();
        M3u8DownloadFactory.addProvider(new BouncyCastleProvider());
        M3u8DownLoader.downLoad(url,outPutDir,"测试1",new DownloadListener() {
            @Override
            public void start() {
                stringBuilder.append("\n"+"开始下载！");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvInfo.setText(stringBuilder);
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void process(String downloadUrl, int finished, int sum, float percent) {
                stringBuilder.append("\n"+"已下载" + finished + "个\t一共" + sum + "个\t已完成 " + percent + "%");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvInfo.setText(stringBuilder);
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void speed(String speedPerSecond) {
                stringBuilder.append("\n"+"下载速度：" + speedPerSecond);
                scrollView.fullScroll(View.FOCUS_DOWN);
            }

            @Override
            public void onComplete(final String savePath) {
                stringBuilder.append("\n"+"下载完毕");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvInfo.setText(stringBuilder);
                        scrollView.fullScroll(View.FOCUS_DOWN);
                        ToastUtils.show("下载成功");
                        FileUtils.notifyScanFile(scrollView.getContext(),savePath);
                        tvDownLoad.setEnabled(true);
                    }
                });
            }



            @Override
            public void onError(Exception exception) {
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                PrintStream printStream=new PrintStream(baos);
                exception.printStackTrace(printStream);
                String exceptionStr = baos.toString();
                IOCloser.closeIOArray(baos,printStream);
                stringBuilder.append(exceptionStr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                          tvInfo.setText(stringBuilder);
                        scrollView.fullScroll(View.FOCUS_DOWN);
                        tvDownLoad.setEnabled(true);
                    }
                });
            }
        });

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
