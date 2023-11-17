package com.dylan.library.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 播放MP3 音频流 适用于接口返回Mp3文件流场景
 */
public class AudioStreamingPlayer {
    private static final int INTIAL_KB_BUFFER = 96 * 10 / 8;//assume 96kbps*10secs/8bits per byte
    private int totalKbRead = 0;
    private final Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    private File downloadingMediaFile;
    private Context context;
    private int counter = 0;
    private static Runnable r;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);


    public AudioStreamingPlayer(Context context) {
        this.context = context;
    }

    public void startStreaming(final String mediaUrl) {
        URLConnection cn = null;
        try {
            cn = new URL(mediaUrl).openConnection();
            cn.addRequestProperty("User-Agent", "NSPlayer/10.0.0.4072 WMFSDK/10.0");
            cn.connect();
            InputStream stream = cn.getInputStream();
            if (stream == null) {
                Log.e(getClass().getName(), "Unable to create InputStream for mediaUrl:" + mediaUrl);
            }
            r = new Runnable() {
                public void run() {
                    try {
                        downloadAudioIncrement(stream);
                    } catch (IOException e) {
                        Log.e(getClass().getName(), "Unable to initialize the MediaPlayer");
                        return;
                    }
                }
            };
            executorService.execute(r);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startStreaming(InputStream inputStream) {
        r = new Runnable() {
            public void run() {
                try {
                    downloadAudioIncrement(inputStream);
                } catch (IOException e) {
                    Log.e(getClass().getName(), "Unable to initialize the MediaPlayer");
                    return;
                }
            }
        };
        executorService.execute(r);
    }

    /**
     * Download the url stream to a temporary location and then call the setDataSource
     * for that local file
     */
    public void downloadAudioIncrement(InputStream stream) throws IOException {
        downloadingMediaFile = new File(context.getCacheDir(), "downloadingMedia.dat");
        if (downloadingMediaFile.exists()) {
            downloadingMediaFile.delete();
        }
        FileOutputStream out = new FileOutputStream(downloadingMediaFile);
        byte buf[] = new byte[16384];
        int totalBytesRead = 0, incrementalBytesRead = 0;
        do {
            int numread = stream.read(buf);
            if (numread <= 0)
                break;
            out.write(buf, 0, numread);
            totalBytesRead += numread;
            incrementalBytesRead += numread;
            totalKbRead = totalBytesRead / 1000;

            testMediaBuffer();
        } while (validateNotInterrupted());
        stream.close();
        if (validateNotInterrupted()) {
            fireDataFullyLoaded();
        }
    }

    private boolean validateNotInterrupted() {
        return true;
    }


    private void testMediaBuffer() {
        Runnable updater = new Runnable() {
            public void run() {
                if (mediaPlayer == null) {
                    //  Only create the MediaPlayer once we have the minimum buffered data
                    if (totalKbRead >= INTIAL_KB_BUFFER) {
                        try {
                            startMediaPlayer();
                        } catch (Exception e) {
                            Log.e(getClass().getName(), "Error copying buffered conent.", e);
                        }
                    }
                } else if (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() <= 1000) {
                    //  NOTE:  The media player has stopped at the end so transfer any existing buffered data
                    //  We test for < 1second of data because the media player can stop when there is still
                    //  a few milliseconds of data left to play
                    transferBufferToMediaPlayer();
                }
            }
        };
        handler.post(updater);
    }

    private void startMediaPlayer() {
        try {
            File bufferedFile = new File(context.getCacheDir(), "playingMedia" + (counter++) + ".dat");
            moveFile(downloadingMediaFile, bufferedFile);
            mediaPlayer = createMediaPlayer(bufferedFile);
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(getClass().getName(), "Error initializing the MediaPlayer.", e);
            return;
        }
    }

    public void pausePlayer() {
        try {
            getMediaPlayer().pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startPlayer() {
        getMediaPlayer().start();
    }

    public void stopPlayer() {
        getMediaPlayer().stop();
    }

    /**
     * 根据文件创建一个mediaplayer对象
     */
    private MediaPlayer createMediaPlayer(File mediaFile)
            throws IOException {
        MediaPlayer mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(
                new MediaPlayer.OnErrorListener() {
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.e(getClass().getName(), "Error in MediaPlayer: (" + what + ") with extra (" + extra + ")");
                        return false;
                    }
                });
        //  It appears that for security/permission reasons, it is better to pass a FileDescriptor rather than a direct path to the File.
        //  Also I have seen errors such as "PVMFErrNotSupported" and "Prepare failed.: status=0x1" if a file path String is passed to
        //  setDataSource().  So unless otherwise noted, we use a FileDescriptor here.
        FileInputStream fis = new FileInputStream(mediaFile);
        mPlayer.setDataSource(fis.getFD());
        mPlayer.prepare();
        return mPlayer;
    }


    private void transferBufferToMediaPlayer() {
        try {
            boolean wasPlaying = mediaPlayer.isPlaying();
            int curPosition = mediaPlayer.getCurrentPosition();
            File oldBufferedFile = new File(context.getCacheDir(), "playingMedia" + counter + ".dat");
            File bufferedFile = new File(context.getCacheDir(), "playingMedia" + (counter++) + ".dat");
            bufferedFile.deleteOnExit();
            moveFile(downloadingMediaFile, bufferedFile);
            mediaPlayer.pause();
            mediaPlayer = createMediaPlayer(bufferedFile);
            mediaPlayer.seekTo(curPosition);
            boolean atEndOfFile = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() <= 1000;
            if (wasPlaying || atEndOfFile) {
                mediaPlayer.start();
            }
            oldBufferedFile.delete();
        } catch (Exception e) {
            Log.e(getClass().getName(), "Error updating to newly loaded content.", e);
        }
    }


    private void fireDataFullyLoaded() {
        Runnable updater = new Runnable() {
            public void run() {
                transferBufferToMediaPlayer();
                downloadingMediaFile.delete();
            }
        };
        handler.post(updater);
    }

    //TODO 这个方法应该可以控制歌曲的播放
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


    /**
     * Move the file in oldLocation to newLocation.
     */
    public void moveFile(File oldLocation, File newLocation)
            throws IOException {
        if (oldLocation.exists()) {
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(oldLocation));
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(newLocation, false));
            try {
                byte[] buff = new byte[8192];
                int numChars;
                while ((numChars = reader.read(buff, 0, buff.length)) != -1) {
                    writer.write(buff, 0, numChars);
                }
            } catch (IOException ex) {
                throw new IOException("IOException when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
            } finally {
                try {
                    if (reader != null) {
                        writer.close();
                        reader.close();
                    }
                } catch (IOException ex) {
                    Log.e(getClass().getName(), "Error closing files when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
                }
            }
        } else {
            throw new IOException("Old location does not exist when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
        }
    }


    public void release(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        executorService.shutdownNow();
    }
}
