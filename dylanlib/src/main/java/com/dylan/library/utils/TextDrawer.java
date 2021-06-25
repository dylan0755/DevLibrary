package com.dylan.library.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

/**
 * Author: Dylan
 * Date: 2021/02/06
 * Desc:
 */
public class TextDrawer {
    private TextPaint textPaint;
    private StaticLayout staticLayout;
    private String recordText;

    public TextDrawer(){
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }


    public void setTextSize(float px){
        textPaint.setTextSize(px);
    }

    public void setTextColor(int color){
        textPaint.setColor(color);
    }

    public void setTextBold(float fraction){
        textPaint.setStrokeWidth(fraction);
    }




    public void draw(Canvas canvas,String text, int parentWidth,int parentHeight){
        if (staticLayout==null) {
            recordText=text;
            staticLayout = new StaticLayout(text, textPaint, parentWidth, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
        }else{
            if (!TextUtils.isEmpty(recordText)&&!recordText.equals(text)){//更新了文本
                recordText=text;
                staticLayout = new StaticLayout(text, textPaint, parentWidth, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
            }
        }
        canvas.save();//锁画布(为了保存之前的画布状态)
        //开始绘制的位置
        canvas.translate(0,(parentHeight-staticLayout.getHeight())*1.0f/2);
        staticLayout.draw(canvas);
        canvas.restore();
    }






}
