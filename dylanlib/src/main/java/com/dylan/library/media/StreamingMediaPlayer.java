package com.dylan.library.media;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * MediaPlayer does not yet support streaming from external URLs so this class provides a pseudo-streaming function
 * by downloading the content incrementally & playing as soon as we get enough audio in our temporary storage.
 */
public class StreamingMediaPlayer extends Service {
    private static final int INTIAL_KB_BUFFER = 96 * 10 / 8;//assume 96kbps*10secs/8bits per byte
    private TextView textStreamed;

    private ImageButton playButton;

    private ProgressBar progressBar;

    //  Track for display by progressBar
    private long mediaLengthInKb, mediaLengthInSeconds;
    private int totalKbRead = 0;
    // Create Handler to call View updates on the main UI thread.
    private final Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    private File downloadingMediaFile;
    private boolean isInterrupted;
    private Context context;
    private int counter = 0;
    private static Runnable r;
    private static Thread playerThread;
    private LocalBinder localBinder = new LocalBinder();
    private MediaPlayer player;
    private boolean isPause = false;     //播放器是否处于暂停状态
    private boolean isSame = false;      //所点播歌曲是否是当前播放歌曲
    private Integer position = -1;       //设置播放标记
    private List<String> music_name;     //歌曲列表
    private List<String> music_path;

    public StreamingMediaPlayer(Context context, TextView textStreamed, ImageButton playButton, Button streamButton, ProgressBar progressBar) {
        this.context = context;
        this.textStreamed = textStreamed;
        this.playButton = playButton;
        this.progressBar = progressBar;
    }

    /**
     * Progressivly download the media to a temporary location and update the MediaPlayer as new content becomes available.
     */
    public void startStreaming(final String mediaUrl, long mediaLengthInKb, long mediaLengthInSeconds) throws IOException {

        this.mediaLengthInKb = mediaLengthInKb;
        this.mediaLengthInSeconds = mediaLengthInSeconds;

        r = new Runnable() {
            public void run() {
                try {
                    Log.i("downloadAudioIncrement", "downloadAudioIncrement");
                    downloadAudioIncrement(mediaUrl);
                } catch (IOException e) {
                    Log.e(getClass().getName(), "Unable to initialize the MediaPlayer for fileUrl=" + mediaUrl, e);
                    return;
                }
            }
        };
        playerThread = new Thread(r);
        playerThread.start();
        //new Thread(r).start();
    }

