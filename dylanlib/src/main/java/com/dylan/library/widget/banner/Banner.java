package com.dylan.library.widget.banner;

import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2022/03/25
 * Desc:
 */
public class Banner extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private static final long DEFAULT_AUTO_TIME = 2500;
    private static final int NORMAL_COUNT = 2;

    private ViewPager.OnPageChangeListener outerPageChangeListener;
    private OnPageItemClickListener onPageClickListener;
    private HolderCreator holderCreator;
    private BannerViewPager viewPager;
    private PagerAdapter adapter;
    private List<View> views;
    private Indicator indicator;
    private boolean isAutoPlay = true;
    private long autoTurningTime = DEFAULT_AUTO_TIME;

    /**
     * 虚拟当前页 1表示真实页数的第一页
     */
    private int currentPage;
    /**
     * 实际数量
     */
    private int realCount;
    /**
     * 需要的数量
     */
    private int needCount;
    /**
     * 额外的页数
     */
    private int needPage = NORMAL_COUNT;
    /**
     * 额外的页数是往最左边添加和最右边添加，该变量记录一边添加的数量
     */
    private int sidePage;

    public Banner(Context context) {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        views = new ArrayList<>();
        initViews(context);
    }

    private void initViews(Context context) {
        viewPager = new BannerViewPager(context);
        viewPager.setLayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        viewPager.setClipToPadding(false);
        viewPager.addOnPageChangeListener(this);
        addView(viewPager);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isAutoPlay()) {
            startTurning();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isAutoPlay()) {
            stopTurning();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realPosition = toRealPosition(position);
        if (outerPageChangeListener != null) {
            outerPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
        }
        if (indicator != null) {
            indicator.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        //解决多次重复回调onPageSelected问题,暂时这么处理
        boolean resetItem = currentPage == sidePage - 1 || currentPage == needCount - (sidePage - 1) || (position != currentPage && needCount - currentPage == sidePage);
        currentPage = position;
        if (resetItem) return;
        int realPosition = toRealPosition(position);
        if (outerPageChangeListener != null) {
            outerPageChangeListener.onPageSelected(realPosition);
        }
        if (indicator != null) {
            indicator.onPageSelected(realPosition);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (outerPageChangeListener != null) {
            outerPageChangeListener.onPageScrollStateChanged(state);
        }
        if (indicator != null) {
            indicator.onPageScrollStateChanged(state);
        }
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            //一屏一页0是真实数据的最后一页，一屏三页1是真实数据的最后一页，实际数量 + 虚拟当前页 = 实际最后一页索引
            if (currentPage == sidePage - 1) {
                viewPager.setCurrentItem(realCount + currentPage, false);
            }
            //如果是最后一页，那么真实数据的第一页，直接设置到真实数据的第一页
            else if (currentPage == needCount - sidePage) {
                viewPager.setCurrentItem(sidePage, false);
            }
        }
    }

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlay()) {
                currentPage++;
                if (currentPage == realCount + sidePage + 1) {
                    viewPager.setCurrentItem(sidePage, false);
                    post(task);
                } else {
                    viewPager.setCurrentItem(currentPage);
                    postDelayed(task, autoTurningTime);
                }
            }
        }
    };

    private class BannerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return realCount > 1 ? needCount : realCount;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = views.get(position);
            if (onPageClickListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int toRealPosition = toRealPosition(position);
                        onPageClickListener.onPageItemClick(v, toRealPosition);
                    }
                });
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isAutoPlay()) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                startTurning();
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopTurning();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void startPager(int startPosition) {
        if (adapter == null) {
            adapter = new BannerAdapter();
        }
        viewPager.setAdapter(adapter);
        currentPage = startPosition + sidePage;
        viewPager.setScrollable(realCount > 1);
        viewPager.setFirstLayoutToField(false);
        viewPager.setFocusable(true);
        viewPager.setCurrentItem(currentPage);
        if (indicator != null) {
            indicator.initIndicatorCount(realCount);
        }
        if (isAutoPlay()) {
            startTurning();
        }
    }

    private void createImages(List<?> items) {
        this.views.clear();
        if (items == null || items.size() == 0 || holderCreator == null) {
            realCount = 0;
            needCount = 0;
            return;
        }
        realCount = items.size();
        sidePage = needPage / NORMAL_COUNT;
        needCount = realCount + needPage;
        for (int i = 0; i < needCount; i++) {
            int toRealPosition = toRealPosition(i);
            View view = holderCreator.createView(getContext(), toRealPosition, items.get(toRealPosition));
            views.add(view);
        }
    }

    private int toRealPosition(int position) {
        int realPosition = 0;
        if (realCount != 0) {
            realPosition = (position - sidePage) % realCount;
        }
        if (realPosition < 0) {
            realPosition += realCount;
        }
        return realPosition;
    }

    /*--------------- 下面是对外暴露的方法 ---------------*/

    /**
     * 设置一屏多页
     *
     * @param multiWidth 左右页面露出来的宽度一致
     * @param pageMargin >0 item与item之间的宽度， <0 item与item之间重叠宽度，小于0 魅族效果banner效果
     */
    public Banner setPageMargin(int multiWidth, int pageMargin) {
        return setPageMargin(multiWidth, multiWidth, pageMargin);
    }

    /**
     * 设置一屏多页
     *
     * @param leftWidth  左边页面显露出来的宽度
     * @param rightWidth 右边页面露出来的宽度
     * @param pageMargin >0 item与item之间的宽度， <0 item与item之间重叠宽度
     */
    public Banner setPageMargin(int leftWidth, int rightWidth, int pageMargin) {
        viewPager.setPageMargin(pageMargin);
        viewPager.setOverlapStyle(pageMargin < 0);
        viewPager.setPadding(leftWidth + Math.abs(pageMargin), viewPager.getPaddingTop(), rightWidth + Math.abs(pageMargin), viewPager.getPaddingBottom());
        viewPager.setOffscreenPageLimit(2);
        needPage = NORMAL_COUNT + NORMAL_COUNT;
        return this;
    }

    public Banner setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        viewPager.setPageTransformer(reverseDrawingOrder, transformer);
        return this;
    }

    public Banner setAutoTurningTime(long autoTurningTime) {
        this.autoTurningTime = autoTurningTime;
        return this;
    }

    /**
     * {@link ViewPager.SimpleOnPageChangeListener}
     */
    public Banner setOuterPageChangeListener(ViewPager.OnPageChangeListener outerPageChangeListener) {
        this.outerPageChangeListener = outerPageChangeListener;
        return this;
    }

    /**
     * 设置viewpager的切换时长
     */
    public Banner setPagerScrollDuration(int pagerScrollDuration) {
        viewPager.setPagerScrollDuration(pagerScrollDuration);
        return this;
    }

    public Banner setOffscreenPageLimit(int limit) {
        viewPager.setOffscreenPageLimit(limit);
        return this;
    }

    /**
     * 设置banner圆角 api21以上
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Banner setRoundCorners(final float radius) {
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
        setClipToOutline(true);
        return this;
    }

    /**
     * 是否自动轮播 大于1页轮播才生效
     */
    public Banner setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
        if (isAutoPlay && realCount > 1) {
            startTurning();
        }
        return this;
    }

    public Banner setIndicator(Indicator indicator) {
        return setIndicator(indicator, true);
    }

    /**
     * 设置indicator，支持在xml中创建
     *
     * @param attachToRoot true 添加到banner布局中
     */
    public Banner setIndicator(Indicator indicator, boolean attachToRoot) {
        if (this.indicator != null) {
            removeView(this.indicator.getView());
        }
        if (indicator != null) {
            this.indicator = indicator;
            if (attachToRoot) {
                addView(this.indicator.getView(), this.indicator.getParams());
            }
        }
        return this;
    }

    public Banner setHolderCreator(HolderCreator holderCreator) {
        this.holderCreator = holderCreator;
        return this;
    }

    public Banner setOnPageClickListener(OnPageItemClickListener onPageClickListener) {
        this.onPageClickListener = onPageClickListener;
        return this;
    }

    /**
     * @param items         数据集
     * @param startPosition 开始位置 真实索引
     */
    public void setPages(List<?> items, int startPosition) {
        createImages(items);
        startPager(startPosition);
    }

    public void setPages(List<?> items) {
        setPages(items, 0);
    }

    /**
     * 是否正在轮播
     */
    public boolean isAutoPlay() {
        return isAutoPlay && realCount > 1;
    }

    /**
     * 返回真实位置
     */
    public int getCurrentPager() {
        int position = toRealPosition(currentPage);
        return Math.max(position, 0);
    }

    public void startTurning() {
        stopTurning();
        postDelayed(task, autoTurningTime);
    }

    public void stopTurning() {
        removeCallbacks(task);
    }
}
