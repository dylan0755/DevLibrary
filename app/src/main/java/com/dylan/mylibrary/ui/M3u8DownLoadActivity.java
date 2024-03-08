package com.dylan.mylibrary.ui;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;

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
        edtLink.setText("http://play2.cp21.ott.cibntv.net/play.videocache.lecloud.com/270/47/24/letv-uts/14/ver_00_22-1130050208-avc-199670-aac-48000-198680-6457344-b6fbfbc2de7a0638cefaeac53942cbc7-1593581495891.m3u8?crypt=4aa7f2e97&b=260&nlh=4096&nlt=60&bf=30&p2p=1&video_type=mp4&termid=2&tss=ios&platid=1&splatid=107&its=0&qos=3&fcheck=0&amltag=1023&mltag=1023&uid=3748152347.rp&keyitem=GOw_33YJAAbXYE-cnQwpfLlv_b2zAkYctFVqe5bsXQpaGNn3T1-vhw..&ntm=1647690600&nkey=293f2d8dcc296490fabfd39dfbc5bddc&nkey2=24599e33a3d27247f6bc6b5f9b31ec31&auth_key=1647690600-1-3748152347.rp-1-107-e565e311cab253015d6025f72a43b437&geo=CN-19-262-4&payff=0&m3v=3&ostype=android&cvid=220843857095&playid=0&hwtype=un&tzm=1365603941&xm=bda33fa64d48e84123394ffa7af28e60&fky=12345&vtype=21&vid=67673743&tm=1647672557&mmsid=67834930&uuid=1672557710210775&p2=04&p1=0&key=33c7fd06609665387f78a96a9202e431&uidx=0&errc=0&gn=50049&ndtype=2&vrtmcd=102&buss=1023&cips=223.104.68.27");
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
            Toaster.show("请输入链接");
            return;
        }
        final String outPutDir=  Environment.getExternalStorageDirectory().toString()+"/1test";
        FileUtils.createDirIfNotExists(outPutDir);
        final StringBuilder stringBuilder=new StringBuilder();

        //文件名称
        String fileName = "test.mp4";
        //创建下载实例，设置并发线程数
        M3u8DownLoader videoDownload = new M3u8DownLoader(10,new BouncyCastleProvider());
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
                        Toaster.show("下载成功");
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
                Toaster.show("存储权限未允许");
            }
        }
    }
}
