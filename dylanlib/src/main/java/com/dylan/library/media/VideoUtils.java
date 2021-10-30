package com.dylan.library.media;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Pair;


import com.dylan.library.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangwei on 2018/3/8 0008.
 */
@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoUtils {


    private static final Object TAG = VideoUtils.class.getSimpleName() ;


    public static boolean hasAudioTrack(String videoIn) {
        MediaExtractor oriExtrator = new MediaExtractor();
        try {
            oriExtrator.setDataSource(videoIn);
            int numTracks = oriExtrator.getTrackCount();
            for(int i = 0; i < numTracks; ++i) {
                MediaFormat format = oriExtrator.getTrackFormat(i);
                String mime = format.getString("mime");
                if (mime.startsWith("audio/")) {
                    oriExtrator.release();
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        oriExtrator.release();
        return false;
    }





    public static void changVideoSound(Context context, final String videoIn, final String videoOut, Sound sound) throws IOException {
        File cacheDir = new File(Environment.getExternalStorageDirectory().toString(), "1");
        cacheDir.mkdir();
        MediaExtractor oriExtrator = new MediaExtractor();
        oriExtrator.setDataSource(videoIn);
        int oriAudioIndex = VideoUtils.selectTrack(oriExtrator, true);
        if (oriAudioIndex < 0) {
            Logger.e("no audio stream!");
            oriExtrator.release();
            return;
        }
        long time = System.currentTimeMillis();
        final File videoWavFileBefore = new File(cacheDir, "videoBefore" + time + ".wav");
        final File videoChangSoundPcmFile = new File(cacheDir, "video" + time + "ChangeSound.pcm");
        final File videoFinalWavFile = new File(cacheDir, "videoFinal" + time + ".wav");

        MediaSource mediaSource=new MediaSource(videoIn);
        AudioUtils.extractWav(context,mediaSource, videoWavFileBefore.getAbsolutePath(), null, null);

        MediaFormat audioTrackFormat = oriExtrator.getTrackFormat(oriAudioIndex);
        final int sampleRate = audioTrackFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int channelCount = audioTrackFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT) ? audioTrackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT) : 1;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        if (channelCount == 2) {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        }
        AudioUtils.changeSound(videoWavFileBefore.getAbsolutePath(),videoChangSoundPcmFile.getAbsolutePath(),sound);
        new PcmToWavUtil(sampleRate, channelConfig, channelCount, AudioFormat.ENCODING_PCM_16BIT).pcmToWav(videoChangSoundPcmFile.getAbsolutePath(), videoFinalWavFile.getAbsolutePath());



        final int TIMEOUT_US = 2500;
        //重新将声音变化过后的pcm写入
        int audioBitrate = AudioUtils.getAudioBitrate(audioTrackFormat);
        int oriVideoIndex = VideoUtils.selectTrack(oriExtrator, false);
        MediaFormat oriVideoFormat = oriExtrator.getTrackFormat(oriVideoIndex);
        int rotation = oriVideoFormat.containsKey(MediaFormat.KEY_ROTATION) ? oriVideoFormat.getInteger(MediaFormat.KEY_ROTATION) : 0;
        MediaMuxer mediaMuxer = new MediaMuxer(videoOut, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mediaMuxer.setOrientationHint(rotation);
        int muxerVideoIndex = mediaMuxer.addTrack(oriVideoFormat);
        int muxerAudioIndex = mediaMuxer.addTrack(audioTrackFormat);

        //重新写入音频
        mediaMuxer.start();

        MediaExtractor pcmExtrator = new MediaExtractor();
        pcmExtrator.setDataSource(videoFinalWavFile.getAbsolutePath());
        int audioTrack = VideoUtils.selectTrack(pcmExtrator, true);
        pcmExtrator.selectTrack(audioTrack);
        MediaFormat pcmTrackFormat = pcmExtrator.getTrackFormat(audioTrack);
        int maxBufferSize = AudioUtils.getAudioMaxBufferSize(pcmTrackFormat);
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        MediaFormat encodeFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);//参数对应-> mime type、采样率、声道数
        encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, audioBitrate);//比特率
        encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxBufferSize);
        MediaCodec encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        encoder.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        encoder.start();
        boolean encodeInputDone = false;
        boolean encodeDone = false;
        long lastAudioFrameTimeUs = -1;
        final int AAC_FRAME_TIME_US = 1024 * 1000 * 1000 / sampleRate;
        boolean detectTimeError = false;
        try {
            while (!encodeDone) {
                int inputBufferIndex = encoder.dequeueInputBuffer(TIMEOUT_US);
                if (!encodeInputDone && inputBufferIndex >= 0) {
                    long sampleTime = pcmExtrator.getSampleTime();
                    if (sampleTime < 0) {
                        encodeInputDone = true;
                        encoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        int flags = pcmExtrator.getSampleFlags();
                        buffer.clear();
                        int size = pcmExtrator.readSampleData(buffer, 0);
                        ByteBuffer inputBuffer = encoder.getInputBuffer(inputBufferIndex);
                        inputBuffer.clear();
                        inputBuffer.put(buffer);
                        inputBuffer.position(0);
                        Logger.d("audio queuePcmBuffer " + sampleTime / 1000 + " size:" + size);
                        encoder.queueInputBuffer(inputBufferIndex, 0, size, sampleTime, flags);
                        pcmExtrator.advance();
                    }
                }

                while (true) {
                    int outputBufferIndex = encoder.dequeueOutputBuffer(info, TIMEOUT_US);
                    if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        break;
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newFormat = encoder.getOutputFormat();
                        Logger.d(TAG, "audio decode newFormat = " + newFormat);
                    } else if (outputBufferIndex < 0) {
                        //ignore
                        Logger.e(TAG, "unexpected result from audio decoder.dequeueOutputBuffer: " + outputBufferIndex);
                    } else {
                        if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                            encodeDone = true;
                            break;
                        }
                        ByteBuffer encodeOutputBuffer = encoder.getOutputBuffer(outputBufferIndex);
                        Logger.d(TAG, "audio writeSampleData " + info.presentationTimeUs + " size:" + info.size + " flags:" + info.flags);
                        if (!detectTimeError && lastAudioFrameTimeUs != -1 && info.presentationTimeUs < lastAudioFrameTimeUs + AAC_FRAME_TIME_US) {
                            //某些情况下帧时间会出错，目前未找到原因（系统相机录得双声道视频正常，我录的单声道视频不正常）
//                            Logger.d(TAG, "audio 时间戳错误，lastAudioFrameTimeUs:" + lastAudioFrameTimeUs + " " +
//                                    "info.presentationTimeUs:" + info.presentationTimeUs);
                            detectTimeError = true;
                        }
                        if (detectTimeError) {
                            info.presentationTimeUs = lastAudioFrameTimeUs + AAC_FRAME_TIME_US;
                           // Logger.d(TAG, "audio 时间戳错误，使用修正的时间戳:" + info.presentationTimeUs);
                            detectTimeError = false;
                        }
                        if (info.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                            lastAudioFrameTimeUs = info.presentationTimeUs;
                        }
                        mediaMuxer.writeSampleData(muxerAudioIndex, encodeOutputBuffer, info);

                        encodeOutputBuffer.clear();
                        encoder.releaseOutputBuffer(outputBufferIndex, false);
                    }
                }
            }
            //重新将视频写入
            if (oriAudioIndex >= 0) {
                oriExtrator.unselectTrack(oriAudioIndex);
            }
            oriExtrator.selectTrack(oriVideoIndex);
            oriExtrator.seekTo(0, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
            maxBufferSize = oriVideoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);

            int frameRate = oriVideoFormat.containsKey(MediaFormat.KEY_FRAME_RATE) ? oriVideoFormat.getInteger(MediaFormat.KEY_FRAME_RATE) : (int) Math.ceil(VideoUtils.getAveFrameRate(mediaSource));
            buffer = ByteBuffer.allocateDirect(maxBufferSize);
            final int VIDEO_FRAME_TIME_US = (int) (1000 * 1000f / frameRate);
            long lastVideoFrameTimeUs = -1;
            detectTimeError = false;
            while (true) {
                long sampleTimeUs = oriExtrator.getSampleTime();
                if (sampleTimeUs == -1) {
                    break;
                }
                info.presentationTimeUs = sampleTimeUs;
                info.flags = oriExtrator.getSampleFlags();
                info.size = oriExtrator.readSampleData(buffer, 0);
                if (info.size < 0) {
                    break;
                }
                //写入视频
                if (!detectTimeError && lastVideoFrameTimeUs != -1 && info.presentationTimeUs < lastVideoFrameTimeUs + VIDEO_FRAME_TIME_US) {
                    //某些视频帧时间会出错
//                    Logger.e(TAG, "video 时间戳错误，lastVideoFrameTimeUs:" + lastVideoFrameTimeUs + " " +
//                            "info.presentationTimeUs:" + info.presentationTimeUs + " VIDEO_FRAME_TIME_US:" + VIDEO_FRAME_TIME_US);
                    detectTimeError = true;
                }
                if (detectTimeError) {
                    info.presentationTimeUs = lastVideoFrameTimeUs + VIDEO_FRAME_TIME_US;
                    //Logger.e(TAG, "video 时间戳错误，使用修正的时间戳:" + info.presentationTimeUs);
                    detectTimeError = false;
                }
                if (info.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                    lastVideoFrameTimeUs = info.presentationTimeUs;
                }
                Logger.w(TAG, "video writeSampleData:" + info.presentationTimeUs + " type:" + info.flags + " size:" + info.size);
                mediaMuxer.writeSampleData(muxerVideoIndex, buffer, info);
                oriExtrator.advance();
            }
        } finally {
            videoChangSoundPcmFile.delete();
            videoWavFileBefore.delete();
            videoFinalWavFile.delete();

            try {
                pcmExtrator.release();
                oriExtrator.release();
                mediaMuxer.release();
                encoder.stop();
                encoder.release();
            } catch (Exception e) {
                Logger.e(e);
            }
        }
    }




    public static int selectTrack(MediaExtractor extractor, boolean isAudio) {
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (isAudio) {
                if (mime.startsWith("audio/")) {
                    return i;
                }
            } else {
                if (mime.startsWith("video/")) {
                    return i;
                }
            }
        }
        return -5;
    }


    /**
     * 用于制作全关键帧视频时计算比特率应该为多少
     *
     * @return
     */
    public static int getBitrateForAllKeyFrameVideo(MediaSource input) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        input.setDataSource(extractor);
        int trackIndex = VideoUtils.selectTrack(extractor, false);
        extractor.selectTrack(trackIndex);
        int keyFrameCount = 0;
        int frameCount = 0;
        while (true) {
            int flags = extractor.getSampleFlags();
            if (flags > 0 && (flags & MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
                keyFrameCount++;
            }
            long sampleTime = extractor.getSampleTime();
            if (sampleTime < 0) {
                break;
            }
            frameCount++;
            extractor.advance();
        }
        extractor.release();
        float bitrateMultiple = (frameCount - keyFrameCount) / (float) keyFrameCount + 1;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        input.setDataSource(retriever);
        int oriBitrate = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
        retriever.release();
        if (frameCount == keyFrameCount) {
            return oriBitrate;
        }
        return (int) (bitrateMultiple * oriBitrate);
    }

    public static Pair<Integer, Integer> getVideoFrameCount(String input) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(input);
        int trackIndex = VideoUtils.selectTrack(extractor, false);
        extractor.selectTrack(trackIndex);
        int keyFrameCount = 0;
        int frameCount = 0;
        while (true) {
            int flags = extractor.getSampleFlags();
            if (flags > 0 && (flags & MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
                keyFrameCount++;
            }
            long sampleTime = extractor.getSampleTime();
            if (sampleTime < 0) {
                break;
            }
            frameCount++;
            extractor.advance();
        }
        extractor.release();
        return new Pair<>(keyFrameCount, frameCount);
    }

    public static int getFrameRate(MediaSource mediaSource) {
        MediaExtractor extractor = new MediaExtractor();
        try {
            mediaSource.setDataSource(extractor);
            int trackIndex = VideoUtils.selectTrack(extractor, false);
            MediaFormat format = extractor.getTrackFormat(trackIndex);
            return format.containsKey(MediaFormat.KEY_FRAME_RATE) ? format.getInteger(MediaFormat.KEY_FRAME_RATE) : -1;
        } catch (IOException e) {
            Logger.e(e);
            return -1;
        } finally {
            extractor.release();
        }
    }

    public static float getAveFrameRate(MediaSource mediaSource) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        mediaSource.setDataSource(extractor);
        int trackIndex = VideoUtils.selectTrack(extractor, false);
        extractor.selectTrack(trackIndex);
        long lastSampleTimeUs = 0;
        int frameCount = 0;
        while (true) {
            long sampleTime = extractor.getSampleTime();
            if (sampleTime < 0) {
                break;
            } else {
                lastSampleTimeUs = sampleTime;
            }
            frameCount++;
            extractor.advance();
        }
        extractor.release();
        return frameCount / (lastSampleTimeUs / 1000f / 1000f);
    }

    public static void seekToLastFrame(MediaExtractor extractor, int trackIndex, int durationMs) {
        int seekToDuration = durationMs * 1000;
        if (extractor.getSampleTrackIndex() != trackIndex) {
            extractor.selectTrack(trackIndex);
        }
        extractor.seekTo(seekToDuration, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        while (seekToDuration > 0 && extractor.getSampleTrackIndex() != trackIndex) {
            seekToDuration -= 10000;
            extractor.seekTo(seekToDuration, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);
        }
    }

    public static File getVideoCacheDir(Context context) {
        File cacheDir = new File(context.getCacheDir(), "video/");
        cacheDir.mkdirs();
        return cacheDir;
    }


    public static boolean trySetProfileAndLevel(MediaCodec codec, String mime, MediaFormat format, int profileInt, int levelInt) {
        MediaCodecInfo codecInfo = codec.getCodecInfo();
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mime);
        MediaCodecInfo.CodecProfileLevel[] profileLevels = capabilities.profileLevels;
        if (profileLevels == null) {
            return false;
        }
        for (MediaCodecInfo.CodecProfileLevel level : profileLevels) {
            if (level.profile == profileInt) {
                if (level.level == levelInt) {
                    format.setInteger(MediaFormat.KEY_PROFILE, profileInt);
                    format.setInteger(MediaFormat.KEY_LEVEL, levelInt);
                    return true;
                }
            }
        }
        return false;
    }

    public static int getMaxSupportBitrate(MediaCodec codec, String mime) {
        try {
            MediaCodecInfo codecInfo = codec.getCodecInfo();
            MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mime);
            Integer maxBitrate = capabilities.getVideoCapabilities().getBitrateRange().getUpper();
            return maxBitrate;
        } catch (Exception e) {
            Logger.e(e);
            return -1;
        }
    }

    static List<Long> getFrameTimeStampsList(MediaExtractor extractor){
        List<Long> frameTimeStamps = new ArrayList<>();
        while (true) {
            long sampleTime = extractor.getSampleTime();
            if (sampleTime < 0) {
                break;
            }
            frameTimeStamps.add(sampleTime);
            extractor.advance();
        }
        return frameTimeStamps;
    }

    public static Pair<Integer, Integer> getVideoSize(MediaSource source) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        source.setDataSource(extractor);
        MediaFormat format = extractor.getTrackFormat(VideoUtils.selectTrack(extractor, false));
        int width = format.getInteger(MediaFormat.KEY_WIDTH);
        int height = format.getInteger(MediaFormat.KEY_HEIGHT);
        return new Pair<>(width,height);
    }

    /**
     * 保存文件 存储位置：/storage/emulated/0/DICM/path1/path2/new_photo_file.png
     * String path = savaVideoToMediaStore("videp.mp4", "new video file descrition", "video/mp4");
     */
    public static String savaVideoToMediaStore(Context context, String videoPath, String name, String description, String mime) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DISPLAY_NAME, name);
        values.put(MediaStore.Video.Media.MIME_TYPE, mime);
        values.put(MediaStore.Video.Media.DESCRIPTION, description);
        if (Build.VERSION.SDK_INT >= 29) {
            values.put("relative_path","Movies/VideoProcessor");
        }
        Uri url = null;
        String stringUri = null;
        ContentResolver cr = context.getContentResolver();
        try {
            url = cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            if (url == null) {
                return null;
            }
            byte[] buffer = new byte[1024];
            ParcelFileDescriptor descriptor = cr.openFileDescriptor(url, "w");
            FileOutputStream outputStream = new FileOutputStream(descriptor.getFileDescriptor());
            InputStream inputStream = new FileInputStream(videoPath);
            while (true) {
                int readSize = inputStream.read(buffer);
                if (readSize == -1) {
                    break;
                }
                outputStream.write(buffer, 0, readSize);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            if (url != null) {
                cr.delete(url, null, null);
            }
        }
        if (url != null) {
            stringUri = url.toString();
        }
        return stringUri;
    }
}
