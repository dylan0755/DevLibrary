package com.dankal.mylibrary.util;

import com.google.gson.Gson;

/**
 * Created by Dylan on 2016/9/26.
 */
public abstract  class IResponBodyImpl implements IResponBody{
     

    @Override
    public abstract void onSucess(String jsonData, Gson gson) ;



    @Override
    public void onErro(String erromessage) {
        //Log.e( "onErro: ",""+erromessage );
    }





}
