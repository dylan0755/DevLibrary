package com.dylan.library.media.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;

import com.dylan.library.opengl.CameraGLSurfaceView;
import com.dylan.library.opengl.GlUtils;
import com.dylan.library.opengl.Texture2dDrawer;
import com.dylan.library.opengl.TextureOESDrawer;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Author: Dylan
 * Date: 2021/1/20
 * Desc:
 */
public class CameraRender implements GLSurfaceView.Renderer {
    private static final String TAG = CameraRender.class.getSimpleName();
    private CameraHelper mCameraHelper;//相机

    //画布纹理
    private int cameraTextureId;
    private SurfaceTexture mSurfaceTexture;
    private float[] mMvpMatrix;
    private TextureOESDrawer mTextureOESDrawer;
    private static final float[] TEXTURE_MATRIX = {0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f};
    private float[] mTexMatrix = Arrays.copyOf(TEXTURE_MATRIX, TEXTURE_MATRIX.length);
    protected byte[] mCameraNv21Byte;  //Nv21 属于YUV420

    //开关
    protected boolean mIsPreviewing;
    protected volatile boolean mIsSwitchCamera;
    protected volatile boolean mIsStopPreview;
    protected boolean mIsOpenMirror;//是否开启镜像
    private CameraRenderStatusListener mRenderStatusListener;
    protected GLSurfaceView mGlSurfaceView;
    protected int mViewWidth;
    protected int mViewHeight;
    protected Handler mBackgroundHandler;

