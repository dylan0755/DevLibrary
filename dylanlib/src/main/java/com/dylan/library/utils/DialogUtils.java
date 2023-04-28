package com.dylan.library.utils;

import android.app.Dialog;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.WindowManager;

import com.dylan.library.screen.ScreenUtils;

/**
 * Author: Dylan
 * Date: 2021/05/14
 * Desc:
 */
public class DialogUtils {


    //Dialog 弹出来状态栏和Activity 保持一致
    public static void keepStatusBarSameStyleWithActivity(Dialog dialog){
        if (dialog.getWindow()!=null)dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    //AlertDialog 在部分手机不居中
    public static void compatAlertDialogAfterShowMethod(AlertDialog dialog){
        if (dialog.getWindow()!=null){
            WindowManager.LayoutParams layoutParams= dialog.getWindow().getAttributes();
            layoutParams.width= (int) (ScreenUtils.getScreenWidth(dialog.getContext())* 0.95);
            layoutParams.gravity= Gravity.CENTER;
            dialog.getWindow().setAttributes(layoutParams);//要重新set 一遍，系统才会通知更新属性
        }
    }

}
