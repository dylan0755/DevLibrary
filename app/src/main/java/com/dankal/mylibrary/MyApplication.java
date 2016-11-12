package com.dankal.mylibrary;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.dankal.mylibrary.bean.QiNiuDomain;
import com.dankal.mylibrary.domain.RestApi;
import com.dankal.mylibrary.util.IResponBodyImpl;
import com.dankal.mylibrary.util.ResponseBodyParser;
import com.dylan.library.util.StringCheck;
import com.dylan.library.util.ToastUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

/**
 * Created by Dylan on 2016/9/1.
 */
public class MyApplication extends Application {
    private static Context mContext;
    private RestApi mRestApi;
    public static String qiniuDomain;
    private static String mUploadToken;
    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.mContext = this;
        mContext = this;
        mRestApi = RestApi.Factory.getInstance(RestApi.Factory.STRING_CONVERTER);
        loadqiniuDomain();
    
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
                if (StringCheck.isValid(qiniuDomain)){
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
