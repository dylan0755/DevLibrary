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
        edtLink.setText("http://play2.cp21.ott.cibntv.net/play.videocache.lecloud.com/137/25/21/letv-uts/77455241/ver_00_22-1150449834-avc-201758-aac-48000-29000-952982-ec4ac05f2fde27b159e29fafff54e70f-1647585238980.m3u8?crypt=35aa7f2e98&b=262&nlh=4096&nlt=60&bf=30&p2p=1&video_type=mp4&termid=2&tss=ios&platid=1&splatid=107&its=0&qos=3&fcheck=0&amltag=1023&mltag=1023&uid=3748152325.rp&keyitem=GOw_33YJAAbXYE-cnQwpfLlv_b2zAkYctFVqe5bsXQpaGNn3T1-vhw..&ntm=1647649200&nkey=6242598e727fc7241d4c42bf9006208d&nkey2=2317bc7d917696bbb414f66316d47113&auth_key=1647649200-1-3748152325.rp-1-107-180a3f2a21e6ba3dae4c267ddbfa0f50&geo=CN-19-262-4&payff=0&m3v=3&ostype=android&cvid=41121924727&playid=0&hwtype=un&tzm=-2124056637&xm=6272c2202eb410cc9431b27d791cebff&fky=12345&vtype=21&vid=77304648&tm=1647631005&mmsid=77455241&uuid=1631005423196799&p2=04&p1=0&key=75fd244cc818f1eec8a622f04e30d9f2&uidx=0&errc=0&gn=50049&ndtype=2&vrtmcd=102&buss=1023&cips=223.104.68.5");
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
        String url= edtLink.getText().toString();
        String outPutDir= Environment.getExternalStorageDirectory().toString();
        final StringBuilder stringBuilder=new StringBuilder();
        M3u8DownloadFactory.addProvider(new BouncyCastleProvider());
        M3u8DownLoader.downLoad(url,outPutDir,"m3u8_test_"+System.currentTimeMillis(),new DownloadListener() {
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
