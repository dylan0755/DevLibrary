package android.app;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 需要系统签名
 */
public class ActivityController extends IActivityController.Stub {
    private static final String TAG = ActivityController.class.getSimpleName() + "======";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void monitor() {
        try {
            Class<?> cActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Method getServiceMethod = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", new Class[]{String.class});
            Object ServiceManager = getServiceMethod.invoke(null, new Object[]{"activity"});
            Method iBinder = cActivityManagerNative.getMethod("asInterface", IBinder.class);
            Object iAMS = iBinder.invoke(null, ServiceManager);
            Method setMethod = iAMS.getClass().getMethod("setActivityController", Class.forName("android.app.IActivityController"), boolean.class);
            setMethod.invoke(iAMS, new ActivityController(),true);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean activityStarting(Intent intent, String pkg) throws RemoteException {
        //拦截动作

        String targetClassName="";
        if (intent.getComponent()!=null){
            targetClassName=intent.getComponent().getClassName();
        }
        Log.d(TAG, " >>>>>>start, pkg =" + pkg+" targetClassName="+targetClassName);
        return true;

    }

    public boolean activityResuming(String pkg) throws RemoteException {

        //Log.d(TAG, ">>>>>>resuming, pkg =" + pkg);

        return false;

    }

    public boolean appCrashed(String processName, int pid, String shortMsg, String longMsg, long timeMillis, String stackTrace) throws RemoteException {

        return false;

    }

    public int appEarlyNotResponding(String processName, int pid, String annotation) throws RemoteException {

        return 0;

    }

    public int appNotResponding(String processName, int pid, String processStats) throws RemoteException {

        return 0;

    }

    public int systemNotResponding(String msg) throws RemoteException {

        return 0;

    }




}
