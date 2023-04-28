package com.dylan.library.widget.tab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2016/8/31.
 */
public class HorizontalScrollTabLayout extends LinearLayout {
    private int TAB_TEXT_SIZE;
    private int COLOR_TEXT_NORMAL;
    private int COLOR_TEXT_SELECT = Color.BLACK;
    private int mIndicatorColor = Color.BLACK;
    private int mIndicatorWidht;
    private Path mPath;
    private Paint mPaint;//三角形画笔
    private int mMeasureWidth;
    private int MAX_VISIABLE_TAB_COUNT = 5;//默认一行可见的tab数量
    private float mInitTranslationX;
    private float mTranslationX;
    private int mMaxScrollRange;
    private int mTabWidth;
    private int mTriangleWidth;
    private int mTriangleHeight;
    private int widthMeasureSpec;
    private int heightMeasureSpec;
    private ViewPager mViewPager;
    private List<TabItem> mTabItemList;
    private int mTabVisiableCount;
    private int currentPosition;
    private float downX;
    private int mTouchSlop;
    private boolean isDragged;
    private boolean syncColor=true;
    private OverScroller mScroller;
    public static int SHAPE_TRIANGLE = 1;//三角形指示器
    public static int SHAPE_RECTANGLE = 2;//长方形指示器
    private int mShape = SHAPE_RECTANGLE;
    private int indicatorRightMargin;
    private int indicatorLeftMargin;
    private TabClickListener tabClickListener;
    private int mMinimumVelocity;
    private VelocityTracker mVelocityTracker;


    public HorizontalScrollTabLayout(Context context) {
        this(context, null);

    }

