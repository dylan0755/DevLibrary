package com.dylan.mylibrary.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PlayPcmUtils {
    AudioTrack audioTrack;
    File pcmPath;
    int bufferSize;
    boolean isPlaying;

    public PlayPcmUtils(String pcmP) {
        pcmPath = new File(pcmP);
        bufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                bufferSize, AudioTrack.MODE_STREAM);
    }

    public void playPcm() {
        PlayPCMRecord playPCMRecord = new PlayPCMRecord();
        playPCMRecord.start();
    }

    private class PlayPCMRecord  extends Thread  {
        @Override
        public void run() {
            int bufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
            FileInputStream fis = null;
            try {
                audioTrack.play();
                fis = new FileInputStream(pcmPath);
                byte[] buffer = new byte[bufferSize];
                int len = 0;
                isPlaying = true;
                while ((len = fis.read(buffer)) != -1) {
//                    Log.d(TAG, "playPCMRecord: len " + len);
                    audioTrack.write(buffer, 0, len);
                }

            } catch (Exception e) {
                Log.e("wqs", "playPCMRecord: e : " + e);
            } finally {
                isPlaying = false;
                if (audioTrack != null) {
                    audioTrack.stop();
                    audioTrack = null;
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

}
