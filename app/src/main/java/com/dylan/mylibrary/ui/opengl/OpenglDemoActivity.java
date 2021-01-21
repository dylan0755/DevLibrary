package com.dylan.mylibrary.ui.opengl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.io.FileUtils;
import com.dylan.library.media.MediaTools;
import com.dylan.library.media.camera.CameraRender;
import com.dylan.library.media.camera.CameraRenderStatusListener;
import com.dylan.library.media.encoder.MediaEncoder;
import com.dylan.library.media.encoder.MediaMuxerWrapper;
import com.dylan.library.media.encoder.MediaShareAudioEncoder;
import com.dylan.library.media.encoder.MediaStandardAudioEncoder;
import com.dylan.library.media.encoder.MediaVideoEncoder;
import com.dylan.library.opengl.GlUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.PermissionRequestBuilder;
import com.dylan.library.utils.PermissionUtils;
import com.dylan.library.utils.ToastUtils;
import com.dylan.library.utils.thread.ThreadHelper;
import com.dylan.mylibrary.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;


/**
 * Author: Dylan
 * Date: 2021/1/20 0020
 * Desc:
 */
public class OpenglDemoActivity extends AppCompatActivity {
    private GLSurfaceView mGlSurfaceView;
    private TextView tvTime;
    private CameraRender cameraRender;
    private final byte[] mRecordLock = new byte[1];
    private PermissionRequestBuilder builder;
    private File mVideoOutFile;
    private MediaMuxerWrapper muxerWrapper;
    private MediaVideoEncoder mVideoEncoder;
    private MediaStandardAudioEncoder standardAudioEncoder;
    private MediaShareAudioEncoder shareAudioEncoder;
    private boolean needOpenAudioRecord = true;
    private CountDownLatch mRecordBarrier;
    private CountDownTimer mCountDownTimer;
    private long millisInFuture = 20 * 3600 * 1000L;//20个小时
    private volatile long mStartTime = 0;
    protected volatile boolean mIsTakingPic = false;
    protected volatile boolean mIsNeedTakePic = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengldemo);
        tvTime = findViewById(R.id.tvTime);
        mGlSurfaceView = findViewById(R.id.glSurfaceView);
        mGlSurfaceView.setEGLContextClientVersion(2);
        cameraRender = new CameraRender(mGlSurfaceView, new RenderCallBack());
        mGlSurfaceView.setRenderer(cameraRender);//设置画笔
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//手动刷新
        builder = new PermissionRequestBuilder(this);
        builder.addPerm(Manifest.permission.CAMERA, true)
                .addPerm(Manifest.permission.WRITE_EXTERNAL_STORAGE, true)
                .addPerm(Manifest.permission.READ_EXTERNAL_STORAGE, true)
                .addPerm(Manifest.permission.RECORD_AUDIO, true)
                .startRequest(1);

        final ImageView ivVideoRecord = findViewById(R.id.ivVideoRecord);
        ivVideoRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();//点击拍照
