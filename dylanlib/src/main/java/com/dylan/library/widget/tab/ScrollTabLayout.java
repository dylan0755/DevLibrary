package com.dylan.library.widget.tab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2016/8/31.
 */

@Deprecated
public class ScrollTabLayout extends LinearLayout {
    private int TAB_TEXT_SIZE;
    private int COLOR_TEXT_NORMAL;
    private int COLOR_TEXT_SELECT = Color.BLACK;
    private int mIndicatorColor = Color.BLACK;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private Path mIndicatorPath;
    private Paint mIndicatorPaint;//三角形画笔
    private int mMeasureWidth;
    private int beginScrollLeftMargin;//距离左边多少距离时开始滚动
    private int beginScrollTabIndex;
    private float mTranslationX;
    private int mMaxScrollRange;
    private int mTriangleWidth;
    private int mTriangleHeight;
    private int widthMeasureSpec;
    private int heightMeasureSpec;
    private ViewPager mViewPager;
    private List<TabItem> mTabItemList;
    private int mVisibleTabCount;
    private int currentPosition;
    private float downX;
    private int mTouchSlop;
    private boolean isDragged;
    private boolean syncColor = true;
    private boolean hasMeasure;
    private OverScroller mScroller;
    public static int SHAPE_TRIANGLE = 1;//三角形指示器
    public static int SHAPE_RECTANGLE = 2;//长方形指示器
    private int mShape = SHAPE_RECTANGLE;
    private TabClickListener tabClickListener;
    private PageSelectedListener pageSelectedListener;
    private int mMinimumVelocity;
    private VelocityTracker mVelocityTracker;
    private int tabMargin;
    private int mInitTranslationX;

    public ScrollTabLayout(Context context) {
        this(context, null);

    }

