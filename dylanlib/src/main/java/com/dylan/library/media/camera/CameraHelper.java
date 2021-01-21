package com.dylan.library.media.camera;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;


import java.util.List;
import java.util.Map;

/**
 * Author: Dylan
 * Date: 2021/1/20
 * Desc:
 */
public class CameraHelper implements Camera.PreviewCallback{
    private static final String TAG =CameraHelper.class.getSimpleName() ;
    private byte[][] mPreviewCallbackBufferArray;
    private Camera mCamera;
    private int mFrontCameraId;
    private int mBackCameraId;
    public static final int FACE_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    public static final int FACE_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
    protected int mCameraFacing = FACE_FRONT;
    protected int mCameraWidth = 1280;
    protected int mCameraHeight = 720;
    public static final int PREVIEW_BUFFER_SIZE = 3;


    //摄像头
    // 曝光补偿，进度 0.5 表示实际值为 0 就是无补偿
    private static final float EXPOSURE_COMPENSATION = 0.5F;
    private float mExposureCompensation = EXPOSURE_COMPENSATION;
    public static final int FRONT_CAMERA_ORIENTATION = 270;
    public static final int BACK_CAMERA_ORIENTATION = 90;
    protected int mBackCameraOrientation = BACK_CAMERA_ORIENTATION;
    protected int mFrontCameraOrientation = FRONT_CAMERA_ORIENTATION;
    protected int mCameraOrientation = FRONT_CAMERA_ORIENTATION;
    private PreViewFrameCallBack mPreViewFrameCallBack;

    public CameraHelper(PreViewFrameCallBack callBack){
        mPreViewFrameCallBack=callBack;
        initCameraInfo();
    }

    private void initCameraInfo() {
        int number = Camera.getNumberOfCameras();
        if (number <= 0) {
            throw new RuntimeException("No camera");
        }

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < number; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mFrontCameraId = i;
                mFrontCameraOrientation = cameraInfo.orientation;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mBackCameraId = i;
                mBackCameraOrientation = cameraInfo.orientation;
            }
        }

        mCameraOrientation = mCameraFacing == FACE_FRONT ? mFrontCameraOrientation : mBackCameraOrientation;
    }


    //打开摄像头
    public boolean openCamera(int cameraFacing, Activity activity) {
        try {
            if (mCamera != null) {
                return true;
            }
            boolean isFront = cameraFacing == FACE_FRONT;
            int cameraId = isFront ? mFrontCameraId : mBackCameraId;
            Camera camera = Camera.open(cameraId);
            if (camera == null) {
                throw new RuntimeException("No camera");
            }
            mExposureCompensation = EXPOSURE_COMPENSATION;
            CameraUtils.setCameraDisplayOrientation(activity, cameraId, camera);
            Camera.Parameters parameters = camera.getParameters();
            CameraUtils.setFocusModes(parameters);
            CameraUtils.chooseFrameRate(parameters);
            int[] size = CameraUtils.choosePreviewSize(parameters, mCameraWidth, mCameraHeight);
            mCameraWidth = size[0];
            mCameraHeight = size[1];
            parameters.setPreviewFormat(ImageFormat.NV21);
            CameraUtils.setParameters(camera, parameters);
            mCamera = camera;

            List<Camera.Size> size1 = parameters.getSupportedPictureSizes();

            // log camera all parameters
            if (CameraUtils.DEBUG) {
                Map<String, String> fullCameraParameters = CameraUtils.getFullCameraParameters(mCamera);
                String fullParams = fullCameraParameters.toString();
                // log message is too long, so split it.
                if (fullParams.length() > 1000) {
                    int trunk = fullParams.length() / 1000 + 1;
                    for (int i = 0; i < trunk; i++) {
                        int end = i == trunk - 1 ? fullParams.length() : (i + 1) * 1000;
                        String substring = fullParams.substring(i * 1000, end);
                        Log.v(TAG, "AFTER SET camera parameters: " + substring);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public void closeCamera(){
        Log.d(TAG, "closeCamera. camera:" + mCamera);
        try {
            Camera camera = mCamera;
            if (camera != null) {
                camera.stopPreview();
                camera.setPreviewTexture(null);
                camera.setPreviewCallbackWithBuffer(null);
                camera.release();
                mCamera = null;
                mPreviewCallbackBufferArray=null;
            }
        } catch (Exception e) {
            Log.e(TAG, "releaseCamera: ", e);
            mCamera = null;
        }
    }




    public void switchCamera(Activity activity,SurfaceTexture surfaceTexture) {
        boolean isFront = mCameraFacing == FACE_FRONT;
        mCameraFacing = isFront ? FACE_BACK : FACE_FRONT;
        mCameraOrientation = isFront ? mBackCameraOrientation : mFrontCameraOrientation;
        closeCamera();
        openCamera(mCameraFacing,activity);
        startPreview(surfaceTexture);
    }


    public void startPreview(SurfaceTexture surfaceTexture) {
        if (mCamera==null)return;
        Camera camera = mCamera;
        try {
            camera.stopPreview();
            if (mPreviewCallbackBufferArray == null) {
                mPreviewCallbackBufferArray = new byte[PREVIEW_BUFFER_SIZE][mCameraWidth * mCameraHeight
                        * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8];
            }
            camera.setPreviewCallbackWithBuffer(this);
            for (byte[] bytes : mPreviewCallbackBufferArray) {
                camera.addCallbackBuffer(bytes);
            }
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "cameraStartPreview: ", e);
        }
    }


    public Camera getCamera() {
        return mCamera;
    }

    public int getCameraFacing(){
        return mCameraFacing;
    }

    public int getCameraOrientation(){
        return mCameraOrientation;
    }

    public int getCameraWidth() {
        return mCameraWidth;
    }

    public void setCameraWidth(int cameraWidth) {
        this.mCameraWidth = cameraWidth;
    }

    public int getCameraHeight() {
        return mCameraHeight;
    }

    public void setCameraHeight(int cameraHeight) {
        this.mCameraHeight = cameraHeight;
    }

    public boolean isFrontCameraNow(){
        return mCameraFacing == FACE_FRONT;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
         if (mPreViewFrameCallBack!=null)mPreViewFrameCallBack.onPreviewFrame(data,camera);
    }


    public interface PreViewFrameCallBack{
        void onPreviewFrame(byte[] data, Camera camera);
    }
}