//                if (!v.isSelected()) {
//                    startRecording();
//                    v.setSelected(true);
//                    ivVideoRecord.setImageResource(R.drawable.icon_videorecord_doing);
//                } else {
//                    stopRecording();
//                    v.setSelected(false);
//                    ivVideoRecord.setImageResource(R.drawable.icon_videorecord_normal);
//                }
            }
        });

        findViewById(R.id.tvSwitchCamera).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                cameraRender.switchCamera();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraRender.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraRender.onPause();
    }


    class RenderCallBack implements CameraRenderStatusListener {

        @Override
        public Activity getActivity() {
            return OpenglDemoActivity.this;
        }

        //如果有美颜的sdk 可以在这个方法里面接入
        @Override
        public int onDrawFrame(byte[] cameraNv21Byte, int cameraTexId, int cameraWidth, int cameraHeight, float[] mvpMatrix, float[] texMatrix, long timeStamp) {

            int newTextId = 0;//这里可以接入美颜sdk 处理完返回个纹理Id
            if (newTextId != 0) {
                sendRecordingData(newTextId, false, mvpMatrix, texMatrix, timeStamp);
                takePicture(newTextId, false, GlUtils.IDENTITY_MATRIX, texMatrix, cameraHeight, cameraWidth);
            } else {//原相机图片的纹理Id
                sendRecordingData(cameraTexId, true, mvpMatrix, texMatrix, timeStamp);
                takePicture(cameraTexId, true, GlUtils.IDENTITY_MATRIX, texMatrix, cameraHeight, cameraWidth);
            }
            return 0;
        }

        @Override
        public void onCameraChanged(int cameraFacing, int cameraOrientation) {

        }

        @Override
        public void onSurfaceCreated() {

        }

        @Override
        public void onSurfaceChanged(int viewWidth, int viewHeight) {

        }

        @Override
        public void onSurfaceDestroy() {

        }
    }


    /**
     * 发送录制数据给VideoEnCode
     * 中MediaCodec 的Surface里面去
     *
     * @param texId
     * @param texMatrix
     * @param timeStamp
     */
    protected void sendRecordingData(int texId, boolean isCameraTextureId, float[] mvpMatrix, float[] texMatrix, final long timeStamp) {
        synchronized (mRecordLock) {
            if (mVideoEncoder == null) {
                return;
            }

            mVideoEncoder.frameAvailableSoon(texId, isCameraTextureId, texMatrix, mvpMatrix);
            if (mStartTime == 0) {
                mStartTime = timeStamp;
            }
        }

    }

    /**
     * 开始录制
     */
    protected void startRecording() {
        try {
            mStartTime = 0;
            mRecordBarrier = new CountDownLatch(2);
            String videoFileName = "opengl" + System.currentTimeMillis() + ".mp4";
            mVideoOutFile = new File(Environment.getExternalStorageDirectory().toString(), videoFileName);
            muxerWrapper = new MediaMuxerWrapper(mVideoOutFile.getAbsolutePath());

            // for video capturing
//            int videoWidth = mCameraRenderer.getCameraHeight();
//            int videoHeight = mCameraRenderer.getCameraWidth();

            int videoWidth = cameraRender.getViewWidth();
            int videoHeight = cameraRender.getViewHeight();
            Logger.e("videoWidth=" + videoWidth + " videoHeight=" + videoHeight);
            new MediaVideoEncoder(muxerWrapper, mMediaEncoderListener, videoWidth, videoHeight);
            if (needOpenAudioRecord) {
                new MediaStandardAudioEncoder(muxerWrapper, mMediaEncoderListener);
            } else {
                new MediaShareAudioEncoder(muxerWrapper, mMediaEncoderListener);
            }
            muxerWrapper.prepare();
            muxerWrapper.startRecording();
            startTime();
        } catch (IOException e) {
            Logger.e("startCapture:", e);
        }


    }

    /**
     * 停止录制
     */
    protected void stopRecording() {
        Logger.d("stopRecording: ");
        if (muxerWrapper != null) {
            synchronized (mRecordLock) {
                mVideoEncoder = null;
            }
            muxerWrapper.stopRecording();
            muxerWrapper = null;
        }
        stopTime();
    }


    public void takePic() {
        if (mIsTakingPic) {
            return;
        }
        mIsNeedTakePic = true;
        mIsTakingPic = true;
    }

    /**
     * 拍照
     *
     * @param texId
     * @param texMatrix
     * @param texWidth
     * @param texHeight
     */
    protected void takePicture(int texId, boolean isOES, float[] mvpMatrix, float[] texMatrix, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        GlUtils.createBitmapFromTexture(texId, texMatrix, mvpMatrix, texWidth, texHeight, isOES, new GlUtils.OnReadBitmapListener() {
            @Override
            public void onReadBitmapListener(Bitmap var1) {
                try {
                    final String filePath=Environment.getExternalStorageDirectory().toString() + "/" + System.currentTimeMillis() + ".png";
                    BitmapHelper.saveBitmapSync(var1,filePath );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show("保存图片成功");
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath)));
                            sendBroadcast(intent);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mIsTakingPic = false;
            }
        });
    }

    /**
     * 录制封装回调
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        private long mStartRecordTime;

        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                mGlSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        MediaVideoEncoder videoEncoder = (MediaVideoEncoder) encoder;
                        videoEncoder.setEglContext(EGL14.eglGetCurrentContext());
                        synchronized (mRecordLock) {
                            mVideoEncoder = videoEncoder;
                        }
                    }
                });

            } else if (encoder instanceof MediaStandardAudioEncoder) {
                mGlSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mRecordLock) {
                            standardAudioEncoder = (MediaStandardAudioEncoder) encoder;
                        }
                    }
                });
            } else if (encoder instanceof MediaShareAudioEncoder) {
                mGlSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mRecordLock) {
                            shareAudioEncoder = (MediaShareAudioEncoder) encoder;
                        }
                    }
                });
            }
            mStartRecordTime = System.currentTimeMillis();
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            mRecordBarrier.countDown();
            if (mRecordBarrier.getCount() == 0) {
                Logger.e("onStopped: tid:" + Thread.currentThread().getId());
                if (System.currentTimeMillis() - mStartRecordTime <= 1000) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show("时间太短了");
                        }
                    });
                    return;
                }
                mStartRecordTime = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mVideoOutFile)));
                        ToastUtils.show("保存视频成功");
                    }
                });

            }
        }
    };


    private void startTime() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        tvTime.setVisibility(View.VISIBLE);
        tvTime.setText("00:00:00");
        mCountDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long currentMills = (millisInFuture - millisUntilFinished);
                String countDownText = MediaTools.getDurationHourFormat(currentMills);
                if (!isFinishing() && tvTime != null) {
                    tvTime.setText(countDownText);
                }
            }

            @Override
            public void onFinish() {

            }
        };
        mCountDownTimer.start();
    }


    private void stopTime() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            tvTime.setVisibility(View.GONE);
        }
    }


}
