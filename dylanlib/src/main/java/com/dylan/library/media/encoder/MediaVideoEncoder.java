package com.dylan.library.media.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;

import com.dylan.library.opengl.GlUtils;
import com.dylan.library.opengl.ProgramTexture2d;

import java.io.IOException;

public class MediaVideoEncoder extends MediaEncoder {
    private static final String TAG = MediaVideoEncoder.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final String MIME_TYPE = "video/avc";
    // parameters for recording
    private static final int FRAME_RATE = 25;
    private static final int IFRAME_INTERVAL = 10;       // I-frames间隔时间
    private static final float BPP = 0.25f;

    /**
     * 视频宽、高
     */
    private final int mWidth;
    private final int mHeight;
    /**
     * 纹理绘制的起始位置（左下角为（0，0））
     */
    private int cropX;
    private int cropY;
    /**
     * 纹理的宽高
     */
    private int textureWidth, textureHeight;
    private RenderHandler mRenderHandler;
    private Surface mSurface;

    private ProgramTexture2d program;
    private int[] mFboTex;
    private int[] mFboId;
    private int[] mViewPort = new int[4];
    private int mFrameCount;

    public MediaVideoEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener, final int videoWidth, final int videoHeight) {
        this(muxer, listener, videoWidth, videoHeight, 0, 0, videoWidth, videoHeight);
    }

    public MediaVideoEncoder(final MediaMuxerWrapper muxer, final MediaEncoderListener listener, final int videoWidth, final int videoHeight,
                             int cropX, int cropY, int textureWidth, int textureHeight) {
        super(muxer, listener);
        if (DEBUG)
            Log.i(TAG, "MediaVideoEncoder: ");
        mWidth = videoWidth;
        mHeight = videoHeight;
        mRenderHandler = RenderHandler.createHandler(TAG);
        this.cropX = cropX;
        this.cropY = cropY;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }




    /**
     * select the first codec that match a specific MIME type
     *
     * @param mimeType
     * @return null if no codec matched
     */
    private static MediaCodecInfo selectVideoCodec(final String mimeType) {
        if (DEBUG)
            Log.v(TAG, "selectVideoCodec: " + mimeType);

        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            // select first codec that match a specific MIME type and color format
            final String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.equalsIgnoreCase(mimeType)) {
                    if (DEBUG)
                        Log.i(TAG, "codec:" + codecInfo.getName() + ",MIME=" + type);
                    final int format = selectColorFormat(codecInfo, mimeType);
                    if (format > 0) {
                        return codecInfo;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void prepare() throws IOException {
        if (DEBUG)
            Log.i(TAG, "prepare: ");
        mTrackIndex = -1;
        mMuxerStarted = mIsEOS = false;

        final MediaCodecInfo videoCodecInfo = selectVideoCodec(MIME_TYPE);
        if (videoCodecInfo == null) {
            Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
            return;
        }
        if (DEBUG)
            Log.i(TAG, "selected codec: " + videoCodecInfo.getName());

        final MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);    // API >= 18
        format.setInteger(MediaFormat.KEY_BIT_RATE, calcBitRate());
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        if (DEBUG)
            Log.i(TAG, "format: " + format);

        mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        // get Surface for encoder input
        // this method only can call between #configure and #start
        mSurface = mMediaCodec.createInputSurface();    // API >= 18
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


    public boolean frameAvailableSoon(int texId, float[] texMatrix, float[] mvpMatrix) {
        if (program == null) {
            return false;
        }
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, mViewPort, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId[0]);
        GLES20.glViewport(cropX, cropY, textureWidth, textureHeight);
        program.drawFrame(texId, texMatrix, mvpMatrix);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(mViewPort[0], mViewPort[1], mViewPort[2], mViewPort[3]);
        // 先绘制三次，不进行编码，解决黑屏问题
        if (mFrameCount++ < 3) {
            return true;
        }
        boolean result;
        //有新的数据进来了 让MediaCoder 结束线程等待，同时去绘制MediaCodec 中的Surface
        if (result = super.frameAvailableSoon()) {
            mRenderHandler.draw(mFboTex[0], GlUtils.IDENTITY_MATRIX, GlUtils.IDENTITY_MATRIX);
        }
        return result;
    }

    public void setEglContext(final EGLContext sharedContext) {
        mFboTex = new int[1];
        mFboId = new int[1];
        GlUtils.createFrameBuffers(mFboTex, mFboId, mWidth, mHeight);
        program = new ProgramTexture2d();
        mRenderHandler.setEglContext(sharedContext, mSurface, mFboTex[0]);
    }

    private int calcBitRate() {
        final int bitrate = (int) (BPP * FRAME_RATE * mWidth * mHeight);
        if (DEBUG) {
            Log.i(TAG, String.format("bitrate=%5.2f[Mbps]", bitrate / 1024f / 1024f));
        }
        return bitrate;
    }

    @Override
    protected void release() {
        if (DEBUG)
            Log.i(TAG, "release:");
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mRenderHandler != null) {
            mRenderHandler.release();
            mRenderHandler = null;
        }
        GlUtils.deleteFrameBuffers(mFboId);
        if (mFboId != null) {
            mFboId[0] = -1;
        }
        GlUtils.deleteTextures(mFboTex);
        if (mFboTex != null) {
            mFboTex[0] = -1;
        }
        if (program != null) {
            program.release();
            program = null;
        }
        mFrameCount = 0;
        super.release();
    }

    /**
     * select color format available on specific codec and we can use.
     *
     * @return 0 if no colorFormat is matched
     */
    private static int selectColorFormat(final MediaCodecInfo codecInfo, final String mimeType) {
        if (DEBUG)
            Log.i(TAG, "selectColorFormat: " + mimeType);
        int result = 0;
        final MediaCodecInfo.CodecCapabilities caps;
        try {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            caps = codecInfo.getCapabilitiesForType(mimeType);
        } finally {
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        }
        int colorFormat;
        for (int i = 0; i < caps.colorFormats.length; i++) {
            colorFormat = caps.colorFormats[i];
            if (isRecognizedViewoFormat(colorFormat)) {
                result = colorFormat;
                break;
            }
        }
        if (result == 0)
            Log.e(TAG, "couldn't find a good color format for " + codecInfo.getName() + " / " + mimeType);
        return result;
    }

    /**
     * color formats that we can use in this class
     */
    private static int[] recognizedFormats;

    static {
        recognizedFormats = new int[]{
//        	MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,
//        	MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,
//        	MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface,
        };
    }

    private static boolean isRecognizedViewoFormat(final int colorFormat) {
        if (DEBUG)
            Log.i(TAG, "isRecognizedViewoFormat:colorFormat=" + colorFormat);
        final int n = recognizedFormats != null ? recognizedFormats.length : 0;
        for (int i = 0; i < n; i++) {
            if (recognizedFormats[i] == colorFormat) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void signalEndOfInputStream() {
        if (DEBUG)
            Log.d(TAG, "sending EOS to encoder");
        mMediaCodec.signalEndOfInputStream();    // API >= 18
        mIsEOS = true;
    }

}
