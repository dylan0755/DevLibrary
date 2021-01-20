package com.dylan.library.media.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * 不开启AudioRecord 进行录音，适用于录音和语音识别同时使用场景，需要从语音sdk 拿到语音byte[] 数据
 */
public class MediaShareAudioEncoder extends MediaEncoder {
    private static final String TAG = MediaShareAudioEncoder.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final String MIME_TYPE = "audio/mp4a-latm";
    public static final int DEFAULT_BIT_RATE = 64000; //128kb
    public static final int DEFAULT_SIMPLE_RATE = 16000; //百度语音的 采样率
    public static final int DEFAULT_CHANNEL_COUNTS = 1;
    public static final int DEFAULT_MAX_INPUT_SIZE = 16384; //16k
    private MediaFormat audioFormat;
    private AudioThread mAudioThread = null;
    private Vector<byte[]> voiceBytes = new Vector<>();

    public MediaShareAudioEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener) {
        super(muxer, listener);
    }

    @Override
    protected void prepare() throws IOException {
        if (DEBUG)
            Log.v(TAG, "prepare:");
        mTrackIndex = -1;
        mMuxerStarted = mIsEOS = false;
        // prepare MediaCodec for AAC encoding of audio data from inernal mic.
        final MediaCodecInfo audioCodecInfo = selectAudioCodec(MIME_TYPE);
        if (audioCodecInfo == null) {
            Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
            return;
        }
        audioFormat = createMediaFormat();
        mMediaCodec = createMediaCodec();
        mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();
        if (DEBUG)
            Log.i(TAG, "prepare finishing");
        if (mListener != null) {
            try {
                mListener.onPrepared(this);
            } catch (final Exception e) {
                Log.e(TAG, "prepare:", e);
            }
        }
    }


    public MediaFormat createMediaFormat() {
        MediaFormat mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC,
                DEFAULT_SIMPLE_RATE, DEFAULT_CHANNEL_COUNTS);
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, DEFAULT_BIT_RATE);
        mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, DEFAULT_MAX_INPUT_SIZE);
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, DEFAULT_SIMPLE_RATE); //44100
        return mediaFormat;
    }

    public MediaCodec createMediaCodec() throws IOException {
        MediaCodecList mediaCodecList = null;
        String name = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
            name = mediaCodecList.findEncoderForFormat(audioFormat);
            if (name != null) {
                try {
                    return MediaCodec.createByCodecName(name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            MediaCodec.createEncoderByType(MIME_TYPE);
        }
        return null;
    }


    @Override
    protected void startRecording() {
        super.startRecording();
        if (mAudioThread == null) {
            mAudioThread = new AudioThread();
            mAudioThread.start();
        }
    }

    @Override
    protected void release() {
        super.release();
        voiceBytes.clear();
        mAudioThread = null;
    }


    public synchronized void encode(byte[] data) {
        if (!mIsCapturing) return;
        if (data != null) {
            voiceBytes.add(data);
        }
    }

    /**
     * Thread to capture audio data from internal mic as uncompressed 16bit PCM data
     * and write them to the MediaCodec encoder
     */
    private class AudioThread extends Thread {
        @Override
        public void run() {
                while (mIsCapturing && !mRequestStop && !mIsEOS) {
                    if (!voiceBytes.isEmpty()) {
                        byte[] data = voiceBytes.remove(0);
                        if (listener != null) {
                            listener.onTime(getPTSUs());
                        }
                        ByteBuffer buf = ByteBuffer.allocate(data.length);
                        buf.put(data);
                        buf.flip();
                        encode(buf, data.length, getPTSUs());
                        frameAvailableSoon();
                    }
                }

        }
    }

    /**
     * select the first codec that match a specific MIME type
     *
     * @param mimeType
     * @return
     */
    private static MediaCodecInfo selectAudioCodec(final String mimeType) {
        if (DEBUG)
            Log.v(TAG, "selectAudioCodec:");

        MediaCodecInfo result = null;
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        LOOP:
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (DEBUG)
                    Log.i(TAG, "supportedType:" + codecInfo.getName() + ",MIME=" + type);
                if (type.equalsIgnoreCase(mimeType)) {
                    result = codecInfo;
                    break LOOP;
                }
            }
        }
        return result;
    }

}
