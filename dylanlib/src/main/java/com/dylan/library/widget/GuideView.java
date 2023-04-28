package com.dylan.library.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dylan
 */
public class GuideView extends FrameLayout {
    private ViewPager mViewPager;
    private List<View> viewList;
    private Context mContext;
    private int currentIndex;
    private boolean Running;
    private ScaleUtil mScaleUtil;
    //indicatorLayout
    private LinearLayout indicatorLayout;
    private int indicatorLayoutHeight;
    private int indicatorLayoutGravity = Gravity.BOTTOM | Gravity.RIGHT;  //默认右下角
    private int indicatorLayoutMarginRight;
    private int indicatorLayoutMarginLeft;
    private int indicatorLayoutMarginTop;
    private int indicatorLayoutMarginBottom;
    //indicator
    private int indicatorSize;
    private int indicatorSpace;
    private List<View> indicatorList;
    private Drawable selectIndicatorDrawable;
    private Drawable normalIndicatorDrawable;
    private List<SoftReference<Bitmap>> bitmaps;

    public GuideView(Context context) {
        this(context, null);
    }

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bitmaps=new ArrayList<SoftReference<Bitmap>>();
        mScaleUtil = new ScaleUtil(context);
        initUnit();
        mContext = context.getApplicationContext();
        viewList = new ArrayList<View>();
        mViewPager = new ViewPager(context);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mViewPager.setLayoutParams(lp);
        addView(mViewPager);

    }

    private void initUnit() {
        indicatorSpace = mScaleUtil.toScaleSize(20);
        indicatorSize = mScaleUtil.toScaleSize(25);
        indicatorLayoutHeight = mScaleUtil.toScaleSize(100);
        indicatorLayoutMarginRight = mScaleUtil.toScaleSize(50);
        indicatorLayoutMarginLeft = mScaleUtil.toScaleSize(50);
        indicatorLayoutMarginTop = mScaleUtil.toScaleSize(10);
        indicatorLayoutMarginBottom = mScaleUtil.toScaleSize(10);
    }


    private void setAdapter() {
        if (viewList != null && viewList.size() > 0) {
            mViewPager.setAdapter(new GuideAdapter());
            if (viewList.size() > 1) {
                createShapeDrawable();
                initIndicatorLayout(viewList.size());
                mViewPager.setOnPageChangeListener(new PageChageListener());
                int selectIndex = currentIndex % indicatorList.size();
                indicatorchage(selectIndex);
            }

        }
    }



    public void createShapeDrawable() {
        ShapeDrawable selectdrawable = new ShapeDrawable(new OvalShape());
        selectdrawable.getPaint().setColor(Color.WHITE);
        selectIndicatorDrawable = selectdrawable;
        ShapeDrawable normalDrawable = new ShapeDrawable(new OvalShape());
        normalDrawable.getPaint().setColor(Color.parseColor("#f0f0f0"));
        normalDrawable.setAlpha(125);
        normalIndicatorDrawable = normalDrawable;
    }

    public void setSlectedIndicatorDrawable(Drawable drawable) {
        selectIndicatorDrawable = drawable;
        if (indicatorLayout != null) {
            int selectIndex = currentIndex % viewList.size();
            indicatorchage(selectIndex);
        }
    }

    public void setNormalIndicatorDrawable(Drawable drawable) {
        normalIndicatorDrawable = drawable;
        if (indicatorLayout != null) {
            int selectIndex = currentIndex % viewList.size();
            indicatorchage(selectIndex);
        }
    }


    public void stop() {
        Running = false;
    }


    public void setIndicatorSize(int px) {
        indicatorSize = mScaleUtil.toScaleSize(px);
        if (indicatorLayout != null) {
            for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
                View view = indicatorLayout.getChildAt(i);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                lp.width = indicatorSize;
                lp.height = indicatorSize;
                view.setLayoutParams(lp);
            }
        }
    }

    public void setIndicatorHorizontalSpace(int px) {
        indicatorSpace = mScaleUtil.toScaleSize(px);
        if (indicatorLayout != null) {
           /* LinearLayout.LayoutParams indicatorParams =new LinearLayout.LayoutParams(indicatorSize, indicatorSize);
            indicatorParams.leftMargin = indicatorSpace;*/
            for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
                View view = indicatorLayout.getChildAt(i);
                LinearLayout.LayoutParams indicatorParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                indicatorParams.leftMargin = indicatorSpace;
                view.setLayoutParams(indicatorParams);
            }
        }
    }


    public void setIndicatorLayoutGravity(int gravity) {
        indicatorLayoutGravity = gravity;
        if (indicatorLayout != null) {
            LayoutParams lp = (LayoutParams) indicatorLayout.getLayoutParams();
            lp.gravity = indicatorLayoutGravity;
        }
    }

    public void setIndicatorLayoutHeight(int height) {
        indicatorLayoutHeight = mScaleUtil.toScaleSize(height);
        if (indicatorLayout != null) {
            indicatorLayout.getLayoutParams().height = indicatorLayoutHeight;
        }
    }

    public void setIndicatorLayoutMarginRight(int space) {
        indicatorLayoutMarginRight = mScaleUtil.toScaleSize(space);
        if (indicatorLayout != null) {
            LayoutParams lp = (LayoutParams) indicatorLayout.getLayoutParams();
            lp.rightMargin = indicatorLayoutMarginRight;
        }
    }

    public void setIndicatorLayoutMarginLeft(int space) {
        indicatorLayoutMarginLeft = mScaleUtil.toScaleSize(space);
        if (indicatorLayout != null) {
            LayoutParams lp = (LayoutParams) indicatorLayout.getLayoutParams();
            lp.leftMargin = indicatorLayoutMarginLeft;
        }
    }

    public void setIndicatorLayoutMarginTop(int space) {
        indicatorLayoutMarginTop = mScaleUtil.toScaleSize(space);
        if (indicatorLayout != null) {
            LayoutParams lp = (LayoutParams) indicatorLayout.getLayoutParams();
            lp.topMargin = indicatorLayoutMarginTop;
        }
    }


    public void setIndicatorLayoutMarginBottom(int space) {
        indicatorLayoutMarginBottom = mScaleUtil.toScaleSize(space);
        if (indicatorLayout != null) {
            LayoutParams lp = (LayoutParams) indicatorLayout.getLayoutParams();
            lp.bottomMargin = indicatorLayoutMarginBottom;
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initIndicatorLayout(int indicatorCount) {
        indicatorList = new ArrayList<>();
        indicatorLayout = new LinearLayout(mContext);
        LayoutParams indicatorLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, indicatorLayoutHeight);
        indicatorLayoutParams.gravity = indicatorLayoutGravity;
        indicatorLayoutParams.leftMargin = indicatorLayoutMarginLeft;
        indicatorLayoutParams.rightMargin = indicatorLayoutMarginRight;
        indicatorLayoutParams.bottomMargin = indicatorLayoutMarginBottom;
        indicatorLayoutParams.topMargin = indicatorLayoutMarginTop;
        indicatorLayout.setLayoutParams(indicatorLayoutParams);

        LinearLayout.LayoutParams indicatorParams = new LinearLayout.LayoutParams(indicatorSize, indicatorSize);
        indicatorParams.leftMargin = indicatorSpace;
        indicatorParams.gravity = Gravity.CENTER_VERTICAL;
        for (int i = 0; i < indicatorCount; i++) {
            View indicator = new View(mContext);
            indicator.setLayoutParams(indicatorParams);
            indicator.setBackground(selectIndicatorDrawable);
            indicatorLayout.addView(indicator);
            indicatorList.add(indicator);
        }
        addView(indicatorLayout);
    }



    public void setGuideResource(int[] picIds,View lastPage) {
        if (picIds==null||picIds.length==0)return;
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                             LayoutParams.MATCH_PARENT);
        for (int i = 0; i < picIds.length; i++) {
            ImageView imageView = new ImageView(mContext);
            Bitmap bitmap= BitmapReader.readBitMap565(mContext,picIds[i]);
            imageView.setImageBitmap(bitmap);
            SoftReference<Bitmap> softReference=new SoftReference<Bitmap>(bitmap);
            bitmaps.add(softReference);
            viewList.add(imageView);
        }
        lastPage.setLayoutParams(lp);
        viewList.add(lastPage);
        setAdapter();
    }

    public void recycle(){
        if (bitmaps!=null&&bitmaps.size()>0){
            for (int i=0, len=bitmaps.size();i<len;i++){
                SoftReference<Bitmap> softReference=bitmaps.get(i);
                if (softReference!=null){
                    Bitmap bitmap=softReference.get();
                    if (bitmap!=null){
                        bitmap.recycle();
                        bitmap=null;
                    }
                }
            }
        }
        if (viewList!=null)viewList.clear();
        if (indicatorList!=null)indicatorList.clear();
        mScaleUtil=null;
        removeAllViews();
    }

    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {

            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = viewList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = viewList.get(position);
            container.removeView(view);
        }
    }


    class PageChageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            currentIndex = position;
            int selectIndex = currentIndex % indicatorList.size();
            indicatorchage(selectIndex);
        }

    }

    private void indicatorchage(int selectIndex) {
        int i = 0;
        for (View indicator : indicatorList) {
            if (i == selectIndex) {
                indicator.setBackgroundDrawable(selectIndicatorDrawable);
            } else {
                indicator.setBackgroundDrawable(normalIndicatorDrawable);
            }
            i++;
        }

    }


    private static class ScaleUtil {
        private final int BASE_WIDTH = 1080;
        private float BASE_RATIO = 1;

        public ScaleUtil(Context context) {
            BASE_RATIO = 1.0f * context.getResources().getDisplayMetrics().widthPixels / BASE_WIDTH;
        }

        public int toScaleSize(int px) {
            return (int) (BASE_RATIO * px);
        }
    }






    public static class BitmapReader {

        public static Bitmap readBitMap565(Context context, int resId) {
            try {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                InputStream is = context.getResources().openRawResource(resId);
                Bitmap bitmap=BitmapFactory.decodeStream(is, null, opt);
                is.close();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Bitmap readBitMap888(Context context, int resId) {
            try {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                InputStream is = context.getResources().openRawResource(resId);
                Bitmap bitmap=BitmapFactory.decodeStream(is, null, opt);
                is.close();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
