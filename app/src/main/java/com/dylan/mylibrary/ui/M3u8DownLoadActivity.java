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
import com.dylan.library.io.FileUtils;
import com.dylan.library.io.IOCloser;
import com.dylan.library.m3u8.core.DefaultVideoFilter;
import com.dylan.library.m3u8.core.DownLoadListener;
import com.dylan.library.m3u8.core.M3u8DownLoader;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.PermissionRequestBuilder;
import com.dylan.library.utils.ToastUtils;
import com.dylan.library.utils.thread.ThreadPools;
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
        edtLink.setText("http://play2.cp21.ott.cibntv.net/play.videocache.lecloud.com/270/24/108/letv-uts/14/ver_00_22-1130469020-avc-199828-aac-48000-201440-6556792-fadae76d067a770735603136df5deccd-1594732585667.m3u8?crypt=74aa7f2e97&b=260&nlh=4096&nlt=60&bf=30&p2p=1&video_type=mp4&termid=2&tss=ios&platid=1&splatid=107&its=0&qos=3&fcheck=0&amltag=1023&mltag=1023&uid=3748152347.rp&keyitem=GOw_33YJAAbXYE-cnQwpfLlv_b2zAkYctFVqe5bsXQpaGNn3T1-vhw..&ntm=1647679200&nkey=bf500b3c5c165518b5e01e9977565f7a&nkey2=6aeb21970f865a57889dfdfd3691de93&auth_key=1647679200-1-3748152347.rp-1-107-6940ae79e43bfa2895ae3e86fa16b58b&geo=CN-19-262-4&payff=0&m3v=3&ostype=android&cvid=220843857095&playid=0&hwtype=un&tzm=-765102505&xm=649b22fe5e27446d158325beaf6acde0&fky=12345&vtype=21&vid=67851444&tm=1647660650&mmsid=68012742&uuid=1660650944533561&p2=04&p1=0&key=3e44b63b92beb59c4bad1af9d26ce8ab&uidx=0&errc=0&gn=50049&ndtype=2&vrtmcd=102&buss=1023&cips=223.104.68.27");
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
        final String videoUrl= edtLink.getText().toString();
        if (EmptyUtils.isEmpty(videoUrl)){
            ToastUtils.show("请输入链接");
            return;
        }
        final String outPutDir=  Environment.getExternalStorageDirectory().toString()+"/1test";
        FileUtils.createDirIfNotExists(outPutDir);
        final StringBuilder stringBuilder=new StringBuilder();

        ThreadPools.getInstance().fixedThreadPoolRun(new Runnable() {
            @Override
            public void run() {
                //文件名称
                String fileName = "test.mp4";
                //创建下载实例，设置并发线程数
                M3u8DownLoader videoDownload = new M3u8DownLoader(3,new BouncyCastleProvider());
                //设置下载后的文件存储路径
                videoDownload.setDirPath(outPutDir);
                //设置视频过滤器，当下载链接包含多个视频文件时，由用户指定选择哪个视频文件，可以不设置
                videoDownload.setVideoListFilter(new DefaultVideoFilter());
                //开始下载
                videoDownload.startDownload(videoUrl, fileName, new DownLoadListener() {

                    @Override
                    public void onStart() {
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
                    public void onProgress(int finished, int sum, float percent) {
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
