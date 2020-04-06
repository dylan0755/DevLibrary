package com.dylan.mylibrary.ui.filedownloader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dylan.mylibrary.R;

/**
 * Created by Dylan on 2017/12/9.
 */

public class FileDownLoaderActivity extends Activity{
    private EditText edittext;
    private Button button_download;
    private ProgressBar pb;
    private TextView text_progress;
    private NotificationDownLoader mDownLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filedownloader);
        mDownLoader=new NotificationDownLoader(this);
        edittext = (EditText) findViewById(R.id.url_editext);
        button_download = (Button) findViewById(R.id.download_button);
        pb = (ProgressBar) findViewById(R.id.download_progressbar);
        pb.setMax(100);
        text_progress = (TextView) findViewById(R.id.tv_progress);
        button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downloadUrl=edittext.getText().toString();
                mDownLoader.downLoad(v.getContext(),downloadUrl,"");
                button_download.setEnabled(false);
            }
        });






    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
