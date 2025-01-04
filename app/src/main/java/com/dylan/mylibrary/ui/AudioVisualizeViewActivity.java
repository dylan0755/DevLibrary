package com.dylan.mylibrary.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import com.dylan.common.BaseActivity;
import com.dylan.library.utils.DataTypeConversionUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.PermissionUtils;
import com.dylan.library.widget.visualizeview.AudioVisualizeView;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.util.PlayPcmUtils;

import org.jtransforms.fft.DoubleFFT_1D;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Author: Dylan
 * Date: 2023/12/3
 * Desc: 音频频谱动画
 */


public class AudioVisualizeViewActivity extends BaseActivity {
    AudioVisualizeView chatTalkVisualizeView, chatListenVisualizeView;
    private MediaPlayer mPlayer;
    private Button tvAudioPlay, tvStartAudioRecord;

    File recordingFile;//储存AudioRecord录下来的文件
    boolean isRecording = false; //true表示正在录音
    AudioRecord audioRecord = null;
    File parent = null;//文件目录
    int bufferSize = 0;//最小缓冲区大小
    int sampleRateInHz = 44100;//采样率
    int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT; //量化位数

    @Override
    public int getLayoutId() {
        return R.layout.activity_audiovisualizeview;
    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        chatTalkVisualizeView = findViewById(R.id.chatTalkVisualizeView);
        chatListenVisualizeView = findViewById(R.id.chatListenVisualizeView);


        tvAudioPlay = findViewById(R.id.tvAudioPlay);
        tvStartAudioRecord = findViewById(R.id.tvStartAudioRecord);
        tvAudioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPlayer == null || !mPlayer.isPlaying()) {
                    playAudio();
                } else {
                    stopPlay();
                }
            }
        });

        tvStartAudioRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtils.hasRecordAudioPermission(AudioVisualizeViewActivity.this)) {
                    PermissionUtils.requestRecordAudioPerm(AudioVisualizeViewActivity.this);
                    return;
                }

                if (!isRecording) {
                    record();
                } else {
                    isRecording = false;
                }

            }
        });
        init();
    }

    private void playAudio() {
        String fileName = "想起 - 韩雪.mp3"; //子目录文件
        AssetFileDescriptor fd = null;
        try {
            fd = getAssets().openFd(fileName);
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    tvAudioPlay.setText("播放");
                }
            });
            mPlayer.start();
            chatTalkVisualizeView.bindAudioSessionId(mPlayer.getAudioSessionId());
            tvAudioPlay.setText("停止");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopPlay() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            chatTalkVisualizeView.release();
        }
        tvAudioPlay.setText("播放");
    }


    public void init() {
        bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);//计算最小缓冲区
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat, bufferSize);//创建AudioRecorder对象

        parent = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AudiioRecordTest");

        if (!parent.exists())
            parent.mkdirs();//创建文件夹
    }


    //开始录音
    public void record() {
        isRecording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRecording = true;

                recordingFile = new File(parent, "audiotest.pcm");

                if (recordingFile.exists()) {
                    recordingFile.delete();
                }

                try {
                    recordingFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.e("创建储存音频文件出错");
                }


                try {
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(recordingFile)));
                    byte[] buffer = new byte[bufferSize];
                    audioRecord.startRecording();//开始录音
                    tvStartAudioRecord.post(new Runnable() {
                        @Override
                        public void run() {
                            tvStartAudioRecord.setText("停止录音");
                        }
                    });
                    long lastAudioTime=0;
                    while (isRecording) {
                        int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);



                        Log.e("run: ", "buffer="+buffer.length);
                        if (System.currentTimeMillis() - lastAudioTime > 100){
                            double[] realData = DataTypeConversionUtils.toDoubleArray(buffer);
                            DoubleFFT_1D fftInstance = new DoubleFFT_1D(realData.length);
                            fftInstance.realForward(realData);
                            byte[] fft=DataTypeConversionUtils.toByteArray(realData);

                            Log.e("run: ", Arrays.toString(fft));
                            lastAudioTime = System.currentTimeMillis();
                            chatListenVisualizeView.post(new Runnable() {
                                @Override
                                public void run() {
                                    chatListenVisualizeView.receiveAudioData(fft);
                                }
                            });
                        }


                        if (bufferReadResult == AudioRecord.ERROR_INVALID_OPERATION || bufferReadResult == AudioRecord.ERROR_BAD_VALUE) {
                            continue;
                        }
                        if (bufferReadResult == 0 || bufferReadResult == -1) {
                            break;
                        }
                        dos.write(buffer);
                    }
                    tvStartAudioRecord.post(new Runnable() {
                        @Override
                        public void run() {
                            tvStartAudioRecord.setText("开始录音");
                        }
                    });

                    audioRecord.stop();//停止录音
                    dos.close();
                    //play
//                    PlayPcmUtils playPcmUtils = new PlayPcmUtils(recordingFile.getAbsolutePath());
//                    playPcmUtils.playPcm();
                } catch (Throwable t) {
                    t.printStackTrace();
                    Logger.e("Recording Failed");
                }

            }
        }).start();

    }

    //停止录音
    public void stopRecording() {
        isRecording = false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer!=null&&mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
        }
    }
}