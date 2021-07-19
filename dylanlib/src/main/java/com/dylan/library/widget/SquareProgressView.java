package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;

import com.dylan.library.utils.Logger;


/**
 * Author: Dylan
 * Date: 2021/07/19
 * Desc:
 */
public class SquareProgressView extends View {

    private String TAG = "SquareProgress";
    //各个画笔的颜色
    private int maxColor = Color.TRANSPARENT;//总进度条颜色为灰色
    private int curColor = Color.parseColor("#FFFADB22");//当前进度条颜色为黄色
    private int dotColor = Color.RED;//进度条前端的小圆点为红色
    private float allLength;//进度条的总长度
    private int maxProgress = 100;//总的进度条长度为100（可改变）
    private int curProgress = 5;//当前进度为30（可改变）
    private Paint curPaint;//当前进度条的画笔
    private Paint maxPaint;//总进度条的画笔
    private Paint dotPaint;//进度条前端小圆点的画笔
//    private int width;//整个view的宽度，（包括paddingleft和paddingright）
//    private int height;//整个view的高度，（包括paddingtop和paddingbottom）
    private float maxProgressWidth;//整个进度条画笔的宽度
    private float curProgressWidth;//当前进度条画笔的宽度
    private float dotDiameter;//进度条顶端小圆点的直径
    private RectShape maxRectShape;
    private RectShape curRectShape;
    private boolean canDisplayDot = false;//是否显示小圆点 默认不显示
    private Path curPath;//当前进度条的路径，（总的进度条的路径作为onDraw的局部变量）
    private float proWidth;//整个进度条构成矩形的宽度
    private float proHeight;//整个进度条构成矩形的高度
    private float dotCX;//小圆点的X坐标（相对view）
    private float dotCY;//小圆点的Y坐标（相对view）

    public SquareProgressView(Context context) {
        super(context);
        initView();
    }

    public SquareProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SquareProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        canDisplayDot = false;//默认能显示小圆点

        curPaint = new Paint();//当前进度条的画笔设置
        curProgressWidth = dp2Px(5);//dp转px
        curPaint.setAntiAlias(true);//设置画笔抗锯齿
        curPaint.setStyle(Paint.Style.STROKE);//设置画笔（忘了）
        curPaint.setStrokeWidth(curProgressWidth);//设置画笔宽度
        curPaint.setColor(curColor);//设置画笔颜色

        maxProgressWidth = dp2Px(0);//总的进度条的画笔设置
        maxPaint = new Paint();
        maxPaint.setAntiAlias(true);
        maxPaint.setColor(maxColor);
        maxPaint.setStyle(Paint.Style.STROKE);
        maxPaint.setStrokeWidth(maxProgressWidth);

        dotPaint = new Paint();//小圆点的画笔设置
        dotDiameter = dp2Px(0);
        dotPaint.setAntiAlias(true);
        dotPaint.setStyle(Paint.Style.FILL);//因为是画圆，所以这里是这种模式
        dotPaint.setColor(dotColor);

        maxRectShape = new RectShape();//没用到
        curRectShape = new RectShape();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        width = measureWidth(widthMeasureSpec);//得到view的宽度
//        height = measureHeight(heightMeasureSpec);//得到view的高度
//        setMeasuredDimension(width, height);//将自己重新测量的宽高度应用到视图上（只设置size而不设置mode，mode是在布局中就确定了的）
//    }

