package com.dylan.library.bean;

import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Author: Dylan
 * Date: 2019/8/22
 * Desc: EventBus 数据传递类
 */
public class EventBundle {
    private String action;
    private Bundle bundle;
    private Object extraData;

    public EventBundle() {

    }

    public EventBundle(String action) {
        this.action = action;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }


    public EventBundle putInt(String key, int value) {
        if (bundle == null) bundle = new Bundle();
        bundle.putInt(key, value);
        return this;
    }

    public int getInt(String key) {
        if (bundle == null) return 0;
        return bundle.getInt(key);
    }

    public int getInt(String key, int defaultValue) {
        if (bundle == null) return defaultValue;
        return bundle.getInt(key, defaultValue);
    }


    public EventBundle putString(String key, String value) {
        if (bundle == null) bundle = new Bundle();
        bundle.putString(key, value);
        return this;
    }

    public String getString(String key) {
        if (bundle == null) return "";
        return bundle.getString(key);
    }

    public String getString(String key, String defaultValue) {
        if (bundle == null) return defaultValue;
        return bundle.getString(key, defaultValue);
    }


    public EventBundle putBoolean(String key, boolean value) {
        if (bundle == null) bundle = new Bundle();
        bundle.putBoolean(key, value);
        return this;
    }

    public boolean getBoolean(String key) {
        if (bundle == null) return false;
        return bundle.getBoolean(key);
    }


    public boolean getBoolean(String key, boolean defaultValue) {
        if (bundle == null) return defaultValue;
        return bundle.getBoolean(key, defaultValue);
    }


    public EventBundle putLong(String key,long value){
        if (bundle==null)bundle=new Bundle();
        bundle.putLong(key,value);
        return this;
    }

    public long getLong(String key){
        if (bundle==null)return 0;
       return bundle.getLong(key);
    }

    public long getLong(String key,long defaultValue){
        if (bundle==null)return 0;
        return bundle.getLong(key,defaultValue);
    }


    public EventBundle putFloat(String key,float value){
        if (bundle==null)bundle=new Bundle();
        bundle.putFloat(key,value);
        return this;
    }

    public Float getFloat(String key){
        if (bundle==null)return 0.0f;
        return bundle.getFloat(key);
    }

    public float getFloat(String key,float defaultValue){
        if (bundle==null)return 0;
        return bundle.getFloat(key,defaultValue);
    }


    public EventBundle putSerializable(String key, Serializable value){
        if (bundle==null)bundle=new Bundle();
        bundle.putSerializable(key,value);
        return this;
    }

    public Serializable getSerializable(String key){
        if (bundle==null)return null;
        return bundle.getSerializable(key);
    }


    public EventBundle putParcelable(String key, Parcelable value){
        if (bundle==null)bundle=new Bundle();
        bundle.putParcelable(key,value);
        return this;
    }

    public Parcelable getParcelable(String key){
        if (bundle==null)return null;
        return bundle.getParcelable(key);
    }



}
