package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;


/**
 * Created by Dylan on 2016/3/28.
 *
 * @author Dylan 王驰
 */
public class ExpandableListItemLayout extends HorizontalScrollView implements View.OnTouchListener {
    private Context mContext;
    private ListItemWrapperLayout mWrapper;
    private ViewGroup mContentView;
    private ViewGroup menuView;
    private int mScreenWidth;
    private int mMenuViewWidth;
    private  ActiveMode activeMode= ActiveMode.activeWhileOtherOpen;
    private boolean measured = false;
    protected boolean expanded = false;//是否已经展开
    private boolean hasColseOnDown;//按下的时候是否有关闭item操作
    private OnClickListener mClickListener;
    private int ColorValue;
    private int expandSlap;//滑动多长距离则自动弹出隐藏的菜单项
    private ExpandedRecorder mRecorder;

    public ExpandableListItemLayout(Context context) {
        this(context, null);
    }

    public ExpandableListItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        DisplayMetrics outmetricx = context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(outmetricx);
        mScreenWidth = outmetricx.widthPixels;
        setHorizontalScrollBarEnabled(false);
        setOnTouchListener(this);
        expandSlap = ViewConfiguration.get(mContext).getScaledTouchSlop();
        mRecorder = ExpandedRecorder.getExpandRecorderInstance();
        expandSlap *= 2;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!measured) {
            mWrapper = (ListItemWrapperLayout) getChildAt(0);
            mContentView = (ViewGroup) mWrapper.getChildAt(0);
            menuView = (ViewGroup) mWrapper.getChildAt(1);
            mContentView.getLayoutParams().width = mScreenWidth;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //第一次启动时执行该方法。
        if (changed) {
            close();
            measured = true;
        }
    }

    /**
     * 1.当点击屏幕的时候，要先判断是否有其他item没有关闭，
     * 如果有，则在ACTION_DOWN的时候先将其他的item关闭
     * <p>
     * 2.点击的时候也可能是点击现在所正打开的item，
     * 所以判断其他不仅仅判断！=null，还要判断是不是为当前的this
     */

    public boolean onTouchEvent(MotionEvent event, View v) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMenuViewWidth = menuView.getMeasuredWidth();
                //按下颜色变化
                setPressColor();
                //点击的时候判断当前是否有item展开，有则缩回去
                if (closeIfHasExpanding()) {
                    if (activeMode== ActiveMode.noActiveWhileOtherOpen)hasColseOnDown =true;

                }

                break;
            case MotionEvent.ACTION_CANCEL:
                setDefaultColor();
                break;
            case MotionEvent.ACTION_UP:
                setDefaultColor();
                int scrollX = getScrollX();

                if (scrollX > 0) { //布局已经发生移动
                    if (scrollX >= expandSlap) {//达到阀值
                        if (!expanded) {
                            open();
                        } else {
                            close();
                        }
                    } else {//没有达到阀值则缩回去
                        close();
                    }
                    return true;
                } else {//布局没有发生移动则判断布局是否展开状态，没有展开则响应点击事件，否则布局收缩

                    /**
                     * expanded==true，
                     * 说明ACTION_DOWN的执行只是为了让已打开的展开的Item关闭
                     * */
                    if (expanded) {
                        break;
                    }


                    if (activeMode== ActiveMode.noActiveWhileOtherOpen){
                        if (hasColseOnDown){
                            hasColseOnDown=false;
                            break;
                        }
                    }


                    if (mClickListener != null) {
                        mClickListener.onClick(v);
                    }


                }
                break;
        }

        return super.onTouchEvent(event);
    }

    private void setDefaultColor() {
        setBackgroundColor(0);
    }

    private void setPressColor() {
        if (ColorValue == 0) {
            setDefaultColor();
        } else {
            setBackgroundColor(ColorValue);
        }
    }


    //在onTouch中执行onTouchEvent
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return onTouchEvent(event, v);
    }


    public void close() {
        this.smoothScrollTo(0, 0);
        expanded = false;
        mRecorder.UnExpanding(this);

    }

    //打开一个之前先要判断上一个是否已关闭。
    public void open() {
        //传正值往负方向走
        this.smoothScrollTo(mMenuViewWidth, 0);
        expanded = true;
        mRecorder.Expanding(this);
    }

    /**
     * 删除记录
     */
    public void delete() {
        mRecorder.deleteExpandingObject();
    }

    public boolean closeIfHasExpanding() {
        ExpandableListItemLayout sl = mRecorder.getExpandingObj();
        if (sl != null && sl != this) {
            sl.close();
            return true;
        }
        return false;
    }


    public void setActiveWhileOtherOpen(ActiveMode activeMode){
        this.activeMode=activeMode;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mClickListener = l;
    }

    /**
     * 设置压下去的颜色
     */
    public void setPressColor(String colorvalue) {
        try {
            ColorValue = Color.parseColor(colorvalue);

        } catch (Exception e) {
            if (!colorvalue.startsWith("#")) {
                Log.e("Color——Erro", "do not include '#'");
            } else if (colorvalue.length() != 7) {
                Log.e("Color——Erro", "Color code length must be 7");
            } else {
                Log.e("Color——Erro", "Invaliable Code--无效码值");
            }
        }

    }

    public enum ActiveMode{
        activeWhileOtherOpen, // 当点击的某个Item的时候假如有其他Item正在展开，则关掉其它Item，并且本Item的点击事件响应
        noActiveWhileOtherOpen //当点击的某个Item的时候假如有其他Item正在展开，则关掉其它Item，但是本Item的点击事件不响应，如QQ的消息列表
    }

}
