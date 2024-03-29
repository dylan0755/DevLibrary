package com.dylan.library.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.dylan.library.screen.ScreenUtils;

/**
 * Author: Dylan
 * Date: 2019/8/7
 * Desc:
 */
public class CompatUtils {
    private static Context mContext;

    public static void initContext(Context context) {
        mContext = context;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static int getColor(@ColorRes int id) {
        if (mContext==null)return 0;
        return ContextCompat.getColor(mContext, id);
    }

    public static Drawable getDrawable(@DrawableRes int id){
        if (mContext==null)return null;
        return ContextCompat.getDrawable(mContext,id);
    }

    public static String getString(@StringRes int id){
        if (mContext==null)return "";
        return mContext.getResources().getString(id);
    }


    public static void  setTint(Drawable drawables,int color){
        Drawable wrappedDrawable=DrawableCompat.wrap(drawables);
        DrawableCompat.setTint(wrappedDrawable,color);
    }


    public static void setTextAppearance(TextView textView,int resId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(resId);
        }else{
            textView.setTextAppearance(textView.getContext(),resId);
        }
    }

    public static float getDimension(int resId){
        if (mContext==null)return 0.0f;
        return mContext.getResources().getDimension(resId);
    }


    public static ColorDrawable getCompatBitmapDrawable(){
        return new ColorDrawable(android.graphics.Color.TRANSPARENT);
    }


    public static void compatAlertDialogAfterShowMethod(AlertDialog dialog){
        if (dialog.getWindow()!=null){
            WindowManager.LayoutParams layoutParams= dialog.getWindow().getAttributes();
            layoutParams.width= (int) (ScreenUtils.getScreenWidth(dialog.getContext())* 0.95);
            layoutParams.gravity= Gravity.CENTER;
            dialog.getWindow().setAttributes(layoutParams);//要重新set 一遍，系统才会通知更新属性
        }
    }

}