    @Override
    protected void onDraw(Canvas canvas) {

        int tWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();//得到整个view出去padding后的宽度
        int tHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();//得到整个view除去padding后的高度
//        int point1X = getPaddingLeft() + tWidth / 10;//得到第一个点的X坐标（相对于view）
//        int point1Y = getPaddingTop() + tHeight / 10;
//        int point2X = tWidth + getPaddingLeft() - tWidth / 10;
//        int point2Y = getPaddingTop() + tHeight / 10;
//        int point3X = tWidth + getPaddingLeft() - tWidth / 10;
//        int point3Y = tHeight + getPaddingTop() - tHeight / 10;
//        int point4X = getPaddingLeft() + tWidth / 10;
//        int point4Y = tHeight + getPaddingTop() - tHeight / 10;


        int point1X = getPaddingLeft();//得到第一个点的X坐标（相对于view）
        int point1Y = getPaddingTop();
        int point2X = tWidth + getPaddingLeft();
        int point2Y = getPaddingTop();
        int point3X = tWidth + getPaddingLeft();
        int point3Y = tHeight + getPaddingTop();
        int point4X = getPaddingLeft();
        int point4Y = tHeight + getPaddingTop();



        Logger.i("onDraw: point1:" + point1X + "," + point1Y);
        Logger.i("onDraw: point2:" + point2X + "," + point2Y);
        Logger.i("onDraw: point3:" + point3X + "," + point3Y);
        Logger.i("onDraw: point4:" + point4X + "," + point4Y);
        proWidth = point3X - point1X;
        proHeight = point3Y - point1Y;
        Logger.i("onDraw: point5:" + proWidth + "," + proHeight);
        Path maxpath = new Path();//整个进度条的路径
        maxpath.moveTo(point1X, point1Y);
        maxpath.lineTo(point2X, point2Y);
        maxpath.lineTo(point3X, point3Y);
        maxpath.lineTo(point4X, point4Y);
        maxpath.close();
        canvas.drawPath(maxpath, maxPaint);
        allLength = 2 * (proWidth + proHeight);
        curPath = new Path();//当前进度条的路径
        curPath.moveTo(point1X, point1Y);
        float curPersent = (float) curProgress / maxProgress;//当前进度占总进度的百分比
        if (curPersent > 0) {
            if (curPersent < proWidth / allLength) {//处在第一段上面的小圆点的原点坐标和当前进度条的路径
                dotCX = point1X + allLength * curProgress / maxProgress;
                dotCY = point1Y;
                curPath.lineTo(dotCX, dotCY);
            } else if (curPersent < (proHeight + proWidth) / allLength) {
                dotCX = point2X;
                dotCY = point1Y + allLength * curProgress / maxProgress - proWidth;
                curPath.lineTo(point2X, point2Y);
                curPath.lineTo(dotCX, dotCY);
            } else if (curPersent < (2 * proWidth + proHeight) / allLength) {
                dotCX = point1X + allLength - proHeight - allLength * curProgress / maxProgress;
                dotCY = point4Y;
                curPath.lineTo(point2X, point2Y);
                curPath.lineTo(point3X, point3Y);
                curPath.lineTo(dotCX, dotCY);
            } else if (curPersent < 1) {
                dotCX = point1X;
                dotCY = point1Y + allLength - allLength * curProgress / maxProgress;
                curPath.lineTo(point2X, point2Y);
                curPath.lineTo(point3X, point3Y);
                curPath.lineTo(point4X, point4Y);
                curPath.lineTo(dotCX, dotCY);
            } else if (curPersent > 1) {
                dotCX = point1X;
                dotCY = point1Y;
                curPath.lineTo(point2X, point2Y);
                curPath.lineTo(point3X, point3Y);
                curPath.lineTo(point4X, point4Y);
                curPath.close();
            }
        } else {
            dotCX = point1X;
            dotCY = point1Y;
            curPath.lineTo(point1X, point1Y);
        }
        Logger.i(TAG, "onDraw: dotC:" + dotCX + "," + dotCY);
        canvas.drawPath(curPath, curPaint);
        if (canDisplayDot) {
            canvas.drawCircle(dotCX, dotCY, dotDiameter * 0.6f, dotPaint);
        }
        super.onDraw(canvas);
    }

    public void setColor(int bgColor, int progressColor) {
        maxColor = bgColor;
        curColor = progressColor;
        maxPaint.setColor(maxColor);
        curPaint.setColor(curColor);
    }


    public void setPathWidth(int bgPathWidth, int progressPathWidth) {
        maxPaint.setStrokeWidth(bgPathWidth);
        curPaint.setStrokeWidth(progressPathWidth);
    }


    private int measureWidth(int widthMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(widthMeasureSpec);//得到measurespec的模式
        int size = MeasureSpec.getSize(widthMeasureSpec);//得到measurespec的大小
        int padding = getPaddingLeft() + getPaddingRight();//得到padding在宽度上的大小
        if (mode == MeasureSpec.EXACTLY)//这种模式对应于match_parent和具体的数值dp
        {
            Logger.e("result="+size);
            result = size;
        } else {
            result = getSuggestedMinimumWidth();//得到屏幕能给的最大的view的最小宽度，原话：Returns the suggested minimum width that the view should use. This returns the maximum of the view's minimum width and the background's minimum width
            result += padding;//考虑padding后最大的view最小宽度
            if (mode == MeasureSpec.AT_MOST)//这种模式对应于wrap_parent
            {

                result = Math.max(result, size);
                Logger.e("result="+result);
            }
        }
        return result;
    }

    public void setCurProgress(int curProgress) {
        this.curProgress = curProgress;
        invalidate();
    }

    private int measureHeight(int heightMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int padding = getPaddingBottom() + getPaddingTop();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.max(result, size);
            }
        }
        return result;
    }

    /**
     * 数据转换: dp---->px
     */
    private float dp2Px(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }


    public int getCurProgress() {
        return curProgress;
    }
}
