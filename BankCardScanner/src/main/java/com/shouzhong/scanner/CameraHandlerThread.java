package com.shouzhong.scanner;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * 相机线程
 *
 */
class CameraHandlerThread extends HandlerThread {

    private BankCardScannerView mBankCardScannerView;

    public CameraHandlerThread(BankCardScannerView bankCardScannerView) {
        super("CameraHandlerThread");
        mBankCardScannerView = bankCardScannerView;
        start();
    }

    /**
     * 打开系统相机，并进行基本的初始化
     */
    public void startCamera(final int cameraId) {
        Handler localHandler = new Handler(getLooper());
        localHandler.post(new Runnable() {
            @Override
            public void run() {
                mBankCardScannerView.setCameraWrapper(CameraWrapper.getWrapper(CameraUtils.getCamera(cameraId), cameraId));
                Handler mainHandler = new Handler(Looper.getMainLooper());//切换到主线程
                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mBankCardScannerView.setOptimalPreviewSize();
                            mBankCardScannerView.setupCameraPreview();
                        } catch (Exception e) {}
                    }
                }, 50);
            }
        });
    }
}
