package com.dylan.library.utils.thread;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Field;

public class ThreadUtils {

	public static boolean isMainThread(){
		boolean flag=Looper.getMainLooper().getThread()==Thread.currentThread()?true:false;
		return flag;
	}

	public static  void runOnMainThread(Runnable action){
		try {
			Class<?> clazz = Class.forName("android.app.ActivityThread");
			// 其实这货就是ActivityThread.mH这个贼重要的Handler(用于处理各种Client端的组件与系统服务端进行消息交互等等)
			Field field = clazz.getDeclaredField("sMainThreadHandler");
			field.setAccessible(true);
			Handler sMainThreadHandler = (Handler) field.get(null);
			sMainThreadHandler.post(action);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static  void runOnMainThreadDelay(Runnable action,long delayMillis){
		try {
			Class<?> clazz = Class.forName("android.app.ActivityThread");
			// 其实这货就是ActivityThread.mH这个贼重要的Handler(用于处理各种Client端的组件与系统服务端进行消息交互等等)
			Field field = clazz.getDeclaredField("sMainThreadHandler");
			field.setAccessible(true);
			Handler sMainThreadHandler = (Handler) field.get(null);
			sMainThreadHandler.postDelayed(action,delayMillis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
