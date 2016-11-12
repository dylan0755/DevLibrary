package com.dylan.library.screen;

import android.content.Context;

/**
 * Created by Dylan on 2016/10/21.
 */
public class ScaleUtil {
    private Context mContext;
    private static final int BASE_WIDTH =1080;
    private  static float BASE_RATIO=1;
    public ScaleUtil(Context context){
        mContext=context;
        BASE_RATIO=1.0f*mContext.getResources().getDisplayMetrics().widthPixels/ BASE_WIDTH;
    }



    public int toScaleSize(int px){
        return (int) (BASE_RATIO*px);
    }
}
