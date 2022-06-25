package com.dylan.library.media;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import com.dylan.library.io.FileUtils;
import com.dylan.library.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: Dylan
 * Date: 2022/06/25
 * Desc:
 */
public class AudioRecorder {
    private static  String FILE_NAME;
    private AudioRecord audioRecord = null;
    private int recordBufsize = 0;
    private boolean isRecording = false;


    private Thread recordingThread;



    private void createAudioRecord() {
        recordBufsize = AudioRecord
                .getMinBufferSize(16000,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
        Logger.i("audioRecordTest", "size->" + recordBufsize);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                recordBufsize);
    }

    public void startRecord() {
        if (isRecording) {
            return;
        }
        createAudioRecord();
        FileUtils.createDirIfNotExists(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString());
        FILE_NAME=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString() + File.separator + "test_"+System.currentTimeMillis()+".pcm";
        isRecording = true;
        audioRecord.startRecording();
        Logger.i("audioRecordTest", "开始录音");
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte data[] = new byte[recordBufsize];
                File file = new File(FILE_NAME);
                FileOutputStream os = null;
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                        Logger.i("audioRecordTest", "创建录音文件->" + FILE_NAME);
                    }
                    os = new FileOutputStream(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int read;
                if (os != null) {
                    while (isRecording) {
                        read = audioRecord.read(data, 0, recordBufsize);
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                os.write(data);
                                Logger.i("audioRecordTest", "写录音数据->" + read);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                try {
                    if (os != null) os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        recordingThread.start();
    }


    public  String getFileName() {
        return FILE_NAME;
    }

    public void stopRecord() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            Logger.i("audioRecordTest", "停止录音");
            audioRecord.release();
            audioRecord = null;
            recordingThread = null;
        }
    }


}
