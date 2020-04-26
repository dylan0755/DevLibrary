package com.dylan.library.contentObserver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

/**
 * Author: Dylan
 * Date: 2020/4/26
 * Desc:
 */
public class GpsLocatonObserver extends ContentObserver {
   private ContentResolver mContentResolver;
   private ContentObserverChangeListener mListener;

    public GpsLocatonObserver(Context context) {
        super(new Handler(Looper.getMainLooper()));
        mContentResolver=context.getContentResolver();
    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        if (mListener!=null)mListener.onChange(selfChange);
    }


    public void registerObserver(ContentObserverChangeListener listener) {
        mListener = listener;
        mContentResolver.registerContentObserver(Settings.System
                        .getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED), false,
                this);
    }

    public void unRegisterObserver() {
        mContentResolver.unregisterContentObserver(this);
        mContentResolver = null;
        mListener = null;
    }
}
