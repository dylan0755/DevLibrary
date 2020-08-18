package com.dylan.library.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.dylan.library.exception.ELog;

/**
 * Created by Dylan on 2017/4/1.
 */

public class RunTaskUtils {
    private static int activityCount;
    public static final int STATE_ON_NO_REGISTER = 0;//没有注册监听
    public static final int STATE_ON_FRONT = 1;//在前台
    public static final int STATE_ON_BACKROUND = 2;//在后台
    private static int currentState = STATE_ON_NO_REGISTER;


    public static void restore() {
        currentState = STATE_ON_NO_REGISTER;
        activityCount = 0;
    }

    /**
     * 是否在前台
     */
    public static void registerActivityLifeCallBack(Application application) {
        restore();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
                currentState = STATE_ON_FRONT;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                if (0 == activityCount) {
                    currentState = STATE_ON_BACKROUND;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }


    public static void registerActivityLifeCallBack(Application application, final RunningListner listner) {
        restore();
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                if (listner!=null)listner.onActivityCreated(activity,bundle);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                activityCount++;
                currentState = STATE_ON_FRONT;
                if (listner != null) listner.onForeground();
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activityCount--;
                if (0 == activityCount) {
                    currentState = STATE_ON_BACKROUND;
                    if (listner != null) listner.onBackground();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (listner!=null)listner.onActivityDestroyed(activity);
            }
        });
    }


    public static int getRunningState() {
        if (currentState == STATE_ON_NO_REGISTER) {
            try {
                throw new Exception("have no set registerLifeCallBack before getting run status");
            } catch (Exception e) {
                e.printStackTrace();
                ELog.e(e);
            }
        }
        return currentState;
    }


    public interface RunningListner {
        void onForeground();
        void onBackground();
        void onActivityCreated(Activity activity, Bundle savedInstanceState);
        void onActivityDestroyed(Activity activity);
    }
}
