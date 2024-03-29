package com.dylan.mylibrary;

import android.app.Activity;
import android.app.ActivityController;
import android.app.Application;
import android.app.IProcessObserver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDexApplication;

import com.dylan.library.manager.ActivityManager;
import com.dylan.library.utils.AndKit;
import com.dylan.library.utils.Logger;
import com.dylan.mylibrary.bean.QiNiuDomain;
import com.dylan.mylibrary.domain.RestApi;
import com.dylan.mylibrary.util.IResponBodyImpl;
import com.dylan.mylibrary.util.ResponseBodyParser;
import com.dylan.library.exception.CrashHandler;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.RunTaskUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import retrofit2.Call;

/**
 * Created by Dylan on 2016/9/1.
 */
public class MyApplication extends MultiDexApplication {
    private static MyApplication mContext;
    private RestApi mRestApi;
    public static String qiniuDomain;
    private static String mUploadToken;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        AndKit.init(this);
        Logger.setDebugMode(true);
        mRestApi = RestApi.Factory.getInstance(RestApi.Factory.STRING_CONVERTER);
        CrashHandler.getInstance().init(MyApplication.this);
        RunTaskUtils.registerActivityLifeCallBack(MyApplication.this, new RunTaskUtils.RunningListner() {
            @Override
            public void onForeground() {
                Log.e( "onForeground: ","在前台" );
                ActivityManager.getInstance().setOnBackGround(false);

            }

            @Override
            public void onBackground() {
                Log.e( "onBackground: ","在后台" );
                ActivityManager.getInstance().setOnBackGround(true);
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityManager.getInstance().addActivity(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ActivityManager.getInstance().removeActivity(activity);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            monitor();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void monitor(){
        try {
        Class activityManager = Class.forName("android.app.ActivityManager");
        Method getDefaultMethod = activityManager.getMethod("getService");
        Object iActivityManager = getDefaultMethod.invoke(null);
        Method registerMethod = iActivityManager.getClass().getMethod("registerProcessObserver", IProcessObserver.class);
        registerMethod.invoke(iActivityManager, new ProcessImpl());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取七牛域名
     */
    private void loadqiniuDomain() {
        Call call=mRestApi.qiniuDomain();
        final SharedPreferences sp=mContext.getSharedPreferences("uploadtoken",MODE_PRIVATE);
        qiniuDomain=sp.getString("qiniuDomain","");
        ResponseBodyParser.parse(call, new IResponBodyImpl() {
            @Override
            public void onSucess(String jsonData, Gson gson) {
                try {
                    jsonData=new JSONObject(jsonData).getString("BucketDomain");
                    QiNiuDomain domain=gson.fromJson(jsonData,QiNiuDomain.class);
                    qiniuDomain=domain.getBucketDomain();
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("qiniuDomain",qiniuDomain);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    



    public static String getUpLoadToken(){
        return mUploadToken;
    }




    public static String setURL(String url){
        if (url!=null&&!url.isEmpty()){
            if (url.contains("http"))return url;
            else{
                if (EmptyUtils.isNotEmpty(qiniuDomain)){
                    return qiniuDomain+url;
                }else{
                    return "";
                }
            }
        }


        return "";

    }
    public static Application getApplication() {
        return mContext;
    }

    static class ProcessImpl extends IProcessObserver.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void onForegroundActivitiesChanged(int pid, int uid, boolean foregroundActivities) throws RemoteException {

           Log.e("logtag", "onForegroundActivitiesChanged: " + pid);
        }

        @Override
        public void onForegroundServicesChanged(int pid, int uid, int serviceTypes) throws RemoteException {

        }

        @Override
        public void onProcessDied(int pid, int uid) throws RemoteException {

        }
    };
}
