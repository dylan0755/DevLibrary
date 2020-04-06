package com.dylan.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.EmptyUtils;

/**
 * Author: Dylan
 * Date: 2020/3/23
 * Desc:
 */
public class NormalTabSwitcher extends LinearLayout implements View.OnClickListener {
    private int currentPosition = 0;
    private int leftMargin;
    private int leftPadding=3;
    private int topPadding=3;
    private int textSize = 12;
    private int selectDrawableId;
    private int unSelectDrawableId;
    private int selectColor;
    private int unSelectColor;

    public NormalTabSwitcher(Context context) {
        super(context);
    }

    public NormalTabSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        leftMargin = DensityUtils.dp2px(getContext(), 10);
        selectColor=Color.parseColor("#333333");
        unSelectColor=Color.parseColor("#666666");
    }


    public NormalTabSwitcher setPadding(int paddingLeft,int paddingTop){
        this.leftPadding= DensityUtils.dp2px(getContext(),paddingLeft);
        this.topPadding= DensityUtils.dp2px(getContext(),paddingTop);
        return this;
    }

    public NormalTabSwitcher setDrawableId(int selectDrawableId,int unSelectDrawableId){
        this.selectDrawableId=selectDrawableId;
        this.unSelectDrawableId=unSelectDrawableId;
        return this;
    }

    public NormalTabSwitcher setTabTextColor(int selectColor, int unSelectColor){
        this.selectColor=selectColor;
        this.unSelectColor=unSelectColor;
        return this;
    }

    public NormalTabSwitcher setTabTextSize(int sp){
         this.textSize=sp;
         return this;
    }
    public NormalTabSwitcher createTab(String[] tabArrays) {
        if (EmptyUtils.isEmpty(tabArrays)) return this;
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.leftMargin = leftMargin;


        int i = 0;
        for (String str : tabArrays) {
            TextView textView = new TextView(getContext());
            textView.setOnClickListener(this);
            textView.setTextSize(textSize);
            textView.setPadding(leftPadding,  topPadding,leftPadding,topPadding);
            textView.setLayoutParams(layoutParams);
            textView.setText(str);
            textView.setTag(i);
            addView(textView);

            if (i == 0) {
                select(i);
            } else {
                unselect(i);
            }
            i++;

        }
        return this;
    }


    @Override

    public void onClick(View v) {
        int position = (int) v.getTag();
        if (position == currentPosition) return;
        switchTo(position);
        currentPosition = position;
        if (tabListener != null) tabListener.select(position, v);

    }


    public void switchTo(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            if (i == position) {
                select(i);
            } else {
                unselect(i);
            }
        }
    }


    private void select(int position) {
        getChildAt(position).setBackgroundResource(selectDrawableId);
        ((TextView) getChildAt(position)).setTextColor(selectColor);
    }

    private void unselect(int position) {
        getChildAt(position).setBackgroundResource(unSelectDrawableId);
        ((TextView) getChildAt(position)).setTextColor(unSelectColor);
    }


    public interface SelectTabListener {
        void select(int position, View view);
    }

    SelectTabListener tabListener;

    public void setSelectTabListener(SelectTabListener listener) {
        tabListener = listener;

    }

}
