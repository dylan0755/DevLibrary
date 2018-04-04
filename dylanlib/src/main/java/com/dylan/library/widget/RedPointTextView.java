package com.dylan.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dylan.library.R;


/**
 *  文本末尾追加小圆点
 */
public class RedPointTextView extends TextView {
    private int visibility = 0;
    private float pointSize;

    public RedPointTextView(Context context) {
        super(context);
        getAttribute(context,null);
    }

    public RedPointTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttribute(context,attrs);

    }

    public RedPointTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttribute(context,attrs);
    }

    public void getAttribute(Context context, AttributeSet attrs) {
        if(attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RedPointTextView);
            pointSize =array.getDimension(R.styleable.RedPointTextView_redPointSize,8);
            array.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(visibility == 1) {
            int paddingRight = getPaddingRight();
            int paddingLeft=getPaddingLeft();
            String text=getText().toString();
            float endLineTextCenterX=0;
            float endLineTextCenterY=0;
            if (text!=null&&!text.isEmpty()){
                int line=getLayout().getLineCount();
                if (line>1){
                    int start=getLayout().getLineStart(line-1);
                    int end=getLayout().getLineEnd(line-1);
                    float endLineTextWidth=getPaint().measureText(text.substring(start,end));//获取最后一行的宽度
                    endLineTextCenterX=endLineTextWidth+paddingLeft+paddingRight;
                    int top=getLayout().getLineTop(line-1);
                    int bottom=getLayout().getLineBottom(line-1);
                    endLineTextCenterY=(top+bottom)/2;
                }else if (line==1){
                    float endLineTextWidth=getPaint().measureText(text);
                    endLineTextCenterX=endLineTextWidth+paddingLeft+paddingRight/2;
                    int top=getLayout().getLineTop(0);
                    int bottom=getLayout().getLineBottom(0);
                    endLineTextCenterY=(top+bottom)/2;
                }
                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setAntiAlias(true);
                paint.setDither(true);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawCircle(endLineTextCenterX, endLineTextCenterY, pointSize /2, paint);
            }

        }
    }


    public void setPointVisible(){
        visibility = 1;
        invalidate();
    }

    public void setPointInVisible(){
        visibility =0;
        invalidate();
    }
}