package com.dylan.library.media.camera;

import android.app.Activity;

/**
 * Author: Dylan
 * Date: 2021/1/20
 * Desc:
 */
public interface CameraRenderStatusListener {
    Activity getActivity();

    int onDrawFrame(byte[] cameraNv21Byte, int cameraTexId, int cameraWidth, int cameraHeight, float[] mvpMatrix, float[] texMatrix, long timeStamp);

    void onCameraChanged(int cameraFacing, int cameraOrientation);

    void onSurfaceCreated();

    void onSurfaceChanged(int viewWidth, int viewHeight);

    void onSurfaceDestroy();
}
