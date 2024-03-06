package com.dylan.mylibrary.util;

import android.content.Context;
import android.graphics.Color;


import com.dylan.library.utils.DensityUtils;
import com.dylan.library.widget.shape.TextDrawable;

import java.util.HashMap;

public class TextDrawableUtils {


    public static TextDrawable getTextDrawableInAvatar(Context context, String firstChar){
        return TextDrawable.builder()
                .beginConfig().textColor(Color.parseColor("#FF2D2D2D"))
                .bold()
                .fontSize(DensityUtils.dp2px(context,18))
                .width(DensityUtils.dp2px(context,50))
                .height(DensityUtils.dp2px(context,50))
                .endConfig()
                .buildRect(firstChar, Color.parseColor("#FFFADB22"));
    }

    public static TextDrawable getTextDrawableInRelationShipGraph(Context context,String firstChar){
        return TextDrawable.builder()
                .beginConfig().textColor(Color.parseColor("#FF2D2D2D"))
                .bold()
                .fontSize(DensityUtils.dp2px(context,12))
                .width(DensityUtils.dp2px(context,36))
                .height(DensityUtils.dp2px(context,36))
                .endConfig()
                .buildRect(firstChar, Color.parseColor("#FFFADB22"));
    }

    private static HashMap<String,Integer> textColorMap=new HashMap<>();
     static {

    }



}
