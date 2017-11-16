package com.dylan.library.media.voicerecord;

import android.media.MediaRecorder;

import java.io.File;
import java.util.UUID;

public class MediaRecordHelper {

    private MediaRecorder mediaRecorder;
    private String dir;
    private String currentFilePath;
    private static MediaRecordHelper audioInstance; // 单例
    public boolean isPrepared = false;
    private boolean cleanHistory = false;

    private MediaRecordHelper(String dir) {
        this.dir = dir;
    }


    public static MediaRecordHelper getInstance(String dir) {
        if (audioInstance == null) {
            synchronized (MediaRecordHelper.class) {
                if (audioInstance == null) {
                    audioInstance = new MediaRecordHelper(dir);
                }
            }
        }
        return audioInstance;
    }

    public void clearHistoryBeforeStart(boolean bl) {
        cleanHistory = bl;
    }

    public void start(AudioRecordStartListener listener) {
        if (listener == null) return;
        try {
            isPrepared = false;
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            } else {
                if (cleanHistory) {
                    File[] historyFiles = fileDir.listFiles();
                    for (File f : historyFiles) {
                        if (f != null) f.delete();
                    }
                }
            }

            String fileName = generateFileName();
            File file = new File(fileDir, fileName);

            currentFilePath = file.getAbsolutePath();
            mediaRecorder = new MediaRecorder();
            // 设置输出文件
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            // 设置音频源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            // 设置音频编码
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            // 准备结束
            isPrepared = true;
            listener.hasStarted();
        } catch (Exception e) {
            e.printStackTrace();
            listener.startFailure(e);
        }
    }

    public void finish() {
        if (isPrepared) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
        mediaRecorder = null;

    }

    public void cancel() {
        finish();
        if (currentFilePath != null) {
            File file = new File(currentFilePath);
            file.delete();
            currentFilePath = null;
        }
    }

    /**
     * 随机生成文件名称
     *
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {
        if (isPrepared) {
            try {
                // 振幅范围mediaRecorder.getMaxAmplitude():1-32767
                return maxLevel * mediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {
            }
        }
        return 1;
    }

    public String getCurrentPath() {
        return currentFilePath;
    }

    public interface AudioRecordStartListener {
        void hasStarted();

        void startFailure(Exception e);
    }
}
