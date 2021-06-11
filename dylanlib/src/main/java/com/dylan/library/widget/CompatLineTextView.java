package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/2/25
 * Desc:
 */

@Deprecated
public class CompatLineTextView extends TextView {
    private int maxLine = Integer.MAX_VALUE;
    private String ellipString = "...";
    private static final int DEFAULT_VIEW_WIDTH = 200;
    private int maxWidth;

    public CompatLineTextView(Context context) {
        this(context, null);
    }

    public CompatLineTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompatLineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //当布局中设置了 singLine ,则下面获取则为1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            maxLine = getMaxLines();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentSpecWidth = MeasureSpec.getSize(widthMeasureSpec);
        //设置自身宽度
        if (parentSpecMode == MeasureSpec.EXACTLY) {
            maxWidth = parentSpecWidth; //建议：result直接使用确定值
        } else if (parentSpecMode == MeasureSpec.AT_MOST) {
            maxWidth = Math.min(DEFAULT_VIEW_WIDTH, parentSpecWidth);
        } else {
            maxWidth = DEFAULT_VIEW_WIDTH;
        }
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        TextPaint mPaint = getPaint();
        mPaint.setColor(getCurrentTextColor());  //这里获取字体颜色
        Paint.FontMetrics fm = mPaint.getFontMetrics();

        float baseline = fm.descent - fm.ascent;
        float x = getPaddingLeft(); //绘制从paddingLeft 开始
        float y = baseline;  //由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。


        //文本内容
        String contentText = getText().toString();
        if (contentText.isEmpty()) return;

        if (maxWidth<DEFAULT_VIEW_WIDTH){
            maxWidth=DEFAULT_VIEW_WIDTH;
        }
        int actualWidth =maxWidth- getPaddingLeft() - getPaddingRight();

        //文本自动换行
        List<String> texts = autoSplit(contentText, mPaint, actualWidth);


        for (String text : texts) {
            canvas.drawText(text, x, y, mPaint);  //坐标以控件左上角为原点
            y += baseline + fm.leading; //添加字体行间距
        }
    }

    private List<String> autoSplit(String content, Paint p, float maxWidth) {
        float ellipLength = p.measureText(ellipString);
        int length = content.length();
        float textWidth = p.measureText(content);
        List<String> list = new ArrayList<>();
        //小于直接显示
        if (textWidth <= maxWidth) {
            list.add(content);
            return list;
        }


        if (maxLine == 1) {
            int end = 1;
            while (end < length) {
                float currentWidth = p.measureText(content, 0, end);
                if (currentWidth + ellipLength > maxWidth) {
                    String result = content.substring(0, end - 1).concat(ellipString);
                    list.add(result);
                    break;
                }
                end += 1;
            }
            return list;
        }


        //多行情况
        int start = 0, end = 1;
        List<String> lineList = new ArrayList<>();
        while (start < length) {
            float currentWidth = p.measureText(content, start, end);
            //到了最大行数显示
            if (lineList.size() + 1 == maxLine) {
                if (currentWidth + ellipLength > maxWidth) { //文本宽度超出控件宽度时,要退1,从前一个字符截取做为一行
                    String endLine = (String) content.subSequence(start, end - 1);
                    endLine = endLine.concat(ellipString);
                    lineList.add(endLine);
                    break;//结束
                }
                if (end == length) { //不足一行的文本
                    lineList.add((String) content.subSequence(start, end));
                    break;
                }
            } //没有到最大行数限制
            else {
                if (currentWidth > maxWidth) { //文本宽度超出控件宽度时,要退1,从前一个字符截取做为一行
                    String lineStr = (String) content.subSequence(start, end - 1);
                    lineList.add(lineStr);
                    end -= 1;
                    start = end;
                    continue;//开始下一行
                }
                if (end == length) { //不足一行的文本
                    lineList.add((String) content.subSequence(start, end));
                    break;
                }
            }

            end += 1;
        }
        return lineList;
    }

}