package com.dylan.library.bean;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Author: Administrator
 * Date: 2020/6/23
 * Desc:
 */
public class BundleBuilder {

    public static Bundle putInt(@Nullable String key, int value){
        Bundle bundle=new Bundle();
        bundle.putInt(key,value);
        return bundle;
    }

    public static Bundle putBoolean(@Nullable String key, boolean value){
        Bundle bundle=new Bundle();
        bundle.putBoolean(key,value);
        return bundle;
    }

    public static Bundle putString(@Nullable String key, String value){
        Bundle bundle=new Bundle();
        bundle.putString(key,value);
        return bundle;
    }

    public static Bundle putSerializable(@Nullable String key, Serializable value){
        Bundle bundle=new Bundle();
        bundle.putSerializable(key,value);
        return bundle;
    }


    public static Bundle putDouble(@Nullable String key, double value){
        Bundle bundle=new Bundle();
        bundle.putDouble(key,value);
        return bundle;
    }




}
