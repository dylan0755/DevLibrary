package com.dylan.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.dylan.library.R;


/**
 * 文本末尾追加小圆点
 */
public class RedPointTextView extends AppCompatTextView {
    private int visibility = 0;
    private float pointSize;
    private int pointColor = Color.RED;
    Paint mPaint ;
    public RedPointTextView(Context context) {
        super(context);
        getAttribute(context, null);
    }

    public RedPointTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttribute(context, attrs);

    }

    public RedPointTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttribute(context, attrs);
    }

    public void getAttribute(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RedPointTextView);
            pointSize = array.getDimension(R.styleable.RedPointTextView_redPointSize, 8);
            pointColor = array.getColor(R.styleable.RedPointTextView_pointColor, Color.RED);
            array.recycle();
        }
        mPaint= new Paint();
        mPaint.setColor(pointColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (visibility == 1) {
//            int paddingRight = getPaddingRight();
//            int paddingLeft = getPaddingLeft();
            String text = getText().toString();
            float endLineTextCenterX = 0;
            float endLineTextCenterY = 0;
            if (!text.isEmpty()) {
                int line = getLayout().getLineCount();
                if (line > 1) {
                    int start = getLayout().getLineStart(line - 1);
                    int end = getLayout().getLineEnd(line - 1);
                    //float endLineTextWidth = getPaint().measureText(text.substring(start, end));//获取最后一行的宽度
                    // endLineTextCenterX = endLineTextWidth + paddingLeft + paddingRight;
                    endLineTextCenterX = getPaint().measureText(text.substring(start, end));
                    int top = getLayout().getLineTop(line - 1);
                    int bottom = getLayout().getLineBottom(line - 1);
                    endLineTextCenterY = (top + bottom)*1.0f / 2;
                } else if (line == 1) {
                    //float endLineTextWidth =getPaint().measureText(text) ;
                    //  endLineTextCenterX = endLineTextWidth + paddingLeft + paddingRight / 2;
                    endLineTextCenterX = getPaint().measureText(text);
                    int top = getLayout().getLineTop(0);
                    int bottom = getLayout().getLineBottom(0);
                    endLineTextCenterY = (top + bottom)*1.0f / 2;
                }


                canvas.drawCircle(endLineTextCenterX, endLineTextCenterY, pointSize / 2, mPaint);
            }

        }
    }


    public void showPoint(int pointColor) {
        this.pointColor = pointColor;
        mPaint.setColor(pointColor);
        visibility = 1;
        invalidate();
    }

    public void showPoint() {
        visibility = 1;
        invalidate();
    }

    public void hidePoint() {
        visibility = 0;
        invalidate();
    }
}