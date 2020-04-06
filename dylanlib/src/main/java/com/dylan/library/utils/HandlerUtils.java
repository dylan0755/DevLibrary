package com.dylan.library.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Dylan on 2018/2/8.
 */

public class HandlerUtils {

    public static Handler getMainLooperHandler(){
          return new Handler(Looper.getMainLooper());
    }
}