    public HorizontalScrollTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        mScroller = new OverScroller(context);
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mTabItemList = new ArrayList<TabItem>();
        initPaint();
        tabClickListener = new TabClickListener();
    }

    public void setMaxVisiableCount(int count) {
        MAX_VISIABLE_TAB_COUNT = count;

    }

    public void setIndicatorShap(int shape) {
        if (shape != SHAPE_RECTANGLE && shape != SHAPE_TRIANGLE) {
            shape = SHAPE_RECTANGLE;
        }
        mShape = shape;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);//实心
        mPaint.setStrokeWidth(15);
        mPaint.setColor(mIndicatorColor);
        mPaint.setPathEffect(new CornerPathEffect(3));//边缘圆角

    }

    public void setIndicatorRightMargin(int rightMargin) {
        indicatorRightMargin= (int) (getResources().getDisplayMetrics().density*rightMargin+0.5f);
    }

    public void setIndicatorLeftMargin(int leftMargin){
        indicatorLeftMargin= (int) (getResources().getDisplayMetrics().density*leftMargin+0.5f);
    }

    /**
     * 绘制指示器
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mInitTranslationX + mTranslationX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasureWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.widthMeasureSpec = widthMeasureSpec;
        this.heightMeasureSpec = heightMeasureSpec;
        setTabWidth();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mShape == SHAPE_TRIANGLE) {
            initTriangle();
            // 初始时指示器的偏移量，Tab的正下方
            mInitTranslationX = mTabWidth / 2 - mTriangleWidth / 2;
        } else {
            initRetangle();
            mInitTranslationX = (mTabWidth / 2 - mIndicatorWidht / 2) - indicatorRightMargin;
        }
    }

    private void initRetangle() {
        mPath = new Path();
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.moveTo(indicatorLeftMargin, 0);
        mIndicatorWidht = mTabWidth / 2;
        mPath.lineTo(mIndicatorWidht, 0);
    }

    /**
     * 初始化三角形指示器
     */
    private void initTriangle() {
        mPath = new Path();
        mTriangleWidth = (mMeasureWidth / 3) / 8;
        mTriangleHeight = mTriangleWidth / 2;
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();//闭合曲线形成三角形
    }


    /**
     * 设置tab的宽度
     */
    private void setTabWidth() {
        if (mTabItemList.size() > MAX_VISIABLE_TAB_COUNT) {
            mTabWidth = (int) (mMeasureWidth / (MAX_VISIABLE_TAB_COUNT - 0.5));//如果tab很多的情况下，mTabWith的宽度不能是平均值，因为需要看到最后一个有半个Tab露出来
        } else {//除以实际的Tab的数量
            if (mTabVisiableCount != 0) {
                mTabWidth = mMeasureWidth / mTabVisiableCount;
            } else {
                mTabWidth = mMeasureWidth / MAX_VISIABLE_TAB_COUNT;
            }
        }

        //手动设置子View的宽度，  不要用  getChildAt(index).setLayoutParam的方式
        // 因为有横竖屏切换的时候，setLayoutParam的无效
        int childCount = getChildCount();
        mMaxScrollRange = (mTabItemList.size() - (MAX_VISIABLE_TAB_COUNT - 1)) * mTabWidth;//向左滑最大的距离，最右边的tab全部显示出来;
        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mTabWidth, MeasureSpec.getMode(widthMeasureSpec));
        for (int i = 0; i < childCount; i++) {
            TabItem tabItem = (TabItem) getChildAt(i);
            if (TAB_TEXT_SIZE != 0) tabItem.setTextSize(TAB_TEXT_SIZE);
            if (i == currentPosition) tabItem.setTextColor(COLOR_TEXT_SELECT);
            else
                tabItem.setTextColor(COLOR_TEXT_NORMAL);
            //先把大小，颜色 设置了再调用测量
            tabItem.measure(newWidthMeasureSpec, heightMeasureSpec);
        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mTabItemList == null || mTabItemList.size() <= MAX_VISIABLE_TAB_COUNT)//tab数量不够多，不能产生滑动事件
            return super.dispatchTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScroller.abortAnimation();
                isDragged = false;
                downX = event.getRawX();
                mVelocityTracker = VelocityTracker.obtain();
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                float dex = event.getRawX() - downX;
                downX = event.getRawX();
                if (!isDragged && Math.abs(dex) > mTouchSlop) {
                    isDragged = true;
                }

                if (isDragged) {
                    if (dex > 0) {//向右滑动
                        if (getScrollX() <= 0) {
                            setScrollX(0);
                            return true;
                        }
                    } else {
                        if (getScrollX() >= mMaxScrollRange) {
                            setScrollX(mMaxScrollRange);
                            return true;
                        }
                    }
                    scrollBy(-(int) dex, 0);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                float vX = mVelocityTracker.getXVelocity();
                if (Math.abs(vX) > mMinimumVelocity) {
                    isDragged = true;
                    fling((int) vX, 0);
                }
                mVelocityTracker.recycle();
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                break;
        }
        if (isDragged) {//发生了滑动，则消费掉此事件，避免Tab的点击事件的产生
            return true;
        } else
            return super.dispatchTouchEvent(event);
    }

    private void fling(int velocityX, int velocityY) {
        int minScrollRangeX = 0;
        int maxScrollRangeX =mMaxScrollRange;
        int minScrollRangeY = 0;
        int maScrollRangeY = 0;
        int startX=getScrollX();
        int startY=getScrollY();
        mScroller.fling(startX, startY, -velocityX, velocityY, minScrollRangeX, maxScrollRangeX, minScrollRangeY, maScrollRangeY);

    }


    public void setUpWidthViewPager(ViewPager viewpager) {
        this.mViewPager = viewpager;
        mViewPager.addOnPageChangeListener(new PageSelectedListener());

    }

    public void syncTextIconColor(boolean bl){
        syncColor=bl;
    }


    public TabItem newTab() {
        TabItem tabItem = new TabItem(getContext());
        return tabItem;
    }

    /**
     * 添加标签
     */
    public HorizontalScrollTabLayout addTab(TabItem tabItem) {
        mTabItemList.add(tabItem);
        return this;
    }


    public void create() {
        removeAllViews();
        if (mTabItemList.size() >= MAX_VISIABLE_TAB_COUNT) {
            mTabVisiableCount = MAX_VISIABLE_TAB_COUNT;
        } else {
            mTabVisiableCount = mTabItemList.size();
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mTabWidth, LayoutParams.MATCH_PARENT);
        for (int i = 0; i < mTabItemList.size(); i++) {
            TabItem tabItem = mTabItemList.get(i);
            tabItem.setLayoutParams(lp);
            tabItem.setTag(String.valueOf(i));
            tabItem.setOnClickListener(tabClickListener);
            tabItem.setGravity(Gravity.CENTER);
            if (COLOR_TEXT_NORMAL == 0) {
                COLOR_TEXT_NORMAL = tabItem.getCurrentTextColor();
            }
            if (syncColor){
                tabItem.setIconColorFilter(COLOR_TEXT_NORMAL);
            }
            if (i == 0) {
                tabItem.setTextColor(COLOR_TEXT_SELECT);
            }
            addView(tabItem);
        }
        if (mMeasureWidth != 0) {//可能在调用onMeasure的时候，addtabs还没有执行，但mTabWidth已经计算出来了
            setTabWidth();
        }
    }

    public TabItem getTabItemAt(int index) {
        int childCount = getChildCount();
        if (childCount > 0 && index <= childCount - 1) {
            return (TabItem) getChildAt(index);
        }
        return null;
    }


    private void setSelectIndex(int position) {
        if (position == currentPosition) {

        } else {
            //将选中的取消选中
            TabItem item = (TabItem) getChildAt(currentPosition);
            item.setTextColor(COLOR_TEXT_NORMAL);//恢复为初始原色
            if (syncColor){
                item.setIconColorFilter(COLOR_TEXT_NORMAL);
            }



            if (mTabSelectListener != null) {
                mTabSelectListener.unSelect(currentPosition, item);
            }
            //记录新的
            TabItem tabItem = (TabItem) getChildAt(position);
            tabItem.setTextColor(COLOR_TEXT_SELECT);
            if (syncColor){
                tabItem.setIconColorFilter(COLOR_TEXT_SELECT);
            }

            currentPosition = position;
            if (mTabSelectListener != null) {
                mTabSelectListener.onSelect(currentPosition, tabItem);
            }
        }
    }


    public void setSelect(int position) {
        if (mViewPager != null) {
            if (mViewPager.getAdapter() != null) {
                mViewPager.setCurrentItem(position);
            } else {
                throw new IllegalStateException("ViewPager has not set PageAdapter before setSelect(postion)");
            }

        } else {
            setSelectIndex(position);
        }

    }

    public void setTabTextSize(int sp) {
        TAB_TEXT_SIZE = sp;
        if (mTabWidth != 0) {
            for (int i = 0; i < getChildCount(); i++) {
                TabItem tv = (TabItem) getChildAt(i);
                tv.setTextSize(TAB_TEXT_SIZE);
            }
            invalidate();
        }

    }

    /**
     * @param color 指示器颜色
     */
    public void setIndicatorColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    /**
     * @param color 标签颜色
     */
    public void setTabColor(int color) {
        this.COLOR_TEXT_NORMAL = color;
        for (int i = 0; i < getChildCount(); i++) {
            TabItem tabItem = (TabItem) getChildAt(i);
            tabItem.setTextColor(COLOR_TEXT_NORMAL);
            if (i == currentPosition) {
                tabItem.setTextColor(COLOR_TEXT_SELECT);
                tabItem.getTitleView().getPaint().setFakeBoldText(true);//加粗
            }
        }
    }

    /**
     * @param color 选中标签的颜色
     */
    public void setTabSelectColor(int color) {
        this.COLOR_TEXT_SELECT = color;
        TabItem tabItem = (TabItem) getChildAt(currentPosition);
        if (tabItem != null) {
            tabItem.setTextColor(COLOR_TEXT_SELECT);
        }

    }


    /**
     * 设置指示器的偏移
     */
    private void setIndicatorOffset(int position, float positionOffset) {
        mTranslationX = mTabWidth * (position + positionOffset);//指示器的偏移量
        invalidate();
    }


    class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String strPostion = (String) v.getTag();
            if (strPostion != null && !strPostion.isEmpty()) {
                int clickPosition = Integer.parseInt(strPostion);
                if (clickPosition == currentPosition) {
                    if (mTabSelectListener != null) {
                        mTabSelectListener.reSelected(clickPosition, (TabItem) v);
                    }
                    return;
                }

                if (mViewPager != null && mViewPager.getAdapter() != null) {
                    mViewPager.setCurrentItem(clickPosition);
                }else{
                    setSelectIndex(clickPosition);
                    setIndicatorOffset(clickPosition, 0);
                }
            }


        }
    }


    class PageSelectedListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setIndicatorOffset(position, positionOffset);
                //当滑动ViewPager的时候，布局需要跟着滑动
                if (mTabVisiableCount > 0 && position >= (mTabVisiableCount - 2) && getChildCount() > mTabVisiableCount) {
                    int px = ((position - (mTabVisiableCount - 2)) * mTabWidth + (int) (mTabWidth * positionOffset));
                    // Log.e( "onPageScrolled: ","px "+px );
                    scrollTo(px, 0);
                }

        }


        @Override
        public void onPageSelected(int position) {
            setSelectIndex(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (currentPosition <= (mTabVisiableCount - 2)) {
                scrollTo(0, 0);
            }

        }
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    //Tab点击监听
    private TabSelectListener mTabSelectListener;

    public interface TabSelectListener {
        void onSelect(int position, TabItem tabItem);

        void unSelect(int position, TabItem tabItem);

        void reSelected(int position, TabItem tabItem);
    }

    public void setTabSelectListener(TabSelectListener listener) {
        mTabSelectListener = listener;
    }


}
