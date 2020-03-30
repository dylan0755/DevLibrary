package com.dylan.library.manager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Set;

/**
 * Author: Dylan
 * Date: 2020/3/30
 * Desc:
 */
public class AppSpManager {
    private static final String FIST_OPEN_WHILE_INSTALLED="firstOpenWhileInstalled";
    private static final String TAG=AppSpManager.class.getSimpleName();
    private static AppSpManager appSpManager;
    private static SharedPreferences sharedPreferences;

    private AppSpManager(Context context){
        sharedPreferences=context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
    }

    public  synchronized static AppSpManager init(Application  application){
         if (appSpManager ==null){
             synchronized (AppSpManager.class){
                 if (appSpManager ==null){
                     appSpManager =new AppSpManager(application);
                 }
             }
         }
         return appSpManager;
    }

    /**
     * 单例模式，获取instance实例
     * @param
     * @return
     */

    private static boolean check(){
        if (appSpManager==null){
            Log.e(TAG, "please init first!");
            return false;
        }

        return true;
    }


    public static void putBoolean(String key, boolean value) {
        if (!check())return;
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key) {
        if (!check())return false;
        return sharedPreferences.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        if (!check())return false;
        return sharedPreferences.getBoolean(key, defValue);
    }


    public static void putString(String key, String value) {
        if (!check())return ;
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        if (!check())return "";
        return sharedPreferences.getString(key, defValue);
    }


    public static void putFloat(String key, float value) {
        if (!check())return ;
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public static float getFloat(String key) {
        if (!check())return 0 ;
        return sharedPreferences.getFloat(key, 0);
    }


    public static float getFloat(String key, float defValue) {
        if (!check())return 0 ;
        return sharedPreferences.getFloat(key, defValue);
    }


    public  static void putInt(String key, int value) {
        if (!check())return  ;
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static float getInt(String key) {
        if (!check())return  0;
        return sharedPreferences.getInt(key,0);
    }

    public static float getInt(String key, int defValue) {
        if (!check())return  0;
        return sharedPreferences.getInt(key,defValue);
    }



    public static void putLong(String key, long value) {
        if (!check())return ;
        sharedPreferences.edit().putLong(key, value).apply();

    }

    public static long getLong(String key) {
        if (!check())return 0;
        return sharedPreferences.getLong(key, 0);
    }

    public static long getLong(String key, long defValue) {
        if (!check())return 0;
        return sharedPreferences.getLong(key, defValue);
    }


    public static void putStringSet(Set<String> set, String key) {
        if (!check())return ;
        sharedPreferences.edit().putStringSet(key, set).apply();
    }

    public static Set<String> getStringSet(String key) {
        if (!check())return null;
        return sharedPreferences.getStringSet(key, null);
    }

    public static void remove(String key) {
        if (!check())return ;
        sharedPreferences.edit().remove(key).apply();
    }


    public static boolean isFirstOpenWhileInstalled(){
       return sharedPreferences.getBoolean(FIST_OPEN_WHILE_INSTALLED,true);
    }

    public static void setIsFirstOpenWhileInstalled(boolean bl){
        sharedPreferences.edit().putBoolean(FIST_OPEN_WHILE_INSTALLED,bl).apply();
    }


}
