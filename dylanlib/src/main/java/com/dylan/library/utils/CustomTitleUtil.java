package com.dylan.library.utils;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Dylan on 2016/12/10.
 */

public class CustomTitleUtil {


    public static void  init(Activity activity, int common_title_id){
        FrameLayout mFrameLayout= (FrameLayout) activity.findViewById(android.R.id.content);
        View title= View.inflate(activity.getApplicationContext(),common_title_id,null);


        View contentView=mFrameLayout.getChildAt(0);//xml中的根部局
        LinearLayout wraplayout=new LinearLayout(activity.getApplicationContext());
        wraplayout.setOrientation(LinearLayout.VERTICAL);
        mFrameLayout.removeView(contentView);


        //先添加自定义标题标题布局，再添加xml中的根部局
        wraplayout.addView(title);
        wraplayout.addView(contentView);
        mFrameLayout.addView(wraplayout);
    }
}
