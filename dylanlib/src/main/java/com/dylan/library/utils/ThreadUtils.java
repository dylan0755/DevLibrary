package com.dylan.library.utils;

import android.os.Looper;

public class ThreadUtils {

	public static boolean isMainThread(){
		boolean flag=Looper.getMainLooper().getThread()==Thread.currentThread()?true:false;
		return flag;
	}
}
