package com.dylan.mylibrary.util;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dylan on 2016/9/26.
 */
public class ResponseBodyParser {
    private static String TAG="ResponseBodyParser";
     public ResponseBodyParser(){

     }





    public static void parse(Call<String> call, final IResponBody iResponBody){
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String jsondata=response.body();
                if (jsondata==null){
                    Log.e(TAG,"parse "+jsondata );
                    return;
                }

                Gson gson=new Gson();
                try {
                    JSONObject jsonObject=new JSONObject(jsondata);
                    String state=jsonObject.optString("state");
                    if (state==null||state.isEmpty()||"001".equals(state)||state.equals("200")){
                        iResponBody.onSucess(jsondata,gson);
                    }else{
                        String message=jsonObject.getString("message");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    iResponBody.onErro(e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                throwable.printStackTrace();
                iResponBody.onErro(throwable.getMessage());
            }
        });
    }
}
