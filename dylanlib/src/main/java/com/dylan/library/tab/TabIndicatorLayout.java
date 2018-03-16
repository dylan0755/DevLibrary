package com.dylan.library.tab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Dylan on 2016/8/31.
 */
public class TabIndicatorLayout extends LinearLayout {
    private Context mContext;
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
    private int mTabWidth;
    private int mTriangleWidth;
    private int mTriangleHeight;
    private int widthMeasureSpec,  heightMeasureSpec;
    private ViewPager mViewPager;
    private List<String> mTitleList;
    private int mTabVisiableCount;
    private int currentPosition;
    private float downX;
    private int mTouchSlop;
    private boolean isDragged;
    private boolean isClick;//区分是点击tab还是滑动viewpager，因为点击的时候和滑动ViewPager的时候本身布局要随着移动
    private OverScroller mScroller;
    public static int SHAPE_TRIANGLE = 1;//三角形指示器
    public static int SHAPE_RECTANGLE = 2;//长方形指示器
    private int mShape = SHAPE_RECTANGLE;

    public TabIndicatorLayout(Context context) {
        this(context, null);

    }

    public TabIndicatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        mContext = context;
        mScroller = new OverScroller(context);
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();

        mTitleList = new ArrayList<String>();
        initPaint();
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mShape == SHAPE_TRIANGLE) {
            initTriangle();
            // 初始时指示器的偏移量，Tab的正下方
            mInitTranslationX = mTabWidth / 2 - mTriangleWidth / 2;
        } else {
            initRetangle();
            mInitTranslationX = mTabWidth / 2 - mIndicatorWidht / 2;
        }


    }

    private void initRetangle() {
        mPath = new Path();
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.moveTo(0, 0);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasureWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.widthMeasureSpec=widthMeasureSpec;
        this.heightMeasureSpec=heightMeasureSpec;
        setTabWidth();
    }

    /**
     * 设置tab的宽度
     */
    private void setTabWidth() {
        if (mTitleList.size() > MAX_VISIABLE_TAB_COUNT) {
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
        int childCount=getChildCount();
        int newWidthMeasureSpec= MeasureSpec.makeMeasureSpec(mTabWidth, MeasureSpec.getMode(widthMeasureSpec));
        for (int i=0;i<childCount;i++){
            TabItem tabItem= (TabItem) getChildAt(i);
            if (TAB_TEXT_SIZE != 0) tabItem.setTextSize(TAB_TEXT_SIZE);
            if (i == currentPosition) tabItem.setTextColor(COLOR_TEXT_SELECT);
            else
                tabItem.setTextColor(COLOR_TEXT_NORMAL);
            //先把大小，颜色 设置了再调用测量
            tabItem.measure(newWidthMeasureSpec,heightMeasureSpec);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mTitleList == null || mTitleList.size() <= MAX_VISIABLE_TAB_COUNT)//tab数量不够多，不能产生滑动事件
            return super.dispatchTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDragged = false;
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float dex = event.getX() - downX;
                downX = event.getX();
                if (Math.abs(dex) > mTouchSlop) {
                    isDragged = true;
                    if (dex > 0) {//向右滑动
                        if (getScrollX() <= 0) {
                            setScrollX(0);
                            return true;
                        }
                    } else {
                        int scrollRange = (mTitleList.size() - (MAX_VISIABLE_TAB_COUNT - 1)) * mTabWidth;//向左滑最大的距离，最右边的tab全部显示出来
                        if (getScrollX() >= scrollRange) {
                            setScrollX(scrollRange);
                            return true;
                        }
                    }
                    scrollBy(-(int) dex, 0);
                    return true;
                }
                break;
        }
        if (isDragged) {//发生了滑动，则消费掉此事件，避免Tab的点击事件的产生
            return true;
        } else
            return super.dispatchTouchEvent(event);
    }

    public void setUpWidthViewPager(ViewPager viewpager) {
        this.mViewPager = viewpager;
        mViewPager.setOnPageChangeListener(new PageSelectedListener());

    }


    /**
     * 添加标签
     */
    public void addTabs(List<String> titleList) {
        if (titleList == null) return;
        removeAllViews();
        newTabs(titleList);
    }
    public void addTabs(String[] titles) {
        if (titles == null) return;
        newTabs(Arrays.asList(titles));
    }
    private void newTabs(List<String> titleList) {
        removeAllViews();
        mTitleList = titleList;
        if (mTitleList.size() >= MAX_VISIABLE_TAB_COUNT) {
            mTabVisiableCount = MAX_VISIABLE_TAB_COUNT;
        } else {
            mTabVisiableCount = mTitleList.size();
        }
        for (int i = 0; i < mTitleList.size(); i++) {
            String title = mTitleList.get(i);
            if (title == null || title.isEmpty()) return;
            TextView textView = new TextView(mContext);
            textView.setTag(String.valueOf(i));
            textView.setOnClickListener(new TabClickListener());
            textView.setGravity(Gravity.CENTER);
            textView.setText(title);
            if (COLOR_TEXT_NORMAL == 0) {//记录系统提供TextView的颜色
                COLOR_TEXT_NORMAL = textView.getCurrentTextColor();
            }
            if (i == 0) {
                setSelect(textView);
            }
            addView(textView);
        }
        if (mMeasureWidth != 0) {//可能在调用onMeasure的时候，addtabs还没有执行，但mTabWidth已经计算出来了
            setTabWidth();
        }
    }


    /**
     * 设置选中
     */
    private void setSelect(TextView textView) {
        textView.setTextColor(COLOR_TEXT_SELECT);
        //textView.getPaint().setFakeBoldText(true);//加粗
    }

    private void setSelectIndex(int position) {
        if (position == currentPosition) {
            if (mTabSelectListener != null) {
                mTabSelectListener.reSelected(position);
            }
        } else {
            //将选中的取消选中
            TextView tv = (TextView) getChildAt(currentPosition);
            tv.setTextColor(COLOR_TEXT_NORMAL);//恢复为初始原色
            if (mTabSelectListener != null) {
                mTabSelectListener.unSelect(currentPosition);
            }
            //记录新的
            TextView textView = (TextView) getChildAt(position);
            textView.setTextColor(COLOR_TEXT_SELECT);
            if (TAB_TEXT_SIZE!=0)textView.setTextSize(TAB_TEXT_SIZE+2);

            currentPosition = position;
            if (mTabSelectListener != null) {
                mTabSelectListener.onSelect(currentPosition);
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
                TextView tv = (TextView) getChildAt(i);
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
            TextView tv = (TextView) getChildAt(i);
            tv.setTextColor(COLOR_TEXT_NORMAL);
            if (i == currentPosition) {
                tv.setTextColor(COLOR_TEXT_SELECT);
                tv.getPaint().setFakeBoldText(true);//加粗
            }
        }
    }

    /**
     * @param color 选中标签的颜色
     */
    public void setTabSelectColor(int color) {
        this.COLOR_TEXT_SELECT = color;
        TextView tv = (TextView) getChildAt(currentPosition);
        if (tv != null) {
            tv.setTextColor(COLOR_TEXT_SELECT);
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
                int position = Integer.parseInt(strPostion);
                setSelectIndex(position);
                setIndicatorOffset(position, 0);
                isClick = true;

                //点击Tab的时候TabLayout也跟着滚动
                if (mTabVisiableCount > 0 && position >= (mTabVisiableCount - 2) && getChildCount() > mTabVisiableCount) {
                    if (position != mTitleList.size() - 1) {//不是最后一个则滚动
                        int px = (position - (mTabVisiableCount - 2)) * mTabWidth;
                        mScroller.startScroll(px, 0, mTabWidth, 0, 500);//点击Tab的时候若需要滑动，则滑动的距离是一个Tab的宽度
                        invalidate();
                    }
                }

                if (mViewPager != null && mViewPager.getAdapter() != null) {
                    mViewPager.setCurrentItem(position);
                }
            }


        }
    }


    class PageSelectedListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (!isClick) {
                setIndicatorOffset(position, positionOffset);
                //当滑动ViewPager的时候，布局需要跟着滑动
                if (mTabVisiableCount > 0 && position >= (mTabVisiableCount - 2) && getChildCount() > mTabVisiableCount) {
                    int px = ((position - (mTabVisiableCount - 2)) * mTabWidth + (int) (mTabWidth * positionOffset));
                    // Log.e( "onPageScrolled: ","px "+px );
                    scrollTo(px, 0);
                }
            }

            if (mPageChangeListener != null) {
                mPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }


        @Override
        public void onPageSelected(int position) {
            setSelectIndex(position);
            if (mPageChangeListener != null) {
                mPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == 0) {  //state 的变化  1,2,0       0的时候完全停止滚动
                isClick = false;
            }
            if (currentPosition <= (mTabVisiableCount - 2)) {
                scrollTo(0, 0);
            }
            if (mPageChangeListener != null) {
                mPageChangeListener.onPageScrollStateChanged(state);
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
        void onSelect(int position);

        void unSelect(int position);

        void reSelected(int position);
    }

    public void setTabSelectListener(TabSelectListener listener) {
        mTabSelectListener = listener;
    }

    //ViewPage滑动监听，提供对外
    private OnPageChangeListener mPageChangeListener;

    public interface OnPageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    public void setPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener = listener;
    }
}
