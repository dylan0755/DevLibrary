package com.dylan.mylibrary.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.dylan.library.io.FileUtils;
import com.dylan.library.media.CenterCropVideoView;
import com.dylan.library.utils.IntentUtils;
import com.dylan.mylibrary.R;

import java.io.File;

/**
 * Author: Dylan
 * Date: 2022/4/15
 * Desc:
 */

public class ClipVideoViewActivity extends AppCompatActivity {
    CenterCropVideoView videoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipvideo);
        videoView=findViewById(R.id.videoView);
        startActivityForResult(IntentUtils.getChooseVideoIntent(),100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release();
    }

   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            Uri uri = data.getData();
            if (uri == null) return ;
            File file = FileUtils.getFileByUri(uri,this);
            if (file == null) return ;
            videoView.start(file.getAbsolutePath());
        }

    }
}
