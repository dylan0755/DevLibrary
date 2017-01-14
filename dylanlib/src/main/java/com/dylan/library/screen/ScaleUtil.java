package com.dylan.library.screen;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Dylan on 2016/10/21.
 */
public class ScaleUtil {
    private static final int BASE_WIDTH =1080;
    private  static float BASE_RATIO=1;
    public ScaleUtil(Context context){
        if (context==null)return;
        //判断现在是横屏还是竖屏状态
        int width=0;
        boolean flag= context.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT;
        if (flag){
            width=context.getResources().getDisplayMetrics().widthPixels;
        }else{
            width=context.getResources().getDisplayMetrics().heightPixels;
        }
        BASE_RATIO = 1.0f * width/ BASE_WIDTH;
    }



    public int toScaleSize(int px){
        return (int) (BASE_RATIO*px);
    }
}
