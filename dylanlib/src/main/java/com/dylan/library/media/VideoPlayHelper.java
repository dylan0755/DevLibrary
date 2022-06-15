package com.dylan.library.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;


import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.io.FileUtils;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * DESC：MP4文件播放 回调 rgba 像素
 * Created on 2021/3/28
 */
public class VideoPlayHelper {
    private final static String TAG = "VideoPlayHelper";

    /**  加载本地文件保存初始大小 */
    private int requestPhotoWidth = 1080;
    private int requestPhotoHeight = 1920;

    public interface VideoDecoderListener {
        void onReadVideoPixel(byte[] bytes, int width, int height);
        void onReadImagePixel(byte[] bytes, int width, int height);
    }

    private VideoDecoder mVideoDecoder;
    private VideoDecoderListener mVideoDecoderListener;

    private Handler mPlayerHandler;


    public VideoPlayHelper(VideoDecoderListener listener, GLSurfaceView surfaceView) {
        this(listener,surfaceView,false,false);
    }

    public VideoPlayHelper(VideoDecoderListener listener, GLSurfaceView surfaceView, final boolean isLoop) {
        this(listener,surfaceView,isLoop,false);
    }

    public VideoPlayHelper(VideoDecoderListener listener, GLSurfaceView surfaceView, final boolean isLoop,final boolean isFrontCam) {
        mVideoDecoderListener = listener;
        startPlayerThread();
        mVideoDecoder = new VideoDecoder();
        mVideoDecoder.setLoop(isLoop);
        mVideoDecoder.setOnReadPixelListener(mOnReadPixelListener);
        surfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mVideoDecoder.create(EGL14.eglGetCurrentContext(), isFrontCam);
            }
        });
    }

    private VideoDecoder.OnReadPixelListener mOnReadPixelListener = new VideoDecoder.OnReadPixelListener() {

        @Override
        public void onReadVideoPixel(int width, int height, byte[] rgba) {
            if (mVideoDecoderListener != null)
                mVideoDecoderListener.onReadVideoPixel(rgba, width, height);
        }

        @Override
        public void onReadImagePixel(int width, int height, byte[] rgba) {
            if (mVideoDecoderListener != null)
                mVideoDecoderListener.onReadImagePixel(rgba, width, height);
        }
    };

    /**
     * 播放
     *
     * @param path
     */
    public void playVideo(final String path) {
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                if (path == null && mVideoDecoder != null) {
                    mVideoDecoder.stop();
                    return;
                }
                if (FileUtils.isImageFileType(path)) {
                    Bitmap bitmap = BitmapHelper.loadBitmapFromExternal(path, requestPhotoWidth, requestPhotoHeight);
                    if (bitmap == null) {
                        Log.e(TAG, "图片加载异常。");
                        return;
                    }
                    int orientation = BitmapHelper.readBitmapDegree(path);
                    bitmap = VideoPlayHelper.this.rotateBitmap(bitmap, orientation);
                    byte[] rgbBytes = new byte[bitmap.getByteCount()];
                    ByteBuffer rgbaBuffer = ByteBuffer.wrap(rgbBytes);
                    bitmap.copyPixelsToBuffer(rgbaBuffer);
                    bitmap.recycle();
//                mVideoDecoder.stop();
                    mVideoDecoderListener.onReadImagePixel(rgbBytes, bitmap.getWidth(), bitmap.getHeight());
                } else if (FileUtils.isVideoFileType(path)) {
                    mVideoDecoder.stop();
                    mVideoDecoder.start(path);
                }
            }
        });
    }



    /**
     * 暂停播放
     */
    public void pausePlay() {
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                mVideoDecoder.stop();
            }
        });
    }




    /**
     * 资源释放
     */
    public void release() {
        mPlayerHandler.removeCallbacksAndMessages(null);
        mVideoDecoderListener = null;
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                mVideoDecoder.release();
                mVideoDecoder = null;
            }
        });
        stopPlayerThread();
    }

    public void setFlip(final boolean isFlip) {
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                mVideoDecoder.setFrontCam(isFlip);
            }
        });
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    /**
     * 启动播放线程
     */
    private void startPlayerThread() {
        HandlerThread playerThread = new HandlerThread("video_decoder");
        playerThread.start();
        mPlayerHandler = new Handler(playerThread.getLooper());
    }

    /**
     * 关闭播放线程
     */
    private void stopPlayerThread() {
        mPlayerHandler.getLooper().quitSafely();
        mPlayerHandler = null;
    }

}
