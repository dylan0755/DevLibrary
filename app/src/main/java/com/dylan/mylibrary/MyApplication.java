package com.dylan.mylibrary;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.dylan.library.utils.Logger;
import com.dylan.mylibrary.bean.QiNiuDomain;
import com.dylan.mylibrary.domain.RestApi;
import com.dylan.mylibrary.util.IResponBodyImpl;
import com.dylan.mylibrary.util.ResponseBodyParser;
import com.dylan.library.exception.CrashHandler;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.RunTaskUtils;
import com.dylan.library.utils.ToastUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

/**
 * Created by Dylan on 2016/9/1.
 */
public class MyApplication extends MultiDexApplication {
    private static Context mContext;
    private RestApi mRestApi;
    public static String qiniuDomain;
    private static String mUploadToken;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        ToastUtils.initToast(this);
        Logger.setDebugMode(true);
        mRestApi = RestApi.Factory.getInstance(RestApi.Factory.STRING_CONVERTER);
        CrashHandler.getInstance().init(MyApplication.this);
        RunTaskUtils.registerActivityLifeCallBack(MyApplication.this, new RunTaskUtils.RunningListner() {
            @Override
            public void onForeground() {
                Log.e( "onForeground: ","在前台" );
            }

            @Override
            public void onBackground() {
                Log.e( "onBackground: ","在后台" );
            }
        });
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
    public static Context getContext() {
        return mContext;
    }
}
