package com.dylan.library.manager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.annotation.NonNull;

import com.dylan.library.R;

/**
 * Created by Dylan on 2017/10/21.
 */

public class ResourceManger {
    public  static int getThemePrimaryColor(@NonNull Context context) {
        return getColorByAttrIndex(context,0);
    }


    public static int getThemePrimaryDarkColor(@NonNull Context context){
        return getColorByAttrIndex(context,1);
    }


    public static int getThemeAccentColor(@NonNull Context context){
        return getColorByAttrIndex(context,2);
    }


    private static int getColorByAttrIndex(Context context, int attrIndex){
        int resId=context.getResources().getIdentifier("AppTheme","style",context.getPackageName());
        if (resId==0){
            return Color.BLACK;
        }
        //第一个参数 redId 是调用者项目style.xml，第二个是本项目中定义的，
        // 意思是从调用者项目的AppTheme 下找到自己定义的参数 colorPrimary，
        // 如果调用者没有定义，则找不到返回黑色。
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(resId, R.styleable.dl_base_color);
        int color =typedArray.getColor(attrIndex,Color.BLACK);
        typedArray.recycle();
        return color;
    }

}