    /**
     * Download the url stream to a temporary location and then call the setDataSource
     * for that local file
     */
    public void downloadAudioIncrement(String mediaUrl) throws IOException {

        URLConnection cn = new URL(mediaUrl).openConnection();
        cn.addRequestProperty("User-Agent", "NSPlayer/10.0.0.4072 WMFSDK/10.0");
        cn.connect();
        InputStream stream = cn.getInputStream();
        if (stream == null) {
            Log.e(getClass().getName(), "Unable to create InputStream for mediaUrl:" + mediaUrl);
        }

        downloadingMediaFile = new File(context.getCacheDir(), "downloadingMedia.dat");

        // Just in case a prior deletion failed because our code crashed or something, we also delete any previously
        // downloaded file to ensure we start fresh.  If you use this code, always delete
        // no longer used downloads else you'll quickly fill up your hard disk memory.  Of course, you can also
        // store any previously downloaded file in a separate data cache for instant replay if you wanted as well.
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
            fireDataLoadUpdate();
        } while (validateNotInterrupted());
        stream.close();
        if (validateNotInterrupted()) {
            fireDataFullyLoaded();
        }
    }

    private boolean validateNotInterrupted() {
        if (isInterrupted) {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
                //mediaPlayer.release();
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Test whether we need to transfer buffered data to the MediaPlayer.
     * Interacting with MediaPlayer on non-main UI thread can causes crashes to so perform this using a Handler.
     */
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

            // We double buffer the data to avoid potential read/write errors that could happen if the
            // download thread attempted to write at the same time the MediaPlayer was trying to read.
            // For example, we can't guarantee that the MediaPlayer won't open a file for playing and leave it locked while
            // the media is playing.  This would permanently deadlock the file download.  To avoid such a deadloack,
            // we move the currently loaded data to a temporary buffer file that we start playing while the remaining
            // data downloads.
            moveFile(downloadingMediaFile, bufferedFile);

            Log.e(getClass().getName(), "Buffered File path: " + bufferedFile.getAbsolutePath());
            Log.e(getClass().getName(), "Buffered File length: " + bufferedFile.length() + "");

            mediaPlayer = createMediaPlayer(bufferedFile);

            // We have pre-loaded enough content and started the MediaPlayer so update the buttons & progress meters.
            mediaPlayer.start();
            startPlayProgressUpdater();
            playButton.setEnabled(true);
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

    /**
     * 把缓存转化成mediaplay对象
     * Transfer buffered data to the MediaPlayer.
     * NOTE: Interacting with a MediaPlayer on a non-main UI thread can cause thread-lock and crashes so
     * this method should always be called using a Handler.
     */
    private void transferBufferToMediaPlayer() {
        try {
            // First determine if we need to restart the player after transferring data...e.g. perhaps the user pressed pause
            boolean wasPlaying = mediaPlayer.isPlaying();
            int curPosition = mediaPlayer.getCurrentPosition();

            // Copy the currently downloaded content to a new buffered File.  Store the old File for deleting later.
            File oldBufferedFile = new File(context.getCacheDir(), "playingMedia" + counter + ".dat");
            File bufferedFile = new File(context.getCacheDir(), "playingMedia" + (counter++) + ".dat");
            //  This may be the last buffered File so ask that it be delete on exit.  If it's already deleted, then this won't mean anything.  If you want to
            // keep and track fully downloaded files for later use, write caching code and please send me a copy.
            bufferedFile.deleteOnExit();
            moveFile(downloadingMediaFile, bufferedFile);
            // Pause the current player now as we are about to create and start a new one.  So far (Android v1.5),
            // this always happens so quickly that the user never realized we've stopped the player and started a new one
            mediaPlayer.pause();
            // Create a new MediaPlayer rather than try to re-prepare the prior one.
            mediaPlayer = createMediaPlayer(bufferedFile);
            mediaPlayer.seekTo(curPosition);

            //  Restart if at end of prior buffered content or mediaPlayer was previously playing.
            //    NOTE:  We test for < 1second of data because the media player can stop when there is still
            //  a few milliseconds of data left to play
            boolean atEndOfFile = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() <= 1000;
            if (wasPlaying || atEndOfFile) {
                mediaPlayer.start();
            }
            // Lastly delete the previously playing buffered File as it's no longer needed.
            oldBufferedFile.delete();

        } catch (Exception e) {
            Log.e(getClass().getName(), "Error updating to newly loaded content.", e);
        }
    }

    private void fireDataLoadUpdate() {
        Runnable updater = new Runnable() {
            public void run() {
                //textStreamed.setText((totalKbRead + " Kb read"));
                float loadProgress = ((float) totalKbRead / (float) mediaLengthInKb);
                //progressBar.setSecondaryProgress((int)(loadProgress*100));
            }
        };
        handler.post(updater);
    }

    private void fireDataFullyLoaded() {
        Runnable updater = new Runnable() {
            public void run() {
                transferBufferToMediaPlayer();
                // Delete the downloaded File as it's now been transferred to the currently playing buffer file.
                downloadingMediaFile.delete();
                //textStreamed.setText(("Audio full loaded: " + totalKbRead + " Kb read"));
            }
        };
        handler.post(updater);
    }

    //TODO 这个方法应该可以控制歌曲的播放
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void startPlayProgressUpdater() {
        float progress = (((float) mediaPlayer.getCurrentPosition() / 1000) / mediaLengthInSeconds);
        progressBar.setProgress((int) (progress * 100));

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    public void interrupt() {
        playButton.setEnabled(false);
        isInterrupted = true;
        validateNotInterrupted();
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

    /**
     * 獲取service中的播放器对象
     *
     * @return 播放器对象
     */
    public MediaPlayer getPlayer() {
        return this.player;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        /**
         * 1.现在需要的就是做从PlayActivity里获取歌曲列表，和歌曲路径，歌曲手名
         *       并存放到各个集合里
         * 2.之后就是对对这些数组进行处理
         */
        music_name = new ArrayList<String>();
        music_path = new ArrayList<String>();
        String info = intent.getStringExtra("info");
        //songPath = intent.getStringExtra("songPath");
        Toast.makeText(getApplicationContext(), "歌曲播放异常", Toast.LENGTH_SHORT).show();
        player = new MediaPlayer();
        try {
            playMusic(info);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "歌曲播放异常", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    //播放音乐
    private void playMusic(String info) throws Exception {
        if ("play".equals(info)) {
            if (isPause) {// 暂停后，继续播放
                player.start();
                isPause = false;
            } else if (isSame) {// 如果现在播放和与所点播歌曲时同一首，继续播放所选歌曲
                player.start();
            } else {// 点播某一首歌曲
                play();
            }
        } else if ("pause".equals(info)) {
            player.pause();// 暂停
            isPause = true;
        } else if ("before".equals(info)) {
            playBefore();// 播放上一首
        } else if ("after".equals(info)) {
            playAfter();// 播放下一首
        }
    }

    private void play() throws Exception {
        //TODO  获取歌曲路径

        try {
            Log.i("playtest", "playtest");
            //    myApp.setPlaying_position(position);  //设置歌曲 当前的播放标记
            player.reset();
            //player.setDataSource(songPath);
            player.start();
            //musicName = music_name.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void playBefore() throws Exception {
        if (position == 0) {
            position = music_name.size() - 1;
        } else {
            position--;
        }
        play();
    }

    private void playAfter() throws Exception {
        if (position == 0) {
            position = music_name.size() + 1;
        } else {
            position++;
        }
        play();
    }

    public class LocalBinder extends Binder {
        public StreamingMediaPlayer getService() {
            return StreamingMediaPlayer.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }
}
