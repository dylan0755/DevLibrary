package com.dylan.mylibrary.ui.filedownloader;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dylan.library.io.FileDownLoader;
import com.dylan.library.io.FileUtils;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.widget.CircleRingProgressView;
import com.dylan.mylibrary.R;
import com.hjq.toast.Toaster;

/**
 * Created by Dylan on 2017/12/9.
 */

public class FileDownLoaderActivity extends Activity{
    private EditText edittext;
    private Button button_download;
    private ProgressBar pb;
    private TextView text_progress;
    private CircleRingProgressView crProgress1;
    private FileDownLoader fileDownLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filedownloader);

        edittext = (EditText) findViewById(R.id.url_editext);
        button_download = (Button) findViewById(R.id.download_button);
        pb = (ProgressBar) findViewById(R.id.download_progressbar);
        pb.setMax(100);
        text_progress = (TextView) findViewById(R.id.tv_progress);
        crProgress1=findViewById(R.id.crProgressView1);
        crProgress1.setCenterBackgroundColor(Color.WHITE);
        crProgress1.setRingStrokeWidth(DensityUtils.dp2px(this,8));
        crProgress1.setRingProgressColor(Color.parseColor("#FFFADB22"));
        crProgress1.setRingBackgroundColor(Color.parseColor("#FF525252"));
        crProgress1.setCenterText("视频\n下载中");
        crProgress1.setCenterTextBold(0.5f);
        crProgress1.setCenterTextColor(Color.BLACK);
        crProgress1.setCenterTextSize(18);

        fileDownLoader=new FileDownLoader();


        button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadUrl=edittext.getText().toString();
                fileDownLoader.downLoad(downloadUrl, "" );
                fileDownLoader.setDownLoadListener(new FileDownLoader.DownLoadListener() {


                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(int erroType,final String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toaster.show(error);
                            }
                        });

                    }

                    @Override
                    public void onProgress(long totalSize, long hasDownLoadSize, final int progressPercent) {
                        pb.setProgress(progressPercent);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_progress.setText(progressPercent+"%");
                                crProgress1.setProgress(progressPercent,progressPercent+"%");
                            }
                        });
                    }

                    @Override
                    public void onComplete(long totalSize, final String downLoadFilePath, boolean loadFromCache) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button_download.setEnabled(true);
                                FileUtils.notifyScanFile(FileDownLoaderActivity.this,downLoadFilePath);
                            }
                        });
                    }


                });
                button_download.setEnabled(false);
            }
        });






    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fileDownLoader!=null)fileDownLoader.cancel();

    }
}
