package com.dylan.library.media.encoder;

import android.graphics.Color;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.dylan.library.opengl.EglCore;
import com.dylan.library.opengl.TextureDrawer;
import com.dylan.library.opengl.Texture2dDrawer;
import com.dylan.library.opengl.WaterMarkHelper;
import com.dylan.library.opengl.WindowSurface;

public final class RenderHandler implements Runnable {
    private static final String TAG = RenderHandler.class.getSimpleName();
    private static final boolean DEBUG = false;

    private final Object mLock = new Object();
    private EGLContext mShardContext;
    private Surface mSurface;
    private int mTexId;
    private float[] mtx = new float[16];
    private float[] mvp = new float[16];

    private volatile boolean mRequestSetEglContext;
    private volatile boolean mRequestRelease;
    private volatile int mRequestDraw;

    private WindowSurface mInputWindowSurface;
    private EglCore mEglCore;
    private TextureDrawer mFullScreen;
    private WaterMarkHelper waterMarkHelper;
    private WaterMarkHelper.WaterDateBean dateBean;
    private WaterMarkHelper.WaterBean waterBean;
    private int videoWidth,videoHeight;

    public static RenderHandler createHandler(final String name) {
        if (DEBUG)
            Log.v(TAG, "createHandler:");
        final RenderHandler handler = new RenderHandler();
        synchronized (handler.mLock) {
            new Thread(handler, !TextUtils.isEmpty(name) ? name : TAG).start();
            try {
                handler.mLock.wait();
            } catch (final InterruptedException e) {
            }
        }
        return handler;
    }

    public final void setEglContext(final EGLContext sharedContext, final Surface surface, final int texId) {
        if (DEBUG)
            Log.i(TAG, "setEglContext:");
        synchronized (mLock) {
            if (mRequestRelease)
                return;
            mShardContext = sharedContext;
            mTexId = texId;
            mSurface = surface;
            mRequestSetEglContext = true;
            Matrix.setIdentityM(mtx, 0);
            Matrix.setIdentityM(mvp, 0);
            mLock.notifyAll();
            try {
                mLock.wait();
            } catch (final InterruptedException e) {
            }
        }
    }






    //有新数据进来了，结束线程等待，通知绘制图像
    public final void draw(final int texId, final float[] texMatrix, final float[] mvpMatrix,
                           int videoWidth, int videoHeight, WaterMarkHelper.WaterBean waterBean,
                           WaterMarkHelper.WaterDateBean data) {
        if (this.waterBean==null){
            //要重新创建个对象不能使用 GlSurfaceView显示中的水印对象，因为在不同的GL线程环境中，textId也是不一样的
            this.waterBean=new WaterMarkHelper.WaterBean();
            this.waterBean.setX(waterBean.getX());
            this.waterBean.setY(waterBean.getY());
            this.waterBean.setBitmap(waterBean.getBitmap());
        }

        this.dateBean =data;
        this.videoWidth=videoWidth;
        this.videoHeight=videoHeight;
        synchronized (mLock) {
            if (mRequestRelease)
                return;
            mTexId = texId;
            System.arraycopy(texMatrix, 0, mtx, 0, texMatrix.length);
            System.arraycopy(mvpMatrix, 0, mvp, 0, mvpMatrix.length);
            mRequestDraw++;
            mLock.notifyAll();
/*			try {
				mLock.wait();
			} catch (final InterruptedException e) {
			} */
        }
    }

    public boolean isValid() {
        synchronized (mLock) {
            return mSurface == null || ((Surface) mSurface).isValid();
        }
    }

    public final void release() {
        if (DEBUG)
            Log.i(TAG, "release:");
        synchronized (mLock) {
            if (mRequestRelease)
                return;
            mRequestRelease = true;
            mLock.notifyAll();
            try {
                mLock.wait();
            } catch (final InterruptedException e) {
            }
        }
    }

//********************************************************************************
//********************************************************************************

    @Override
    public final void run() {
        if (DEBUG)
            Log.i(TAG, "RenderHandler thread started:");
        synchronized (mLock) {
            mRequestSetEglContext = mRequestRelease = false;
            mRequestDraw = 0;
            mLock.notifyAll();
        }
        boolean localRequestDraw;
        for (; ; ) {
            synchronized (mLock) {
                if (mRequestRelease)
                    break;
                if (mRequestSetEglContext) {
                    mRequestSetEglContext = false;
                    internalPrepare();
                }
                localRequestDraw = mRequestDraw > 0;
                if (localRequestDraw) {
                    mRequestDraw--;
//                mLock.notifyAll();
                }
            }
            if (localRequestDraw) {//是否有数据进行绘制，有数据则绘制
                if ((mEglCore != null) && mTexId >= 0) {
                    mInputWindowSurface.makeCurrent();
                    // clear screen with yellow color so that you can see rendering rectangle
                    GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                    GLES20.glEnable(GLES20.GL_BLEND);
                    GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                    GLES20.glViewport(0,0,videoWidth,videoHeight);
                    mFullScreen.drawFrame(mTexId, mtx, mvp);

                    if (waterBean!=null&&waterBean.isValid()){
                        waterMarkHelper.drawFrame(waterBean);
                    }

                    if (dateBean !=null){
                        waterMarkHelper.drawDateTimeText(dateBean);
                    }
                    mInputWindowSurface.swapBuffers();
                }
            } else {//没数据则线程进行等待
                synchronized (mLock) {
                    try {
                        mLock.wait();
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
            }
        }
        synchronized (mLock) {
            mRequestRelease = true;
            internalRelease();
            mLock.notifyAll();
        }
        if (DEBUG)
            Log.i(TAG, "RenderHandler thread finished:");
    }

    private void internalPrepare() {
        if (DEBUG)
            Log.i(TAG, "internalPrepare:");
        internalRelease();
        mEglCore = new EglCore(mShardContext, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface = new WindowSurface(mEglCore, mSurface, true);
        mInputWindowSurface.makeCurrent();
        mFullScreen = new Texture2dDrawer();
        waterMarkHelper=new WaterMarkHelper();
        waterMarkHelper.initConfig();
        mSurface = null;
        mLock.notifyAll();
    }

    private void internalRelease() {
        if (DEBUG)
            Log.i(TAG, "internalRelease:");
        if (mInputWindowSurface != null) {
            mInputWindowSurface.release();
            mInputWindowSurface = null;
        }
        if (mFullScreen != null) {
            mFullScreen.release();
            mFullScreen = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
        if (waterMarkHelper!=null)waterMarkHelper.releaseTextureId(0);
    }

}
