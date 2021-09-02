package com.dylan.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dylan.library.R;
import com.dylan.library.utils.DensityUtils;

/**
 * Author: Dylan
 * Date: 2020/3/23
 * Desc:
 */
public class CircleIndicatorSwitcher extends LinearLayout {

    public CircleIndicatorSwitcher(Context context) {
        this(context,null);
    }

    public CircleIndicatorSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);


    }


    public void createPoints(int indicatorPointCount){
        createPoints(indicatorPointCount,3, R.drawable.dl_indicator_point);
    }

    public void createPoints(int indicatorPointCount,int pointDrawableId){
        createPoints(indicatorPointCount,3,  pointDrawableId);
    }


    public void createPoints(int indicatorPointCount, int indicatorPointMargin, int pointDrawableId){
        LayoutParams lp = new LayoutParams(-2, -2);
        int mPointLeftRightMargin = DensityUtils.dp2px(getContext(), indicatorPointMargin);
        int mPointTopBottomMargin = DensityUtils.dp2px(getContext(), indicatorPointMargin);
        lp.setMargins(mPointLeftRightMargin, mPointTopBottomMargin, mPointLeftRightMargin, mPointTopBottomMargin);

        for (int i = 0; i < indicatorPointCount; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(lp);
            imageView.setImageResource(pointDrawableId);
            addView(imageView);
        }
    }


    public void switchToPoint(int position){
        for (int k = 0; k < getChildCount(); k++) {
            getChildAt(k).setEnabled(k ==position);
            // 处理指示器选中和未选中状态图片尺寸不相等
            getChildAt(k).requestLayout();
        }
    }
}
