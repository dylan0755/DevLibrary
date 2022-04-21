// This file was written by me, Bill Cox in 2011.
// I place this file into the public domain.  Feel free to copy from it.
// Note, however that libsonic, which this application links to,
// is licensed under LGPL.  You can link to it in your commercial application,
// but any changes you make to sonic.c or sonic.h need to be shared under
// the LGPL license.

package com.dylan.mylibrary.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.dylan.library.io.FileUtils;
import com.dylan.library.media.Sound;
import com.dylan.library.media.VideoUtils;
import com.dylan.library.utils.AndKit;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.ToastUtils;
import com.dylan.mylibrary.R;

import java.io.File;
import java.io.IOException;


public class SonicTestActivity extends AppCompatActivity {
    TextView tvSelectFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sonic);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);


        tvSelectFile = findViewById(R.id.tvSelectFile);
        findViewById(R.id.btnSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
                startActivityForResult(intent, 100);
            }
        });
    }



    public void play(View view) {
        if (EmptyUtils.isEmpty(tvSelectFile)) {
            ToastUtils.show("请选择视频文件");
            return;
        }
        FileUtils.createDirIfNotExists(Environment.getExternalStorageDirectory().getPath() + "/1");
       new Thread(new Runnable() {
           @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
           @Override
           public void run() {
               final EditText speedEdit = findViewById(R.id.speed);
               final EditText pitchEdit = findViewById(R.id.pitch);
               final EditText rateEdit = findViewById(R.id.rate);
               float speed = Float.parseFloat(speedEdit.getText().toString());
               float pitch = Float.parseFloat(pitchEdit.getText().toString());
               float rate = Float.parseFloat(rateEdit.getText().toString());


               String VideoIn=tvSelectFile.getText().toString();
               String dirPath=new File(VideoIn).getParentFile().getPath();
               String videoOut=dirPath+"/testS."+ FileUtils.getFileSuffixFromPath(VideoIn);
               Sound sound=new Sound();
               sound.setPitch(pitch);
               sound.setRate(rate);
               sound.setSpeed(speed);
               try {
                   VideoUtils.changVideoSound(SonicTestActivity.this,VideoIn,videoOut,sound);
                   FileUtils.notifyScanFile(SonicTestActivity.this,videoOut);
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           ToastUtils.show("完成");
                       }
                   });
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }).start();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedVideo = data.getData();
                if (selectedVideo == null) {
                    return;
                }
                final File file = FileUtils.getFileByUri( this,selectedVideo);
                if (file == null) {
                    ToastUtils.show("找不到视频文件");
                    return;
                }
                tvSelectFile.setText(file.getAbsolutePath());


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}