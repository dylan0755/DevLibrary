package com.dylan.library.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.utils.Logger;


/**
 * Author: Dylan
 * Date: 2020/4/1
 * Desc:
 */
public class DrawableCenterTextView extends TextView {
    public final static int  POSITION_LEFT=0;
    public final static int  POSITION_TOP=1;
    public final static int  POSITION_RIGHT=2;
    public final static int  POSITION_BOTTOM=3;

    int leftDrawableWidth=10;
    int leftDrawableHeight=10;
    int topDrawableWidth=10;
    int topDrawableHeight=10;
    int rightDrawableWidth=10;
    int rightDrawableHeight=10;
    int bottomDrawableWidth=10;
    int bottomDrawableHeight=10;
    Paint mPaint;
    Paint mPaint2;
    Rect mBound;
    Drawable left;
    Drawable top;
    Drawable right;
    Drawable bottom;
    public DrawableCenterTextView(Context context) {
        this(context,null,0);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs, defStyleAttr);
        setIncludeFontPadding(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawableCenterTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttributes(context, attrs, defStyleAttr);
        setIncludeFontPadding(false);
    }


    public void getAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DLDrawableTextView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            if (attr==R.styleable.DLDrawableTextView_drawableLeftWidth){
                leftDrawableWidth = a.getDimensionPixelSize(attr,10);
            }else if (attr==R.styleable.DLDrawableTextView_drawableLeftHeight){
                leftDrawableHeight = a.getDimensionPixelSize(attr, 10);
            }else if (attr==R.styleable.DLDrawableTextView_drawableTopWidth){
                topDrawableWidth = a.getDimensionPixelSize(attr,10);
            }else if (attr==R.styleable.DLDrawableTextView_drawableTopHeight){
                topDrawableHeight = a.getDimensionPixelSize(attr, 10);
            }else if (attr==R.styleable.DLDrawableTextView_drawableRightWidth){
                rightDrawableWidth = a.getDimensionPixelSize(attr,10);
            }else if (attr==R.styleable.DLDrawableTextView_drawableRightHeight){
                rightDrawableHeight = a.getDimensionPixelSize(attr, 10);
            }else if (attr==R.styleable.DLDrawableTextView_drawableBottomWidth){
                bottomDrawableWidth = a.getDimensionPixelSize(attr,10);
            }else if (attr==R.styleable.DLDrawableTextView_drawableBottomHeight){
                bottomDrawableHeight = a.getDimensionPixelSize(attr, 10);
            }
        }
        a.recycle();

        /*
         * setCompoundDrawablesWithIntrinsicBounds方法会首先在父类的构造方法中执行，
         * 彼时执行时drawable的大小还都没有开始获取，都是0,
         * 这里获取完自定义的宽高属性后再次调用这个方法，插入drawable的大小
         * */
        setCompoundDrawablesWithIntrinsicBounds(
                left,top,right,bottom);


    }


    /**
     * Sets the Drawables (if any) to appear to the left of, above, to the
     * right of, and below the text. Use {@code null} if you do not want a
     * Drawable there. The Drawables' bounds will be set to their intrinsic
     * bounds.
     * <p>
     * Calling this method will overwrite any Drawables previously set using
     * {@link #setCompoundDrawablesRelative} or related methods.
     * 这里重写这个方法，来设置上下左右的drawable的大小
     *
     * @attr ref android.R.styleable#TextView_drawableLeft
     * @attr ref android.R.styleable#TextView_drawableTop
     * @attr ref android.R.styleable#TextView_drawableRight
     * @attr ref android.R.styleable#TextView_drawableBottom
     */
    @Override
    public void setCompoundDrawablesWithIntrinsicBounds( Drawable left,
                                                         Drawable top,
                                                         Drawable right,
                                                         Drawable bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;


        if (left != null) {
            left.setBounds(0, 0, leftDrawableWidth,leftDrawableHeight);
        }
        if (right != null) {
            right.setBounds(0, 0, rightDrawableWidth,rightDrawableHeight);
        }
        if (top != null) {
            top.setBounds(0, 0, topDrawableWidth,topDrawableHeight);
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, bottomDrawableWidth,bottomDrawableHeight);
        }

        setCompoundDrawables(left, top, right, bottom);
    }

    /*
     * 代码中动态设置drawable的宽高度
     * */
    public void setDrawableSize(int width, int height,int position) {
        if (position==this.POSITION_LEFT) {
            leftDrawableWidth = width;
            leftDrawableHeight = height;
        }
        if (position==this.POSITION_TOP) {
            topDrawableWidth = width;
            topDrawableHeight = height;
        }
        if (position==this.POSITION_RIGHT) {
            rightDrawableWidth = width;
            rightDrawableHeight = height;
        }
        if (position==this.POSITION_BOTTOM) {
            bottomDrawableWidth = width;
            bottomDrawableHeight = height;
        }

        setCompoundDrawablesWithIntrinsicBounds(
                left,top,right,bottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // 获取TextView的Drawable对象，返回的数组长度应该是4，对应左上右下
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {

            if (drawables[0]!=null||drawables[2]!=null){//左边和右边
                setGravity(Gravity.CENTER_VERTICAL);
                int drawableWidth;
                if (drawables[0]!=null){
                    drawableWidth=leftDrawableWidth;
                }else{
                    drawableWidth=rightDrawableWidth;
                }


                int drawablePadding = getCompoundDrawablePadding();
                //文本宽度
                float textWidth =0;
                if (getLineCount()>1){
                    Layout layout=getLayout();
                    String firstLineStr = getText().toString().substring(0, layout.getLineEnd(0));
                    textWidth=getPaint().measureText(firstLineStr);
                }else{
                    textWidth=getPaint().measureText(getText().toString());
                }

                float bodyWidth = textWidth + drawablePadding + drawableWidth+getPaddingLeft()+getPaddingRight();
                // 移动画布开始绘制的X轴
                canvas.translate((getWidth() - bodyWidth) / 2, 0);

            }else if (drawables[1]!=null||drawables[3]!=null){//上边 跟底部
                setGravity(Gravity.CENTER_HORIZONTAL);
                int drawableHeight=0;
                if (drawables[1]!=null){
                    drawableHeight=topDrawableHeight;
                }else{
                    drawableHeight=bottomDrawableHeight;
                }

                Rect rect = new Rect();
                getPaint().getTextBounds(getText().toString(), 0, getText().toString().length(), rect);
//                float textHeight = rect.height(); //该方法不准确
                Paint.FontMetrics fontMetrics2 = getPaint().getFontMetrics();
                float textHeight = (int) (Math.ceil(fontMetrics2.descent - fontMetrics2.ascent));
                textHeight*=getLineCount();

                int drawablePadding = getCompoundDrawablePadding();
                // 计算总高度（文本高度 + drawablePadding + drawableHeight）
                float bodyHeight = textHeight + drawablePadding + drawableHeight+getPaddingTop()+getPaddingBottom();
                // 移动画布开始绘制的Y轴
                canvas.translate(0, (getHeight() - bodyHeight) / 2);
            }


        }
        super.onDraw(canvas);
    }





}



