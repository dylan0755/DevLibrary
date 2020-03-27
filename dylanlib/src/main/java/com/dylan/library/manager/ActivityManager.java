package com.dylan.library.manager;

import android.app.Activity;
import android.content.Context;

import com.dylan.library.utils.Logger;

import java.util.Stack;


public class ActivityManager {

    // Activity栈
    private static Stack<Activity> activityStack;
    // 单例模式
    private static ActivityManager instance;

    private ActivityManager() {
    }

    /**
     * 单一实例
     */
    public static ActivityManager getInstance() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    public void removeActivity(Activity activity){
        if (activity != null) {
            activityStack.remove(activity);
        }
    }



    /**
     * 获取第一个压入栈的Activity即MainActivity
     */
    public Activity firstActivity() {
        Activity activity = activityStack.firstElement();
        return activity;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        Activity curDeleteActivity = null;
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                curDeleteActivity = activity;
            }
        }
        if (curDeleteActivity != null) {
            activityStack.remove(curDeleteActivity);
            finishActivity(curDeleteActivity);
        }
    }



    public Activity getActivity(Class<?> cls){
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }


    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }





    /**
     * 退出应用程序
     */
    public void toExit(Context context) {
        try {
            finishAllActivity();
            android.app.ActivityManager activityMgr = (android.app.ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            if (activityMgr!=null)activityMgr.killBackgroundProcesses(context.getPackageName());
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            },500);

        } catch (Exception e) {
        }
    }
}
