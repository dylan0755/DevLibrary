package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import java.lang.ref.WeakReference;


/**
 * Created by Dylan on 2016/3/28.
 *
 * @author Dylan 王驰
 */
public class ListItemExpandableLayout extends HorizontalScrollView implements View.OnTouchListener {
    private Context mContext;
    private LinearLayout mWrapper;
    private ViewGroup mContentView;
    private ViewGroup menuView;
    private int mMenuViewWidth;
    private ActiveMode activeMode = ActiveMode.activeWhileOtherOpen;
    protected boolean expanded = false;//是否已经展开
    private boolean hasColseOnDown;//按下的时候是否有关闭item操作
    private OnClickListener mClickListener;
    private int ColorValue;
    private int touchSlop;
    private static WeakReference<ListItemExpandableLayout> mRecorder;
    private Drawable defalutBackgroundDraw;
    private OverScroller scroller;

    public ListItemExpandableLayout(Context context) {
        super(context);
        initView(context);
    }

    public ListItemExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);
        setOnTouchListener(this);
        touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        scroller = new OverScroller(context);
        mWrapper = new LinearLayout(getContext());
        mWrapper.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWrapper.setLayoutParams(layoutParams);
        addView(mWrapper);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setFillViewport(true);
        defalutBackgroundDraw = getBackground();
    }

    @Override
    public void addView(View child) {
        if (getChildCount() == 0) {
            super.addView(child);
        } else {
            mWrapper.addView(child);
        }


    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() == 0) {
            super.addView(child, index);
        } else {
            mWrapper.addView(child, index);
        }

    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() == 0) {
            super.addView(child, params);
        } else {
            mWrapper.addView(child, params);
        }

    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() == 0) {
            super.addView(child, index, params);
        } else {
            mWrapper.addView(child, index, params);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        mContentView = (ViewGroup) mWrapper.getChildAt(0);
        mContentView.getLayoutParams().width = parentWidth;
        menuView = (ViewGroup) mWrapper.getChildAt(1);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //第一次启动时执行该方法。
        if (changed) {
            close();
            mMenuViewWidth = menuView.getMeasuredWidth();
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
                scroller.abortAnimation();
                scroller.forceFinished(true);
                mMenuViewWidth = menuView.getMeasuredWidth();
                //按下颜色变化
                setPressColor();
                //点击的时候判断当前是否有item展开，有则缩回去
                if (closeIfHasExpanding()) {
                    if (activeMode == ActiveMode.noActiveWhileOtherOpen) hasColseOnDown = true;

                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                setDefaultColor();
                break;
            case MotionEvent.ACTION_UP:
                setDefaultColor();
                int scrollX = getScrollX();
                if (scrollX > 0) { //布局已经发生移动
                    if (scrollX >= touchSlop) {//达到阀值
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

                    if (activeMode == ActiveMode.noActiveWhileOtherOpen) {
                        if (hasColseOnDown) {
                            hasColseOnDown = false;
                            break;
                        }
                    }

                    if (mClickListener != null) {
                        mClickListener.onClick(v);
                    }

                }
                break;
        }

        return onTouchEvent(event);
    }


    private void prepareScroll(int fx, int fy) {
        int dx = fx - getScrollX();
        int dy = fy - getScrollY();
        scroller.startScroll(getScrollX(), getScrollY(), dx, dy);
    }


    private void setDefaultColor() {
        if (ColorValue == 0) return;
        setBackgroundDrawable(defalutBackgroundDraw);
    }

    private void setPressColor() {
        if (ColorValue == 0) return;
        setBackgroundColor(ColorValue);
    }


    //在onTouch中执行onTouchEvent
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return onTouchEvent(event, v);
    }


    public void close() {
        prepareScroll(0, 0);
        expanded = false;
        mRecorder = null;
        scrollTo(0, 0);
    }

    //打开一个之前先要判断上一个是否已关闭。
    public void open() {
        prepareScroll(mMenuViewWidth, 0);
        expanded = true;
        if (mRecorder != null&&mRecorder.get()!=null) {
            mRecorder.get().close();
        }
        mRecorder = new WeakReference<>(this);
        scrollTo(mMenuViewWidth, 0);
    }

    /**
     * 删除记录
     */
    public void delete() {
        if (mRecorder!=null&&mRecorder.get() == this) mRecorder = null;
    }

    public boolean closeIfHasExpanding() {
        if (mRecorder != null && mRecorder.get() != this) {
            mRecorder.get().close();
            return true;
        }
        return false;
    }


    public void setActiveWhileOtherOpen(ActiveMode activeMode) {
        this.activeMode = activeMode;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mClickListener = l;
    }

    /**
     * 设置压下去的颜色
     */
    public void setPressColor(String colorvalue) {
        ColorValue = Color.parseColor(colorvalue);
    }

    public enum ActiveMode {
        activeWhileOtherOpen, // 当点击的某个Item的时候假如有其他Item正在展开，则关掉其它Item，并且本Item的点击事件响应
        noActiveWhileOtherOpen //当点击的某个Item的时候假如有其他Item正在展开，则关掉其它Item，但是本Item的点击事件不响应，如QQ的消息列表
    }

    private boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }


    @Override
    public void computeScroll() {
        if (scroller!=null&&scroller.computeScrollOffset()) {
            int currX = scroller.getCurrX();
            int currY = scroller.getCurrY();
            scrollTo(currX, currY);
            postInvalidate();
        }
    }
}
