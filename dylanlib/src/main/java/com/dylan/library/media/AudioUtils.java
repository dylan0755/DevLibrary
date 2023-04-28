package com.dylan.library.media;

import android.content.Context;
import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Pair;


import com.dylan.library.media.jssrc.SSRC;
import com.dylan.library.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangwei on 2018/3/7 0007.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AudioUtils {
    private final static Map<Integer, Integer> freqIdxMap = new HashMap<Integer, Integer>();
    public final static int DEFAULT_AAC_BITRATE = 192 * 1000;

    static {
        freqIdxMap.put(96000, 0);
        freqIdxMap.put(88200, 1);
        freqIdxMap.put(64000, 2);
        freqIdxMap.put(48000, 3);
        freqIdxMap.put(44100, 4);
        freqIdxMap.put(32000, 5);
        freqIdxMap.put(24000, 6);
        freqIdxMap.put(22050, 7);
        freqIdxMap.put(16000, 8);
        freqIdxMap.put(12000, 9);
        freqIdxMap.put(11025, 10);
        freqIdxMap.put(8000, 11);
        freqIdxMap.put(7350, 12);
    }

    final static String TAG = "VideoProcessor";
    public static int VOLUMN_MAX_RATIO = 1;


    //调节PCM数据音量  multiple 4.0 表示4倍
    //pData原始音频byte数组，nLen原始音频byte数组长度，data2转换后新音频byte数组，nBitsPerSample采样率，multiple表示Math.pow()返回值
    public int amplifyPCMData(byte[] pData, int nLen, byte[] data2, int nBitsPerSample, float multiple) {
        int nCur = 0;
        if (16 == nBitsPerSample){
            while (nCur < nLen){
                short volum = getShort(pData, nCur);
                float pcmval = volum * multiple;

                //数据溢出处理
                if (pcmval < 32767 && pcmval > -32768){
                    volum = (short)pcmval;
                }else if (pcmval > 32767) {
                    volum = (short)32767;
                } else if (pcmval < -32768) {
                    volum = (short)-32768;
                }
                data2[nCur]   = (byte)( volum& 0xFF);
                data2[nCur + 1] = (byte) ((volum >> 8) & 0xFF);
                nCur += 2;
            }
        }
        return 0;
    }

    private short getShort(byte[] data, int start) {
        return (short) ((data[start] & 0xFF) | (data[start + 1] << 8));

    }

    /**
     * @param volume IntRange [0,100]
     */
    public static void adjustPcmVolume(String fromPath, String toPath, int volume) throws IOException {
        if (volume == 100) {
            copyFile(fromPath, toPath);
            return;
        }
        float vol = normalizeVolume(volume);

        byte[] buffer = new byte[2048];
        FileInputStream fileInputStream = new FileInputStream(fromPath);
        FileOutputStream fileOutputStream = new FileOutputStream(toPath);

        int tmp;
        try {
            while (fileInputStream.read(buffer) != -1) {
                for (int i = 0; i < buffer.length; i += 2) {
                    tmp = (short) ((buffer[i] & 0xff) | (buffer[i + 1] & 0xff) << 8);
                    tmp *= vol;
                    if (tmp > 32767) {
                        tmp = 32767;
                    } else if (tmp < -32768) {
                        tmp = -32768;
                    }
                    buffer[i] = (byte) (tmp & 0xFF);
                    buffer[i + 1] = (byte) ((tmp >>> 8) & 0xFF);
                }
                fileOutputStream.write(buffer);
            }
        } finally {
            fileInputStream.close();
            fileOutputStream.close();
        }
    }

    /**
     * 调整aac音量
     *
     * @param volume [0,100]
     * @throws IOException
     */
    public static void adjustAacVolume(Context context, MediaSource aacSource, String outPath, int volume
            , @Nullable VideoProgressListener listener) throws IOException {
        String name = "temp_aac_" + System.currentTimeMillis();
        File pcmFile = new File(VideoUtils.getVideoCacheDir(context), name + ".pcm");
        File pcmFile2 = new File(VideoUtils.getVideoCacheDir(context), name + "_2.pcm");
        File wavFile = new File(VideoUtils.getVideoCacheDir(context), name + ".wav");

        AudioUtils.extractPCM(aacSource, pcmFile.getAbsolutePath(), null, null);
        AudioUtils.adjustPcmVolume(pcmFile.getAbsolutePath(), pcmFile2.getAbsolutePath(), volume);
        MediaExtractor extractor = new MediaExtractor();
        aacSource.setDataSource(extractor);
        int trackIndex = VideoUtils.selectTrack(extractor, true);
        MediaFormat aacFormat = extractor.getTrackFormat(trackIndex);
        int sampleRate = aacFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int oriChannelCount = aacFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        if (oriChannelCount == 2) {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        }
        new PcmToWavUtils(sampleRate,  oriChannelCount).pcmToWav(pcmFile2.getAbsolutePath(), wavFile.getAbsolutePath());
        AudioUtils.encodeWAVToAAC(wavFile.getPath(), outPath, aacFormat, listener);
    }

    /**
     * @param volume [0,100]
     * @return 0~100 -> 0~1
     */
    private static float normalizeVolume(int volume) {
        return volume / 100f * VOLUMN_MAX_RATIO;
    }

    private static int findFormatFromChannels(int numChannels) {
        switch (numChannels) {
            case 1:
                return AudioFormat.CHANNEL_OUT_MONO;
            case 2:
                return AudioFormat.CHANNEL_OUT_STEREO;
            default:
                return -1; // Error
        }
    }


    public static void changeSound(String audioIn, String pcmOut,Sound sound) {
        FileOutputStream fileOutputStream = null;
        try {
            File fileIn = new File(audioIn);
            InputStream soundFile = new FileInputStream(fileIn);
            File desFile = new File(pcmOut);
            fileOutputStream = new FileOutputStream(desFile);
            MediaExtractor extractor = new MediaExtractor();
            extractor.setDataSource(fileIn.getPath());
            MediaFormat format = extractor.getTrackFormat(VideoUtils.selectTrack(extractor, true));
            int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            int channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            //Logger.e("sampleRate="+sampleRate+" channelCount="+channelCount);
            Sonic sonic = new Sonic(sampleRate, channelCount);
          //  int minSize = AudioTrack.getMinBufferSize(sampleRate, findFormatFromChannels(channelCount), AudioFormat.ENCODING_PCM_16BIT);
            byte[] samples = new byte[1024];
            byte[] modifiedSamples = new byte[1024];

            int bytesRead;

            sonic.setSpeed(sound.getSpeed());
            sonic.setPitch(sound.getPitch());
            sonic.setRate(sound.getRate());
            sonic.setVolume(sound.getVolume());
            do {
                bytesRead = soundFile.read(samples, 0, samples.length);
                if (bytesRead > 0) {
                    sonic.writeBytesToStream(samples, bytesRead);
                } else {
                    sonic.flushStream();
                }
                int available = sonic.readBytesFromStream(modifiedSamples, bytesRead);
                if (available > 0) {
                    if (modifiedSamples.length < available) {
                        modifiedSamples = new byte[available * 2];
                    }
                    fileOutputStream.write(modifiedSamples, 0, available);
                }
            } while (bytesRead > 0);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Logger.e(e);
            e.printStackTrace();
            if (fileOutputStream!=null) {
                try {
                    fileOutputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }


    /**
     * @param volume1 [0,100]
     * @param volume2 [0,100]
     * @throws IOException
     */
    public static void mixPcm(String pcm1Path, String pcm2Path, String toPath
            , int volume1, int volume2) throws IOException {
        float vol1 = normalizeVolume(volume1);
        float vol2 = normalizeVolume(volume2);

        byte[] buffer1 = new byte[2048];
        byte[] buffer2 = new byte[2048];
        byte[] buffer3 = new byte[2048];

        FileInputStream is1 = new FileInputStream(pcm1Path);
        FileInputStream is2 = new FileInputStream(pcm2Path);

        FileOutputStream fileOutputStream = new FileOutputStream(toPath);

        boolean end1 = false, end2 = false;
        short temp2, temp1;
        int temp;
        try {
            while (!end1 || !end2) {
                if (!end1) {
                    end1 = (is1.read(buffer1) == -1);
                    System.arraycopy(buffer1, 0, buffer3, 0, buffer1.length);
                }
                if (!end2) {
                    end2 = (is2.read(buffer2) == -1);
                    for (int i = 0; i < buffer2.length; i += 2) {
                        temp1 = (short) ((buffer1[i] & 0xff) | (buffer1[i + 1] & 0xff) << 8);
                        temp2 = (short) ((buffer2[i] & 0xff) | (buffer2[i + 1] & 0xff) << 8);
                        temp = (int) (temp2 * vol2 + temp1 * vol1);
                        if (temp > 32767) {
                            temp = 32767;
                        } else if (temp < -32768) {
                            temp = -32768;
                        }
                        buffer3[i] = (byte) (temp & 0xFF);
                        buffer3[i + 1] = (byte) ((temp >>> 8) & 0xFF);
                    }
                }
                fileOutputStream.write(buffer3);
            }
        } finally {
            is1.close();
            is2.close();
            fileOutputStream.close();
        }
    }

    public static void stereoToMono(String from, String to) throws IOException {
        stereoToMonoSimple(from, to, 2);
    }

    /**
     * 多声道转单声道,只取第一条声道
     *
     * @param from
     * @param to
     * @param srcChannelCount @IntRange(from = 2)
     * @throws IOException
     */
    public static void stereoToMonoSimple(String from, String to, int srcChannelCount) throws IOException {
        FileInputStream is = new FileInputStream(from);
        FileOutputStream os = new FileOutputStream(to);
        byte[] buffer1 = new byte[1024 * srcChannelCount];
        byte[] buffer2 = new byte[1024];

        while (is.read(buffer1) != -1) {
            for (int i = 0; i < buffer2.length; i += 2) {
                buffer2[i] = buffer1[srcChannelCount * i];
                buffer2[i + 1] = buffer1[srcChannelCount * i + 1];
            }
            os.write(buffer2);
        }
        is.close();
        os.close();
    }


    public static void copyFile(MediaSource from, String to) throws IOException {
        if (from.inputPath != null) {
            copyFile(from.inputPath, to);
            return;
        }
        ParcelFileDescriptor fileDescriptor = from.context.getContentResolver().openFileDescriptor(from.inputUri, "rw");
        FileChannel toChannel = new FileOutputStream(to).getChannel();

        FileChannel fromChannel = new FileInputStream(fileDescriptor.getFileDescriptor()).getChannel();
        fromChannel.transferTo(0, fromChannel.size(), toChannel);
    }

    public static void copyFile(String from, String to) throws IOException {
        FileChannel toChannel = new FileOutputStream(to).getChannel();
        FileChannel fromChannel = new FileInputStream(from).getChannel();
        fromChannel.transferTo(0, fromChannel.size(), toChannel);
    }

    public static boolean isStereo(String aacPath) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(aacPath);
        MediaFormat format = null;
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                break;
            }
        }
        extractor.release();
        if (format == null) {
            return false;
        }
        return format.getInteger(MediaFormat.KEY_CHANNEL_COUNT) > 1;
    }

    /**
     * 检查两段音频格式是否一致,不一致则统一转换为单声道,采样率转为较小的一个
     */
    public static Pair<Integer, Integer> checkAndAdjustAudioFormat(String pcm1, String pcm2, MediaFormat format1, MediaFormat format2) {
        final int DEFAULT_SAMPLE_RATE = 44100;
        int channelCount1 = format1.containsKey(MediaFormat.KEY_CHANNEL_COUNT) ? format1.getInteger(MediaFormat.KEY_CHANNEL_COUNT) : 1;
        int channelCount2 = format2.containsKey(MediaFormat.KEY_CHANNEL_COUNT) ? format2.getInteger(MediaFormat.KEY_CHANNEL_COUNT) : 1;
        int sampleRate1 = format1.containsKey(MediaFormat.KEY_SAMPLE_RATE) ? format1.getInteger(MediaFormat.KEY_SAMPLE_RATE) : DEFAULT_SAMPLE_RATE;
        int sampleRate2 = format2.containsKey(MediaFormat.KEY_SAMPLE_RATE) ? format2.getInteger(MediaFormat.KEY_SAMPLE_RATE) : DEFAULT_SAMPLE_RATE;

        if (channelCount1 == channelCount2 && sampleRate1 == sampleRate2 && channelCount1 <= 2) {
            return new Pair<>(channelCount1, sampleRate1);
        }
        File temp1 = new File(pcm1 + ".temp");
        File temp2 = new File(pcm2 + ".temp");
        int channelCount = channelCount1;
        int sampleRate = sampleRate1;
        //声道不一样，全部转换为单声道
        try {
            if (channelCount1 != channelCount2 || channelCount1 > 2 || channelCount2 > 2) {
                if (channelCount1 > 1) {
                    stereoToMonoSimple(pcm1, temp1.getAbsolutePath(), channelCount1);
                    File file = new File(pcm1);
                    file.delete();
                    temp1.renameTo(file);
                    channelCount1 = 1;
                }
                if (channelCount2 > 1) {
                    stereoToMonoSimple(pcm2, temp2.getAbsolutePath(), channelCount2);
                    File file = new File(pcm2);
                    file.delete();
                    temp2.renameTo(file);
                    channelCount2 = 1;
                }
                channelCount = 1;
            } else {
                channelCount = channelCount1;
            }
            if (sampleRate1 != sampleRate2) {
                sampleRate = Math.min(sampleRate1, sampleRate2);
                if (sampleRate1 != sampleRate) {
                    reSamplePcm(pcm1, temp1.getAbsolutePath(), sampleRate1, sampleRate, channelCount1);
                    File file = new File(pcm1);
                    file.delete();
                    temp1.renameTo(file);
                }
                if (sampleRate2 != sampleRate) {
                    reSamplePcm(pcm2, temp2.getAbsolutePath(), sampleRate2, sampleRate, channelCount2);
                    File file = new File(pcm2);
                    file.delete();
                    temp2.renameTo(file);
                }
            }
            return new Pair<>(channelCount, sampleRate);
        } catch (Exception e) {
            Logger.e(e);
            return new Pair<>(channelCount, sampleRate);
        } finally {
            temp1.delete();
            temp2.exists();
        }
    }

    public static File checkAndFillPcm(File aacPcmFile, int pcmDuration, int fileToDuration) {
        if (pcmDuration >= fileToDuration) {
            return aacPcmFile;
        }
        File cacheFile = new File(aacPcmFile.getAbsolutePath() + ".concat");
        FileInputStream is = null;
        FileOutputStream os = null;
        FileChannel from = null;
        FileChannel to = null;
        try {
            //计算填充次数
            float repeat = fileToDuration / (float) pcmDuration;
            int repeatInt = (int) repeat;
            //拼接repeatInt次
            is = new FileInputStream(aacPcmFile);
            os = new FileOutputStream(cacheFile);
            from = is.getChannel();
            to = os.getChannel();
            for (int i = 0; i < repeatInt; i++) {
                from.transferTo(0, from.size(), to);
                from.position(0);
            }
            //剩下的部分
            float remain = repeat - repeatInt;
            int remainSize = (int) (aacPcmFile.length() * remain);
            if (remainSize > 1024) {
                from.transferTo(0, remainSize, to);
            }
            from.close();
            to.close();
            aacPcmFile.delete();
            cacheFile.renameTo(aacPcmFile);
            return aacPcmFile;
        } catch (Exception e) {
            e.printStackTrace();
            cacheFile.delete();
            return aacPcmFile;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
                if (from != null) {
                    from.close();
                }
                if (to != null) {
                    to.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    /**
     * 需要改变音频速率的情况下，需要先解码->改变速率->编码
     */
    public static void extractPCM(MediaSource audioSource, String outPath, Integer startTimeUs, Integer endTimeUs) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        audioSource.setDataSource(extractor);
        int audioTrack = VideoUtils.selectTrack(extractor, true);
        extractor.selectTrack(audioTrack);
        if (startTimeUs == null) {
            startTimeUs = 0;
        }
        extractor.seekTo(startTimeUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        MediaFormat oriAudioFormat = extractor.getTrackFormat(audioTrack);
        int maxBufferSize;
        if (oriAudioFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            maxBufferSize = oriAudioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
        } else {
            maxBufferSize = 100 * 1000;
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        //调整音频速率需要重解码音频帧
        MediaCodec decoder = MediaCodec.createDecoderByType(oriAudioFormat.getString(MediaFormat.KEY_MIME));
        decoder.configure(oriAudioFormat, null, null, 0);
        decoder.start();

        boolean decodeDone = false;
        boolean decodeInputDone = false;
        final int TIMEOUT_US = 2500;
        File pcmFile = new File(outPath);
        FileChannel writeChannel = new FileOutputStream(pcmFile).getChannel();
        try {
            while (!decodeDone) {
                if (!decodeInputDone) {
                    boolean eof = false;
                    int decodeInputIndex = decoder.dequeueInputBuffer(TIMEOUT_US);
                    if (decodeInputIndex >= 0) {
                        long sampleTimeUs = extractor.getSampleTime();
                        if (sampleTimeUs == -1) {
                            eof = true;
                        } else if (sampleTimeUs < startTimeUs) {
                            extractor.advance();
                            continue;
                        } else if (endTimeUs != null && sampleTimeUs > endTimeUs) {
                            eof = true;
                        }

                        if (eof) {
                            decodeInputDone = true;
                            decoder.queueInputBuffer(decodeInputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        } else {
                            info.size = extractor.readSampleData(buffer, 0);
                            info.presentationTimeUs = sampleTimeUs;
                            info.flags = extractor.getSampleFlags();
                            ByteBuffer inputBuffer = decoder.getInputBuffer(decodeInputIndex);
                            inputBuffer.put(buffer);
                            Logger.i(TAG, "audio decode queueInputBuffer " + info.presentationTimeUs / 1000);
                            decoder.queueInputBuffer(decodeInputIndex, 0, info.size, info.presentationTimeUs, info.flags);
                            extractor.advance();
                        }

                    }
                }

                while (!decodeDone) {
                    int outputBufferIndex = decoder.dequeueOutputBuffer(info, TIMEOUT_US);
                    if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        break;
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newFormat = decoder.getOutputFormat();
                        Logger.i(TAG, "audio decode newFormat = " + newFormat);
                    } else if (outputBufferIndex < 0) {
                        //ignore
                        //Logger.e(TAG, "unexpected result from audio decoder.dequeueOutputBuffer: " + outputBufferIndex);
                    } else {
                        if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                            decodeDone = true;
                        } else {
                            ByteBuffer decodeOutputBuffer = decoder.getOutputBuffer(outputBufferIndex);
                            Logger.i(TAG, "audio decode saveFrame " + info.presentationTimeUs / 1000);
                            writeChannel.write(decodeOutputBuffer);
                        }
                        decoder.releaseOutputBuffer(outputBufferIndex, false);
                    }
                }
            }
        } finally {
            writeChannel.close();
            extractor.release();
            decoder.stop();
            decoder.release();
        }
    }

    public static void extractWav(Context context,MediaSource audioSource, String outPath, Integer startTimeUs, Integer endTimeUs)throws IOException{
        File cacheDir = new File(context.getCacheDir(), "pcmTemp101");
        cacheDir.mkdirs();
        final File videoPcmFile = new File(cacheDir, "video_" + System.currentTimeMillis() + ".pcm");
        MediaExtractor oriExtrator = new MediaExtractor();
        oriExtrator.setDataSource(audioSource.inputPath);
        int oriAudioIndex = VideoUtils.selectTrack(oriExtrator, true);
        if (oriAudioIndex < 0) {
            Logger.e("no audio stream!");
            oriExtrator.release();
            return;
        }
        MediaFormat audioTrackFormat = oriExtrator.getTrackFormat(oriAudioIndex);
        final int sampleRate = audioTrackFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int channelCount = audioTrackFormat.containsKey(MediaFormat.KEY_CHANNEL_COUNT) ? audioTrackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT) : 1;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        if (channelCount == 2) {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        }
        extractPCM(audioSource,videoPcmFile.getAbsolutePath(),startTimeUs,endTimeUs);
        new PcmToWavUtils(sampleRate,channelCount, AudioFormat.ENCODING_PCM_16BIT).pcmToWav(videoPcmFile.getAbsolutePath(),outPath );
        oriExtrator.release();
        videoPcmFile.delete();
    }
    /**
     * 将WAV音频编码成Aac
     *
     * @param wavPath
     * @param outPath
     * @param aacFormat 待编码成的AAC格式，需包含{@link MediaFormat#KEY_SAMPLE_RATE}
     *                  ,{@link MediaFormat#KEY_CHANNEL_COUNT},{@link MediaFormat#KEY_BIT_RATE},
     *                  {@link MediaFormat#KEY_MAX_INPUT_SIZE}，前两个必须
     * @param listener
     * @throws IOException
     */
    public static void encodeWAVToAAC(String wavPath, String outPath, MediaFormat aacFormat, @Nullable VideoProgressListener listener) throws IOException {
        int sampleRate = aacFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int channelCount = aacFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        int bitrate = getAudioBitrate(aacFormat);
        int maxBufferSize = getAudioMaxBufferSize(aacFormat);

        MediaCodec encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        MediaFormat encodeFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);//参数对应-> mime type、采样率、声道数
        encodeFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);//比特率
        encodeFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        encodeFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxBufferSize);
        encoder.configure(encodeFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        encoder.start();

        MediaExtractor wavExtrator = new MediaExtractor();
        wavExtrator.setDataSource(wavPath);
        int audioTrackIndex = VideoUtils.selectTrack(wavExtrator, true);
        wavExtrator.selectTrack(audioTrackIndex);

        MediaFormat pcmTrackFormat = wavExtrator.getTrackFormat(audioTrackIndex);
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        maxBufferSize = getAudioMaxBufferSize(pcmTrackFormat);
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
        long durationUs = pcmTrackFormat.getLong(MediaFormat.KEY_DURATION);
        boolean encodeInputDone = false;
        long lastAudioFrameTimeUs = -1;
        final int TIMEOUT_US = 2500;
        final int AAC_FRAME_TIME_US = 1024 * 1000 * 1000 / sampleRate;
        boolean detectTimeError = false;
        MediaMuxer mediaMuxer = new MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        int muxerTrackIndex = mediaMuxer.addTrack(aacFormat);
        mediaMuxer.start();
        try {
            boolean encodeDone = false;
            while (!encodeDone) {
                int inputBufferIndex = encoder.dequeueInputBuffer(TIMEOUT_US);
                if (!encodeInputDone && inputBufferIndex >= 0) {
                    long sampleTime = wavExtrator.getSampleTime();
                    if (sampleTime < 0) {
                        encodeInputDone = true;
                        encoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        int flags = wavExtrator.getSampleFlags();
                        buffer.clear();
                        int size = wavExtrator.readSampleData(buffer, 0);
                        ByteBuffer inputBuffer = encoder.getInputBuffer(inputBufferIndex);
                        inputBuffer.clear();
                        inputBuffer.put(buffer);
                        inputBuffer.position(0);
                        Logger.i("audio queuePcmBuffer " + sampleTime / 1000 + " size:" + size);
                        encoder.queueInputBuffer(inputBufferIndex, 0, size, sampleTime, flags);
                        wavExtrator.advance();
                    }
                }

                while (true) {
                    int outputBufferIndex = encoder.dequeueOutputBuffer(info, TIMEOUT_US);
                    if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        break;
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        MediaFormat newFormat = encoder.getOutputFormat();
                        Logger.i("audio decode newFormat = " + newFormat);
                    } else if (outputBufferIndex < 0) {
                        //ignore
                        Logger.e("unexpected result from audio decoder.dequeueOutputBuffer: " + outputBufferIndex);
                    } else {
                        if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                            encodeDone = true;
                            break;
                        }
                        ByteBuffer encodeOutputBuffer = encoder.getOutputBuffer(outputBufferIndex);
                        Logger.i("audio writeSampleData " + info.presentationTimeUs + " size:" + info.size + " flags:" + info.flags);
                        if (!detectTimeError && lastAudioFrameTimeUs != -1 && info.presentationTimeUs < lastAudioFrameTimeUs + AAC_FRAME_TIME_US) {
                            //某些情况下帧时间会出错，目前未找到原因（系统相机录得双声道视频正常，我录的单声道视频不正常）
                            Logger.e("audio 时间戳错误，lastAudioFrameTimeUs:" + lastAudioFrameTimeUs + " " +
                                    "info.presentationTimeUs:" + info.presentationTimeUs);
                            detectTimeError = true;
                        }
                        if (detectTimeError) {
                            info.presentationTimeUs = lastAudioFrameTimeUs + AAC_FRAME_TIME_US;
                            Logger.e("audio 时间戳错误，使用修正的时间戳:" + info.presentationTimeUs);
                            detectTimeError = false;
                        }
                        if (info.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                            lastAudioFrameTimeUs = info.presentationTimeUs;
                        }
                        mediaMuxer.writeSampleData(muxerTrackIndex, encodeOutputBuffer, info);
                        if (listener != null) {
                            float encodeProgress = (info.presentationTimeUs - 0) / (float) durationUs;
                            encodeProgress = encodeProgress < 0 ? 0 : encodeProgress;
                            encodeProgress = encodeProgress > 1 ? 1 : encodeProgress;
                            listener.onProgress(0.5f + encodeProgress * 0.5f);
                        }

                        encodeOutputBuffer.clear();
                        encoder.releaseOutputBuffer(outputBufferIndex, false);
                    }
                }
            }
        } finally {
            wavExtrator.release();
            encoder.release();
            mediaMuxer.release();
        }
    }

    public static boolean reSamplePcm(String srcPath, String dstPath, int srcSampleRate, int dstSampleRate, int srcChannelCount) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcPath);
            fos = new FileOutputStream(dstPath);
            new SSRC(fis, fos, srcSampleRate, dstSampleRate, 2, 2, srcChannelCount, (int) new File(srcPath).length(), 0, 0, true);
        } catch (IOException e) {
            Logger.e(e);
            e.printStackTrace();
        }

        return true;
    }

    public static void reversePcm(String srcPath, String dstPath) throws IOException {
        final int bit = 16;
        RandomAccessFile srcFile = null;
        FileOutputStream fos = null;
        try {
            srcFile = new RandomAccessFile(srcPath, "r");
            fos = new FileOutputStream(dstPath);
            int step = bit / 8;
            long len = srcFile.length();
            long offset = len - step;
            byte temp[] = new byte[step];
            while (offset >= 0) {
                srcFile.seek(offset);
                srcFile.read(temp);
                fos.write(temp);
                offset -= step;
            }
        } finally {
            if (srcFile != null) {
                srcFile.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    public static long writeAudioTrack(MediaExtractor extractor, MediaMuxer mediaMuxer, int muxerAudioTrackIndex,
                                       Integer startTimeUs, Integer endTimeUs, VideoProgressListener listener) throws IOException {
        return writeAudioTrack(extractor, mediaMuxer, muxerAudioTrackIndex, startTimeUs, endTimeUs, 0, listener);
    }

    /**
     * 不需要改变音频速率的情况下，直接读写就可
     */
    public static long writeAudioTrack(MediaExtractor extractor, MediaMuxer mediaMuxer, int muxerAudioTrackIndex,
                                       Integer startTimeUs, Integer endTimeUs, long baseMuxerFrameTimeUs, VideoProgressListener listener) throws IOException {
        int audioTrack = VideoUtils.selectTrack(extractor, true);
        extractor.selectTrack(audioTrack);
        if (startTimeUs == null) {
            startTimeUs = 0;
        }
        extractor.seekTo(startTimeUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        MediaFormat audioFormat = extractor.getTrackFormat(audioTrack);
        long durationUs = audioFormat.getLong(MediaFormat.KEY_DURATION);
        int maxBufferSize = audioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxBufferSize);
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        long lastFrametimeUs = baseMuxerFrameTimeUs;
        while (true) {
            long sampleTimeUs = extractor.getSampleTime();
            if (sampleTimeUs == -1) {
                break;
            }
            if (sampleTimeUs < startTimeUs) {
                extractor.advance();
                continue;
            }
            if (endTimeUs != null && sampleTimeUs > endTimeUs) {
                break;
            }
            if (listener != null) {
                float progress = (sampleTimeUs - startTimeUs) / (float) (endTimeUs == null ? durationUs : endTimeUs - startTimeUs);
                progress = progress < 0 ? 0 : progress;
                progress = progress > 1 ? 1 : progress;
                listener.onProgress(progress);
            }
            info.presentationTimeUs = sampleTimeUs - startTimeUs + baseMuxerFrameTimeUs;
            info.flags = extractor.getSampleFlags();
            info.size = extractor.readSampleData(buffer, 0);
            if (info.size < 0) {
                break;
            }
            Logger.i("writeAudioSampleData,time:" + info.presentationTimeUs / 1000f);
            mediaMuxer.writeSampleData(muxerAudioTrackIndex, buffer, info);
            lastFrametimeUs = info.presentationTimeUs;
            extractor.advance();
        }
        return lastFrametimeUs;
    }


    /**
     * 去掉视频的音轨
     */
    public static void removeAudioTrack(String videoPath, String outPath) throws IOException {
        MediaExtractor videoExtractor = new MediaExtractor();
        videoExtractor.setDataSource(videoPath);
        try {
            int videoTrack = VideoUtils.selectTrack(videoExtractor, false);

            videoExtractor.selectTrack(videoTrack);
            MediaFormat videoFormat = videoExtractor.getTrackFormat(videoTrack);

            MediaMuxer mediaMuxer = new MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int muxerVideoTrackIndex = mediaMuxer.addTrack(videoFormat);
            mediaMuxer.start();

            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            //写视频
            int maxBufferSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            ByteBuffer videoBuffer = ByteBuffer.allocateDirect(maxBufferSize);
            while (true) {
                long sampleTimeUs = videoExtractor.getSampleTime();
                if (sampleTimeUs == -1) {
                    break;
                }
                int flags = videoExtractor.getSampleFlags();
                int size = videoExtractor.readSampleData(videoBuffer, 0);
                info.presentationTimeUs = sampleTimeUs;
                info.flags = flags;
                info.size = size;
                mediaMuxer.writeSampleData(muxerVideoTrackIndex, videoBuffer, info);
                videoExtractor.advance();
            }
            mediaMuxer.stop();
            mediaMuxer.release();
        } finally {
            videoExtractor.release();
        }
    }

    /**
     * 替换视频的音轨
     *
     * @param repeat 音频不够长时是否重复填充
     */
    public static void replaceAudioTrack(String videoPath, String aacPath, String outPath, boolean repeat) throws IOException {
        MediaExtractor videoExtractor = new MediaExtractor();
        videoExtractor.setDataSource(videoPath);
        MediaExtractor aacExtractor = new MediaExtractor();
        aacExtractor.setDataSource(aacPath);
        try {
            int videoTrack = VideoUtils.selectTrack(videoExtractor, false);
            int audioTrack = VideoUtils.selectTrack(aacExtractor, true);

            videoExtractor.selectTrack(videoTrack);
            aacExtractor.selectTrack(audioTrack);
            MediaFormat audioFormat = aacExtractor.getTrackFormat(audioTrack);
            MediaFormat videoFormat = videoExtractor.getTrackFormat(videoTrack);

            MediaMuxer mediaMuxer = new MediaMuxer(outPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int muxerAudioTrackIndex = mediaMuxer.addTrack(audioFormat);
            int muxerVideoTrackIndex = mediaMuxer.addTrack(videoFormat);
            mediaMuxer.start();

            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            long lastVideoTimeUs = 0;
            //写视频
            int maxBufferSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            ByteBuffer videoBuffer = ByteBuffer.allocateDirect(maxBufferSize);
            while (true) {
                long sampleTimeUs = videoExtractor.getSampleTime();
                if (sampleTimeUs == -1) {
                    break;
                }
                int flags = videoExtractor.getSampleFlags();
                int size = videoExtractor.readSampleData(videoBuffer, 0);
                info.presentationTimeUs = sampleTimeUs;
                info.flags = flags;
                info.size = size;
                mediaMuxer.writeSampleData(muxerVideoTrackIndex, videoBuffer, info);
                lastVideoTimeUs = sampleTimeUs;
                videoExtractor.advance();
            }
            //写音频
            int sampleRate = audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            final int AAC_FRAME_TIME_US = 1024 * 1000 * 1000 / sampleRate;
            maxBufferSize = getAudioMaxBufferSize(audioFormat);
            ByteBuffer audioBuffer = ByteBuffer.allocateDirect(maxBufferSize);
            long lastAudioSampleTime = 0;
            long baseAudioSampleTime = 0;
            while (lastAudioSampleTime < lastVideoTimeUs) {
                aacExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                while (true) {
                    long sampleTimeUs = aacExtractor.getSampleTime();
                    if (sampleTimeUs == -1) {
                        break;
                    }
                    sampleTimeUs += baseAudioSampleTime;
                    if (sampleTimeUs > lastVideoTimeUs) {
                        lastAudioSampleTime = sampleTimeUs;
                        break;
                    }
                    int flags = aacExtractor.getSampleFlags();
                    int size = aacExtractor.readSampleData(audioBuffer, 0);
                    info.presentationTimeUs = sampleTimeUs;
                    lastAudioSampleTime = sampleTimeUs;
                    info.flags = flags;
                    info.size = size;
                    mediaMuxer.writeSampleData(muxerAudioTrackIndex, audioBuffer, info);
                    aacExtractor.advance();
                }
                baseAudioSampleTime = lastAudioSampleTime + AAC_FRAME_TIME_US;
                if (!repeat) {
                    break;
                }
            }
            mediaMuxer.stop();
            mediaMuxer.release();
        } finally {
            videoExtractor.release();
            aacExtractor.release();
        }
    }

    public static int getAudioMaxBufferSize(MediaFormat format) {
        if (format.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            return format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
        } else {
            return 100 * 1000;
        }
    }

    public static int getAudioBitrate(MediaFormat format) {
        if (format.containsKey(MediaFormat.KEY_BIT_RATE)) {
            return format.getInteger(MediaFormat.KEY_BIT_RATE);
        } else {
            return DEFAULT_AAC_BITRATE;
        }
    }

    public static void checkCsd(MediaFormat audioMediaFormat, int profile, int sampleRate, int channel) {
        int freqIdx = freqIdxMap.containsKey(sampleRate) ? freqIdxMap.get(sampleRate) : 4;
//        byte[] bytes = new byte[]{(byte) 0x11, (byte) 0x90};
//        ByteBuffer bb = ByteBuffer.wrap(bytes);
        ByteBuffer csd = ByteBuffer.allocate(2);
        csd.put(0, (byte) (profile << 3 | freqIdx >> 1));
        csd.put(1, (byte) ((freqIdx & 0x01) << 7 | channel << 3));
        audioMediaFormat.setByteBuffer("csd-0", csd);
    }


    /**
     *  生成静音
     * @param filePath
     * @param durationMs 毫秒
     */
    public static void makeSilenceWav(String filePath, Long durationMs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
           SilenceAudioUtils.makeSilenceWav(filePath,durationMs);
        }
    }
}
