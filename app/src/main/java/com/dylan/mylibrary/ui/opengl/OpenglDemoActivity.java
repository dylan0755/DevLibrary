package com.dylan.mylibrary.ui.opengl;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.io.FileUtils;
import com.dylan.library.media.MediaTools;
import com.dylan.library.media.VideoPlayHelper;
import com.dylan.library.media.camera.CameraFocusView;
import com.dylan.library.media.camera.CameraRender;
import com.dylan.library.media.camera.CameraRenderStatusListener;
import com.dylan.library.media.encoder.MediaEncoder;
import com.dylan.library.media.encoder.MediaMuxerWrapper;
import com.dylan.library.media.encoder.MediaShareAudioEncoder;
import com.dylan.library.media.encoder.MediaStandardAudioEncoder;
import com.dylan.library.media.encoder.MediaVideoEncoder;
import com.dylan.library.opengl.CameraGLSurfaceView;
import com.dylan.library.opengl.GlUtils;
import com.dylan.library.opengl.WaterMarkHelper;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.IntentUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.PermissionRequestBuilder;
import com.dylan.library.utils.ToastUtils;
import com.dylan.mylibrary.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;


/**
 * Author: Dylan
 * Date: 2021/1/20
 * Desc:
 */
public class OpenglDemoActivity extends AppCompatActivity implements CameraGLSurfaceView.OnTouchFocusCallBack {
    private CameraGLSurfaceView mGlSurfaceView;
    private CameraFocusView cameraFocusView;
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
    private WaterMarkHelper markHelper;
    private VideoPlayHelper playHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opengldemo);
        markHelper = new WaterMarkHelper();
        tvTime = findViewById(R.id.tvTime);
        mGlSurfaceView = findViewById(R.id.glSurfaceView);
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setOnTouchFocusCallBack(this);
        cameraFocusView = findViewById(R.id.cameraFocusView);
        cameraFocusView.setImageResource(R.drawable.photograph_focus);
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
                //               takePic();//点击拍照
                if (!v.isSelected()) {
                    startRecording();
                    v.setSelected(true);
                    ivVideoRecord.setImageResource(R.drawable.icon_videorecord_doing);
                } else {
                    stopRecording();
                    v.setSelected(false);
                    ivVideoRecord.setImageResource(R.drawable.icon_videorecord_normal);
                }
            }
        });

        findViewById(R.id.tvSwitchCamera).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                cameraRender.switchCamera();
            }
        });

        findViewById(R.id.selectVideo).setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                startActivityForResult(IntentUtils.getChooseVideoIntent(), 100);
            }
        });
        playHelper = new VideoPlayHelper(mVideoDecoderListener, mGlSurfaceView,false);
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
        if (playHelper != null) playHelper.pausePlay();
    }

    @Override
    public void onTouchFocus(float rawX, float rawY, int focusRectSize) {
        cameraRender.handleFocus(rawX, rawY, focusRectSize);
        cameraFocusView.showCameraFocus(rawX, rawY);
    }


    class RenderCallBack implements CameraRenderStatusListener {

        private WaterMarkHelper.WaterDateBean waterDateBean;
        //画水印
        private WaterMarkHelper.WaterBean waterBean;

        @Override
        public Activity getActivity() {
            return OpenglDemoActivity.this;
        }

        //如果有美颜的sdk 可以在这个方法里面接入
        @Override
        public int onDrawFrame(byte[] cameraNv21Byte, int cameraTexId, int cameraWidth, int cameraHeight, float[] mvpMatrix, float[] texMatrix, long timeStamp) {


            markHelper.drawFrame(waterBean);
            sendRecordingData(cameraTexId, true, mvpMatrix,
                    texMatrix, timeStamp, waterBean, waterDateBean);
            takePicture(cameraTexId, true, GlUtils.IDENTITY_MATRIX, texMatrix, cameraHeight, cameraWidth);
            markHelper.drawDateTimeText(waterDateBean);

            return 0;
        }

        @Override
        public void onCameraChanged(int cameraFacing, int cameraOrientation) {
        }

        @Override
        public void onSurfaceCreated() {
            Bitmap bitmap = BitmapHelper.getBitmapFromText("驰@水印测试", Color.RED, DensityUtils.dp2px(OpenglDemoActivity.this, 22));
            //静态水印
            waterBean = new WaterMarkHelper.WaterBean();
            waterBean.setBitmap(bitmap);
            waterBean.setX(150);
            markHelper.initConfig();

            //日期水印
            waterDateBean = new WaterMarkHelper.WaterDateBean();
            waterDateBean.setColor(Color.WHITE);
            waterDateBean.setTextSize(DensityUtils.dp2px(OpenglDemoActivity.this, 26));
            waterDateBean.setX(100);

        }


        @Override
        public void onSurfaceChanged(int viewWidth, int viewHeight) {
            if (waterBean != null) waterBean.setY((int) (cameraRender.getViewHeight() * 0.85f));
            if (waterDateBean != null)
                waterDateBean.setY((int) (cameraRender.getViewHeight() * 0.75f));
        }

        @Override
        public void onSurfaceDestroy() {
            markHelper.releaseTextureId(waterBean);//记得调用释放
            waterBean = null;
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
    protected void sendRecordingData(int texId, boolean isCameraTextureId, float[] mvpMatrix,
                                     float[] texMatrix, final long timeStamp,
                                     WaterMarkHelper.WaterBean bean,
                                     WaterMarkHelper.WaterDateBean data) {
        synchronized (mRecordLock) {
            if (mVideoEncoder == null) {
                return;
            }

            mVideoEncoder.frameAvailableSoon(texId, isCameraTextureId, texMatrix, mvpMatrix, bean, data);
            if (mStartTime == 0) {
                mStartTime = timeStamp;
            }
        }

    }

    /**
     * 开始录制
     */
    protected void startRecording() {
        mStartTime = 0;
        mRecordBarrier = new CountDownLatch(2);
        String videoFileName = "opengl" + System.currentTimeMillis() + ".mp4";
        mVideoOutFile = new File(Environment.getExternalStorageDirectory().toString(), videoFileName);
//        try {
        try {
            muxerWrapper = new MediaMuxerWrapper(mVideoOutFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }


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
        try {
            muxerWrapper.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        muxerWrapper.startRecording();
            startTime();

//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
                    final String filePath = Environment.getExternalStorageDirectory().toString() + "/" + System.currentTimeMillis() + ".png";
                    BitmapHelper.saveBitmapSync(var1, filePath);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show("保存图片成功");
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath)));
                            sendBroadcast(intent);
                        }
                    });


                    mIsTakingPic = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

    private VideoPlayHelper.VideoDecoderListener mVideoDecoderListener = new VideoPlayHelper.VideoDecoderListener() {

        @Override
        public void onReadVideoPixel(final byte[] bytes, final int width, final int height) {

           // BitmapHelper.loadBitmapFromByteArray(bytes,width,height);

        }

        @Override
        public void onReadImagePixel(byte[] bytes, int width, int height) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (100 == requestCode && data != null && data.getData() != null) {
            File selectFile = FileUtils.getFileByUri( this,data.getData());
            if (selectFile == null) return;
            playHelper.playVideo(selectFile.getAbsolutePath());
        }
    }


    @Override
    public void onDestroy() {
        if (playHelper != null) playHelper.release();
        super.onDestroy();
    }


}
