package com.dylan.library.contentObserver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;


/**
 * Author: Dylan
 * Date: 2020/2/20
 * Desc:
 */
public class RotationObserver extends ContentObserver {
    ContentResolver mResolver;
    private ContentObserverChangeListener mListener;

    public RotationObserver(Context context) {
        super(new Handler(Looper.getMainLooper()));
        mResolver = context.getContentResolver();
    }

    //屏幕旋转设置改变时调用
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        if (mListener != null) mListener.onChange(selfChange);

    }

    public void registerObserver(ContentObserverChangeListener listener) {
        mListener = listener;
        mResolver.registerContentObserver(Settings.System
                        .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                this);
    }

    public void unRegisterObserver() {
        mResolver.unregisterContentObserver(this);
        mResolver = null;
        mListener = null;
    }



}