    public CameraRender(GLSurfaceView glSurfaceView, CameraRenderStatusListener renderStatusListener) {
        mGlSurfaceView = glSurfaceView;
        mRenderStatusListener = renderStatusListener;
        mCameraHelper = new CameraHelper(new CameraHelper.PreViewFrameCallBack() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {  //相机摄像头数据采集回调
                mCameraNv21Byte = data;
                camera.addCallbackBuffer(data);
                if (!mIsStopPreview) {
                    mGlSurfaceView.requestRender();
                }
            }
        });
    }


    //打开摄像头
    protected void openCamera(int cameraFacing, Activity activity) {
        boolean isSuccess = mCameraHelper.openCamera(cameraFacing, activity);
        if (isSuccess) {
            if (mViewWidth > 0 && mViewHeight > 0) {
                mMvpMatrix = GlUtils.changeMvpMatrixCrop(GlUtils.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHelper.getCameraHeight(), mCameraHelper.getCameraWidth());
                openMirrorIfNeed();
            }
            mRenderStatusListener.onCameraChanged(mCameraHelper.getCameraFacing(), mCameraHelper.getCameraOrientation());
        }


    }


    protected void closeCamera() {
        mCameraHelper.closeCamera();
        mIsPreviewing = false;
        mCameraNv21Byte = null;
    }


    protected void startPreview() {
        if (cameraTextureId <= 0 || mIsPreviewing) {
            return;
        }
        try {
            mCameraHelper.startPreview(mSurfaceTexture);
            mIsPreviewing = true;
        } catch (Exception e) {
            Log.e(TAG, "cameraStartPreview: ", e);
        }
    }


    //以下是底层OpenGl 的回调
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureOESDrawer = new TextureOESDrawer();
        //创建纹理对象并关联纹理ID，注意是OES 纹理格式,因为摄像头采集的图片数据是YUV 编码的，
        // 必须通过OES 采样器和着色器才能转化成RGB，如果用2D 着色器绘制，虽然不会报错，但是画面漆黑，不能正常渲染
        cameraTextureId = GlUtils.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(cameraTextureId);
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {//开启摄像头
                openCamera(mCameraHelper.getCameraFacing(), mRenderStatusListener.getActivity());
                startPreview();
            }
        });
        LimitFpsUtil.setTargetFps(LimitFpsUtil.DEFAULT_FPS);
        mRenderStatusListener.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mViewWidth != width || mViewHeight != height) {
            mMvpMatrix = GlUtils.changeMvpMatrixCrop(GlUtils.IDENTITY_MATRIX, width, height, mCameraHelper.getCameraHeight(), mCameraHelper.getCameraWidth());
            openMirrorIfNeed();
        }
        mViewWidth = width;
        mViewHeight = height;
        mRenderStatusListener.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mSurfaceTexture == null)return;
       GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
       //开启GL的混合模式，即图像叠加,不开启一边是sampe2D 一边是sampeOES,黑色渲染不出来
       GLES20.glEnable(GLES20.GL_BLEND);
       GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        try {
            //这两行代码一定要一起使用
            mSurfaceTexture.updateTexImage();//将最新的摄像头数据更新到OpenGl 纹理中
            // 这里如果用TEXTURE_MATRIX，而下面的代码用mTexMatrix   那么后置摄像头就是上下颠倒的
            mSurfaceTexture.getTransformMatrix(mTexMatrix);
        } catch (Exception e) {
            Log.e(TAG, "onDrawFrame: ", e);
        }


        if (!mIsSwitchCamera) {
            GLES20.glViewport(0,0,mViewWidth,mViewHeight);
            mTextureOESDrawer.drawFrame(cameraTextureId, mTexMatrix, mMvpMatrix);
        }

        if (!mIsStopPreview) {
            if (mCameraNv21Byte != null) {
               mRenderStatusListener.onDrawFrame(mCameraNv21Byte, cameraTextureId,
                        mCameraHelper.getCameraWidth(), mCameraHelper.getCameraHeight(), mMvpMatrix, mTexMatrix, mSurfaceTexture.getTimestamp());
            }
        }

        LimitFpsUtil.limitFrameRate();
        if (!mIsStopPreview) {
            mGlSurfaceView.requestRender();
        }

    }


    private void openMirrorIfNeed() {
        if (mIsOpenMirror && mCameraHelper.isFrontCameraNow()) {//当前为前置摄像头并且开启了镜像
            GlUtils.frontCameraMirror(mMvpMatrix);
        }else{
            Log.e(TAG, "openMirrorIfNeed: "+false );
        }
    }

    public void setSupportMirror(boolean bl) {
        mIsOpenMirror = bl;
    }


    public void onResume() {
        startBackgroundThread();
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                openCamera(mCameraHelper.getCameraFacing(), mRenderStatusListener.getActivity());
                startPreview();
            }
        });
        mGlSurfaceView.onResume();
    }

    public void onPause() {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                closeCamera();
            }
        });
        stopBackgroundThread();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                destroyGlSurface();
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // ignored
        }
        mGlSurfaceView.onPause();
    }


    //当改变SurfaceView的大小则调用此方法
    public void resizeGlSurfaceView(final int cameraWidth, final int cameraHeight) {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsStopPreview = true;
                mIsSwitchCamera = true;
                mCameraHelper.setCameraWidth(cameraWidth);
                mCameraHelper.setCameraHeight(cameraHeight);
                closeCamera();
                openCamera(mCameraHelper.getCameraFacing(), mRenderStatusListener.getActivity());
                startPreview();
                mIsSwitchCamera = false;
                mIsStopPreview = false;
            }
        });
    }

    //镜像开关
    public void toggleMirror() {
        mIsOpenMirror = !mIsOpenMirror;
        mIsStopPreview = true;
        mIsSwitchCamera = true;
        closeCamera();
        openCamera(mCameraHelper.getCameraFacing(), mRenderStatusListener.getActivity());
        startPreview();
        mIsSwitchCamera = false;
        mIsStopPreview = false;
    }


    public void switchCamera() {
        if (mBackgroundHandler == null) {
            return;
        }
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsStopPreview = true;
                mIsSwitchCamera = true;
                mCameraHelper.switchCamera(mRenderStatusListener.getActivity(),mSurfaceTexture);
                mIsSwitchCamera = false;
                mIsStopPreview = false;
            }
        });
    }

    //切换分辨率
    public void changeResolution(final int cameraWidth, final int cameraHeight) {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsStopPreview = true;
                mIsSwitchCamera = true;
                mCameraHelper.setCameraWidth(cameraWidth);
                mCameraHelper.setCameraHeight(cameraHeight);
                closeCamera();
                openCamera(mCameraHelper.getCameraFacing(), mRenderStatusListener.getActivity());
                startPreview();
                mIsSwitchCamera = false;
                mIsStopPreview = false;
            }
        });
    }


    public void handleFocus(float rawX, float rawY, int focusRectSize){
        mCameraHelper.handleFocusMetering(rawX, rawY, focusRectSize,mViewWidth,mViewHeight);
    }


    private void startBackgroundThread() {
        if (mBackgroundHandler == null) {
            HandlerThread backgroundThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
            backgroundThread.start();
            mBackgroundHandler = new Handler(backgroundThread.getLooper());
        } else {
            mBackgroundHandler.removeCallbacks(mQuitEvent);
        }
    }

    private void stopBackgroundThread() {
        if (mBackgroundHandler != null) {
            mBackgroundHandler.removeCallbacks(mQuitEvent);
            // 5s 后销毁相机线程，减少快速切换前后台带来的开销
            mBackgroundHandler.postDelayed(mQuitEvent, 5000);
        }
    }

    private final Runnable mQuitEvent = new Runnable() {
        @Override
        public void run() {
            mBackgroundHandler.getLooper().quitSafely();
            mBackgroundHandler = null;
        }
    };


    //销毁
    private void destroyGlSurface() {
        if (cameraTextureId != 0) {
            GLES20.glDeleteTextures(1, new int[]{cameraTextureId}, 0);
            cameraTextureId = 0;
        }

        if (mTextureOESDrawer != null) {
            mTextureOESDrawer.release();
            mTextureOESDrawer = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mRenderStatusListener.onSurfaceDestroy();
    }


    public int getViewWidth() {
        return mViewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.mViewWidth = viewWidth;
    }

    public int getViewHeight() {
        return mViewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.mViewHeight = viewHeight;
    }




    public CameraHelper getCameraHelper() {
        return mCameraHelper;
    }
}
