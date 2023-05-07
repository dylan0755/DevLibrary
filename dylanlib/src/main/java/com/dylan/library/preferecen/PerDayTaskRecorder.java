package com.dylan.library.preferecen;

import android.content.SharedPreferences;

import com.dylan.library.utils.DateUtils;
import com.dylan.library.utils.ReflectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: Dylan
 * Date: 2023/5/1
 * Desc:
 */
public class PerDayTaskRecorder {
    private SharedPreferences mSharePreference;

    public PerDayTaskRecorder(String name) {

        mSharePreference = ReflectUtils.getApplication().getSharedPreferences(name, 0);
    }

    public boolean hasSigned(){
        return mSharePreference.getBoolean(wrapKey("sign"),false);
    }

    public void finishSign(){
        mSharePreference.edit().putBoolean(wrapKey("sign"),true).apply();
    }


    public SharedPreferences getSharePreference() {
        return mSharePreference;
    }

    public void clearHistory() {
        String currentDayPrefix=DateUtils.getCurrentData("yyy-MM-dd");
        Map<String, ?> map = mSharePreference.getAll();
        List<String> waitRemoveKeys=new ArrayList<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (!entry.getKey().startsWith(currentDayPrefix)){
                waitRemoveKeys.add(entry.getKey());
            }
        }
        SharedPreferences.Editor editor=mSharePreference.edit();
        for (String key:waitRemoveKeys){
            editor.remove(key);
        }
        editor.apply();
    }

    public void putString(String key,String value){
        mSharePreference.edit().putString(wrapKey(key),value).apply();
    }

    public String getString(String key){
        return mSharePreference.getString(wrapKey(key),"");
    }

    public boolean getBoolean(String key){
        return mSharePreference.getBoolean(wrapKey(key),false);
    }

    public void putBoolean(String key,boolean value){
        mSharePreference.edit().putBoolean(wrapKey(key),value).apply();
    }
    public int getInt(String key){
        return mSharePreference.getInt(wrapKey(key),0);
    }

    public int getInt(String key,int defaultV){
        return mSharePreference.getInt(wrapKey(key),defaultV);
    }

    public void putInt(String key,int value){
        mSharePreference.edit().putInt(wrapKey(key),value).apply();
    }
    public void putLong(String key,long value){
        mSharePreference.edit().putLong(wrapKey(key),value).apply();
    }

    public long getLong(String key){
        return mSharePreference.getLong(wrapKey(key),0);
    }




    public int increaseCount(String key) {
        int count = mSharePreference.getInt(wrapKey(key), 0);
        count += 1;
        mSharePreference.edit().putInt(wrapKey(key), count).apply();
        return count;
    }

    public int getCount(String key) {
        return mSharePreference.getInt(wrapKey(key), 0);
    }



    public static String wrapKey(String key) {
        return DateUtils.getCurrentData("yyy-MM-dd")+"_"+key;
    }

    public  boolean hasFinishTask(String taskName){
        return getBoolean(wrapKey(taskName));
    }
    public void finishTask(String taskName){
         putBoolean(wrapKey(taskName), true);
    }

}