    public ScrollTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        mScroller = new OverScroller(context);
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mTabItemList = new ArrayList<>();
        tabClickListener = new TabClickListener();
        tabMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        mIndicatorWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        mIndicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
        initPaint();
    }


    public void setIndicatorShap(int shape) {
        if (shape != SHAPE_RECTANGLE && shape != SHAPE_TRIANGLE) {
            shape = SHAPE_RECTANGLE;
        }
        mShape = shape;
    }

    public void setTabMargin(int margin) {
        tabMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getContext().getResources().getDisplayMetrics());
    }

    /**
     * @param color 指示器颜色
     */
    public void setIndicatorColor(int color) {
        mIndicatorPaint.setColor(color);
        invalidate();
    }

    public void setIndicatorWidth(int indicatorWidth) {
        mIndicatorWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorWidth, getContext().getResources().getDisplayMetrics());
    }

    public void setIndicatorHeight(int indicatorHeight) {
        mIndicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, getContext().getResources().getDisplayMetrics());
        mIndicatorPaint.setStrokeWidth(mIndicatorHeight);
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        mIndicatorPaint = new Paint();
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setStyle(Paint.Style.FILL);//实心
        mIndicatorPaint.setStrokeWidth(mIndicatorHeight);
        mIndicatorPaint.setColor(mIndicatorColor);
        mIndicatorPaint.setPathEffect(new CornerPathEffect(3));//边缘圆角

    }


    /**
     * 绘制指示器
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mTranslationX, getHeight());
        canvas.drawPath(mIndicatorPath, mIndicatorPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasureWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.widthMeasureSpec = widthMeasureSpec;
        this.heightMeasureSpec = heightMeasureSpec;
        measureTab();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getChildCount() == 0) return;
        mInitTranslationX = 0;
        if (mShape == SHAPE_TRIANGLE) {
            initTriangle();
            // 初始时指示器的偏移量，Tab的正下方
            mInitTranslationX = getChildAt(0).getMeasuredWidth() / 2 - mTriangleWidth / 2;
        } else {
            initRetangle();
            mInitTranslationX = (getChildAt(0).getMeasuredWidth() / 2 - mIndicatorWidth / 2);
        }
        mTranslationX += mInitTranslationX;
    }

    private void initRetangle() {
        mIndicatorPath = new Path();
        mIndicatorPaint.setStyle(Paint.Style.STROKE);
        mIndicatorPath.moveTo(0, 0);
        mIndicatorPath.lineTo(mIndicatorWidth, 0);
    }

    /**
     * 初始化三角形指示器
     */
    private void initTriangle() {
        mIndicatorPath = new Path();
        mTriangleWidth = (mMeasureWidth / 3) / 8;
        mTriangleHeight = mTriangleWidth / 2;
        mIndicatorPath.moveTo(0, 0);
        mIndicatorPath.lineTo(mTriangleWidth, 0);
        mIndicatorPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mIndicatorPath.close();//闭合曲线形成三角形
    }


    /**
     * 测量tab的宽度
     */
    private void measureTab() {
        int childCount = getChildCount();
        if (childCount == 0) return;
        int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int totalRange = 0;
        for (int i = 0; i < childCount; i++) {
            TabItem tabItem = (TabItem) getChildAt(i);
            if (TAB_TEXT_SIZE != 0) tabItem.setTextSize(TAB_TEXT_SIZE);
            if (i == currentPosition) {
                tabItem.setTextColor(COLOR_TEXT_SELECT);
            } else {
                tabItem.setTextColor(COLOR_TEXT_NORMAL);
            }

            tabItem.measure(newWidthMeasureSpec, heightMeasureSpec);
            int itemWidth = tabItem.getMeasuredWidth();
            //指示器长度不能超过Tab长度
            if (mIndicatorWidth > itemWidth) mIndicatorWidth = itemWidth;
            totalRange += itemWidth;
            if (totalRange >= mMeasureWidth && mVisibleTabCount == 0) {
                mVisibleTabCount = (i + 1);
                if (i - 3 > 0) beginScrollTabIndex = i - 3;
                else if (i - 2 > 0) beginScrollTabIndex = i - 2;
                else if (i - 1 > 0) beginScrollTabIndex = i - 1;
                else beginScrollTabIndex = i;
            }
        }
        if (totalRange > mMeasureWidth) {
            mMaxScrollRange = totalRange - mMeasureWidth;
        }


    }


    public void syncTextIconColor(boolean bl) {
        syncColor = bl;
    }


    public TabItem newTab() {
        TabItem tabItem = new TabItem(getContext());
        return tabItem;
    }

    /**
     * 添加标签
     */
    public ScrollTabLayout addTab(TabItem tabItem) {
        mTabItemList.add(tabItem);
        return this;
    }

    public ScrollTabLayout addTabs(List<TabItem> tabItems) {
        if (tabItems == null || tabItems.isEmpty()) return this;
        mTabItemList.addAll(tabItems);
        return this;
    }

    public int getTabCount() {
        return mTabItemList.size();
    }


    public void create() {
        removeAllViews();
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        for (int i = 0; i < mTabItemList.size(); i++) {
            TabItem tabItem = mTabItemList.get(i);
            tabItem.setPadding(tabMargin / 2, 0, tabMargin / 2, 0);
            tabItem.setLayoutParams(lp);
            tabItem.setTag(String.valueOf(i));
            tabItem.setOnClickListener(tabClickListener);
            tabItem.setGravity(Gravity.CENTER);
            if (COLOR_TEXT_NORMAL == 0) {
                COLOR_TEXT_NORMAL = tabItem.getCurrentTextColor();
            }
            if (syncColor) {
                tabItem.setIconColorFilter(COLOR_TEXT_NORMAL);
            }
            if (i == 0) {
                tabItem.setTextColor(COLOR_TEXT_SELECT);
            }
            addView(tabItem);
        }
        if (mMeasureWidth != 0) {//可能在调用onMeasure的时候，addtabs还没有执行，但mTabWidth已经计算出来了
            measureTab();
        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (EmptyUtils.isEmpty(mTabItemList) || mMaxScrollRange <= 0)//tab数量不够多，不能产生滑动事件
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
                if (!isDragged && Math.abs(dex) >= mTouchSlop / 2) {
                    isDragged = true;
                }

                if (isDragged) {
                    //Logger.e("isDragged dex=" + dex);
                    if (dex > 0) {//向右滑动
                        if (getScrollX() <= 0) {
                            setScrollX(0);
                            break;
                        }
                    } else {
                        if (getScrollX() >= mMaxScrollRange) {
                            setScrollX(mMaxScrollRange);
                            break;
                        }
                    }
                    scrollBy(-(int) dex, 0);
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

        }
        if (isDragged) {//发生了滑动，则消费掉此事件，避免Tab的点击事件的产生
            return true;
        } else
            return super.dispatchTouchEvent(event);

    }


    private void fling(int velocityX, int velocityY) {
        int minScrollRangeX = 0;
        int maxScrollRangeX = mMaxScrollRange;
        int minScrollRangeY = 0;
        int maScrollRangeY = 0;
        int startX = getScrollX();
        int startY = getScrollY();
        mScroller.fling(startX, startY, -velocityX, velocityY, minScrollRangeX, maxScrollRangeX, minScrollRangeY, maScrollRangeY);

    }


    public void setUpWidthViewPager(ViewPager viewpager) {
        this.mViewPager = viewpager;
        pageSelectedListener=new PageSelectedListener();
        mViewPager.addOnPageChangeListener(pageSelectedListener);

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
            if (syncColor) {
                item.setIconColorFilter(COLOR_TEXT_NORMAL);
            }


            if (mTabSelectListener != null) {
                mTabSelectListener.unSelect(currentPosition, item);
            }
            //记录新的
            TabItem tabItem = (TabItem) getChildAt(position);
            tabItem.setTextColor(COLOR_TEXT_SELECT);
            if (syncColor) {
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
        for (int i = 0; i < getChildCount(); i++) {
            TabItem tv = (TabItem) getChildAt(i);
            tv.setTextSize(TAB_TEXT_SIZE);
        }
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
//        mTranslationX = mTabWidth * (position + positionOffset);//指示器的偏移量
//        invalidate();

        if (positionOffset == 0) {
            mTranslationX = 0;//指示器的偏移量
            if (position == 0) {
                mTranslationX = mInitTranslationX + positionOffset;
            } else {
                for (int i = 0; i <= position; i++) {
                    if (i != position) {
                        mTranslationX += getChildAt(i).getMeasuredWidth();
                    } else {
                        mTranslationX += getChildAt(i).getMeasuredWidth() / 2 - mIndicatorWidth / 2;
                    }
                }
            }
        }
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
                    pageSelectedListener.recordPosition=mViewPager.getCurrentItem();
                    mViewPager.setCurrentItem(clickPosition);
                } else {
                    setSelectIndex(clickPosition);
                    setIndicatorOffset(clickPosition, 0);
                }
            }


        }
    }


    class PageSelectedListener implements ViewPager.OnPageChangeListener {
        //记录上一次滑动的positionOffsetPixels值
        private int recordPosition;
        private boolean isDragging;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //当前偏移量
            int currentOffset = 0;
            for (int i = 0; i < position; i++) {
                currentOffset += getChildAt(i).getMeasuredWidth();
            }
            //指示器偏移量
            int indicatorPadding=0;
            Logger.e("recordPosition="+recordPosition+" position="+position);

            if (!isDragging){

            }

            if(positionOffset!=0&&recordPosition ==position&&position+1<getTabCount()){//向左滑，切换下一个
                indicatorPadding = getChildAt(position + 1).getMeasuredWidth() / 2 - mIndicatorWidth / 2;
            }else if (positionOffset!=0&&position<recordPosition){
                indicatorPadding = getChildAt(position).getMeasuredWidth() / 2 - mIndicatorWidth / 2;
            }


            if (positionOffset==0){
                indicatorPadding = getChildAt(position ).getMeasuredWidth() / 2 - mIndicatorWidth / 2;
            }


            int viewWidth = getChildAt(position).getMeasuredWidth();
            mTranslationX = currentOffset+indicatorPadding  + positionOffset * viewWidth;


            //当滑动ViewPager的时候，TabLayout需要跟着滑动
        //   linkageTabLayout();
            //刷新
            invalidate();

        }


        @Override
        public void onPageSelected(int position) {
            setSelectIndex(position);
            isDragging=false;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
             if (state==ViewPager.SCROLL_STATE_DRAGGING){
                 recordPosition=mViewPager.getCurrentItem();
                 isDragging=true;
             }else if (state==ViewPager.SCROLL_STATE_SETTLING){
                 isDragging=false;
             }
        }
    }

    //TabLayout 随ViewPager 联动
    private void linkageTabLayout() {
        if (mVisibleTabCount > 0) {
            if (beginScrollLeftMargin == 0) {
                beginScrollLeftMargin = getChildAt(beginScrollTabIndex).getLeft();
            }

            int scrollRange = (int) (mTranslationX - beginScrollLeftMargin);
            if (scrollRange < 0) scrollRange = 0;
            if (scrollRange > mMaxScrollRange) scrollRange = mMaxScrollRange;
            scrollTo(scrollRange, 0);
        }
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
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
