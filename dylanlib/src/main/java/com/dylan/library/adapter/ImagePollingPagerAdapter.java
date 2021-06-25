package com.dylan.library.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.Scroller;

import com.dylan.library.utils.EmptyUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class ImagePollingPagerAdapter<T> extends PagerAdapter {
    private boolean isPlayAuto = true;
    private boolean isLock;
    private List<T> dataList;
    private Handler mHandler = new Handler();
    private ViewPager mViewPager;
    private int pollDuration;

    private List<ImageView> viewList = new ArrayList<>();

    public ImagePollingPagerAdapter(ViewPager pager, List<T> list) {
        this(pager, list, 5000, 1000);
    }


    public ImagePollingPagerAdapter(ViewPager pager, List<T> list, int PollingDuration, int scrollDuration) {
        this.pollDuration = PollingDuration;
        if (list.size() != 1 && list.size() < 3) {
            List<T> finalList = new ArrayList<>();
            finalList.addAll(list);
            for (T t : list) {
                finalList.add(t);
            }
            list = finalList;
        }
        if (EmptyUtils.isNotEmpty(list)) {
            setPlayAuto(list.size() > 1);
        }


        dataList = list;
        mViewPager = pager;
        if (EmptyUtils.isNotEmpty(dataList)) {
            ViewGroup.LayoutParams lpIv = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            for (int i = 0; i < dataList.size(); i++) {
                //图片
                ImageView ivPic = new ImageView(pager.getContext());
                ivPic.setLayoutParams(lpIv);
                ivPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                viewList.add(ivPic);
            }
        }
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            scrollerField.set(mViewPager, new PollingScroller(mViewPager.getContext(), scrollDuration));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int pageLimit = list.size() % 3 + 1;
        pager.setOffscreenPageLimit(pageLimit);
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lock();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    mHandler.removeCallbacksAndMessages(null);
                } else if (MotionEvent.ACTION_UP == event.getAction()) {
                    unLock();
                }
                return false;
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                final int finalPosition = position % viewList.size();
                ImagePollingPagerAdapter.this.onPageScrolled(finalPosition, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                final int finalPosition = position % viewList.size();
                ImagePollingPagerAdapter.this.onPageSelected(finalPosition);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        polling();
    }


    public List<T> getDataList() {
        return dataList;
    }


    public void setPlayAuto(boolean isPlayAuto) {
        this.isPlayAuto = isPlayAuto;
    }

    @Override
    public int getCount() {
        return viewList == null ? 0 : (isPlayAuto ? Integer.MAX_VALUE : viewList.size());
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final int finalPosition = position % viewList.size();
        ImageView ivPic = viewList.get(finalPosition);

        onBindItem(ivPic, finalPosition);
        ViewParent parent = ivPic.getParent();
        if (parent != null) {
            ViewGroup vg = (ViewGroup) parent;
            vg.removeView(ivPic);
        }
        container.addView(ivPic);
        return ivPic;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }

    public abstract void onBindItem(ImageView iv, int position);


    public abstract void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    public abstract void onPageSelected(int position);

    public void lock() {
        isLock = true;
        mHandler.removeCallbacksAndMessages(null);
    }

    public void unLock() {
        isLock = false;
        polling();
    }

    private void polling() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLock) {
                    int currentItem = mViewPager.getCurrentItem();
                    mViewPager.setCurrentItem(currentItem + 1);
                }
                polling();
            }
        }, pollDuration);

    }


    public class PollingScroller extends Scroller {
        private int mDuration = 1000;

        public PollingScroller(Context context, int duration) {
            super(context);
            mDuration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }


    public void release() {
        mHandler.removeCallbacksAndMessages(null);
    }

}
