package com.dylan.mylibrary.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dylan.library.widget.VoiceLineView;
import com.dylan.mylibrary.R;

import java.io.File;
import java.io.IOException;

public class VoiceLineDemoActivity extends AppCompatActivity implements Runnable{
    private MediaRecorder mMediaRecorder;
    private boolean isAlive = true;
    private VoiceLineView voiceLineView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / 100;
            double db = 0;// 分贝
            if (ratio > 1)
                db = 30 * Math.log10(ratio);
            voiceLineView.setVolume((int) (db));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voiceline);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                    ||ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    ||ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;
            }
        }
        toStart();
    }

    private void toStart() {
        voiceLineView = (VoiceLineView) findViewById(R.id.voicLine);
        voiceLineView.start();
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();

        /* ②setAudioSource/setVedioSource */
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
        /*
         * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
         * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
         */
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        /* ③准备 */
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "hello.log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setMaxDuration(1000 * 60 * 10);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* ④开始 */
        try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(this);
        thread.start();
    }


    @Override
    public void run() {
        while (isAlive) {
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED
                    ||ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                    ||ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                toStart();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mMediaRecorder.stop();
        super.onDestroy();
    }
}
