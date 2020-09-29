package com.dylan.library.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;


/**
 * Author: Administrator
 * Date: 2020/9/29
 * Desc:
 */
public class SingleClickAppCompatActivity extends AppCompatActivity {

    private String mActivityJumpTag;
    private long mClickTime;


    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (checkDoubleClick(intent)){
            super.startActivityForResult(intent, requestCode, options);
        }
    }




    /**
     * 检查是否重复跳转，不需要则重写方法并返回true
     */
    protected boolean checkDoubleClick(Intent intent) {

        // 默认检查通过
        boolean result = true;
        // 标记对象
        String tag;
        if (intent.getComponent() != null) { // 显式跳转
            tag = intent.getComponent().getClassName();
        }else if (intent.getAction() != null) { // 隐式跳转
            tag = intent.getAction();
        }else {
            return true;
        }

        if (tag.equals(mActivityJumpTag) && mClickTime >= SystemClock.uptimeMillis() - 500) {
            // 检查不通过
            result = false;
        }

        // 记录启动标记和时间
        mActivityJumpTag = tag;
        mClickTime = SystemClock.uptimeMillis();
        return result;
    }
}
