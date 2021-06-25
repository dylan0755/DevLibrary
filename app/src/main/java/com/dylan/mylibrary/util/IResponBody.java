package com.dylan.mylibrary.util;

import com.google.gson.Gson;

/**
 * Created by Dylan on 2016/9/26.
 */
public interface IResponBody {
     void onSucess(String jsonData, Gson gson);
    void onErro(String erromessage);//访问服务器，接口失败

}
