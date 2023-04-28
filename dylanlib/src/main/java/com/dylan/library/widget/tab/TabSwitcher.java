package com.dylan.library.widget.tab;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntDef;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.dylan.library.R;
import com.dylan.library.proguard.NotProguard;
import com.dylan.library.utils.DensityUtils;
import com.dylan.library.utils.EmptyUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author: Dylan
 * Date: 2020/3/23
 * Desc:
 */
public class TabSwitcher extends LinearLayout implements View.OnClickListener {
    private int currentPosition = 0;
    private int horizontalSpace = 3;
    private int selectTextSize = 14;
    private int normalTextSize = 14;
    private Drawable selectBgDrawable;
    private Drawable normalBgDrawable;
    private int selectTextColor;
    private int normalTextColor;
    private int indicatorTopPadding;
    @NotProguard
    public static final int TEXT_BOLD_NONE = 0;
    @NotProguard
    public static final int TEXT_BOLD_WHEN_SELECT = 1;
    @NotProguard
    public static final int TEXT_BOLD_BOTH = 2;

    @IntDef({TEXT_BOLD_NONE, TEXT_BOLD_WHEN_SELECT, TEXT_BOLD_BOTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BoldStyle {}
    @BoldStyle
    private int textBoldStyle = TEXT_BOLD_NONE;
    private int normalTextBoldValue = 1,selectTextBoldValue=1;
    private int indicatorWidth=20;
    private int indicatorHeight=2;
    private int indicatorColor;


    public TabSwitcher(Context context) {
        this(context, null);
    }

    public TabSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        selectTextColor = Color.parseColor("#333333");
        normalTextColor = Color.parseColor("#666666");
        setGravity(Gravity.CENTER_VERTICAL);
    }


    private void setHorizontalSpace(int space) {
        this.horizontalSpace = DensityUtils.dp2px(getContext(), space);
    }

    private void setIndicatorTopMargin(int indicatorTopPadding) {
        this.indicatorTopPadding = DensityUtils.dp2px(getContext(), indicatorTopPadding);
    }
    public void setIndicatorWidth(int indicatorWidth){
        this.indicatorWidth=DensityUtils.dp2px(getContext(), indicatorWidth) ;
    }

    public void setIndicatorHeight(int indicatorHeight) {
        this.indicatorHeight =DensityUtils.dp2px(getContext(), indicatorHeight) ;
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public void setTabTextBoldStyle(@BoldStyle int boldStyle, int normalTextBoldValue,int selectTextBoldValue) {
        textBoldStyle = boldStyle;
        this.normalTextBoldValue =normalTextBoldValue;
        this.selectTextBoldValue =selectTextBoldValue;
    }


    private void setDrawable(Drawable selectDrawable, Drawable normalBgDrawable) {
        this.selectBgDrawable = selectDrawable;
        this.normalBgDrawable = normalBgDrawable;
    }

    private void setTabTextColor(int selectColor, int normalColor) {
        this.selectTextColor = selectColor;
        this.normalTextColor = normalColor;
    }

    private void setTabTextSize(int selectTextSize, int normalTextSize) {
        this.selectTextSize = selectTextSize;
        this.normalTextSize = normalTextSize;
    }

    private TabSwitcher createTab(String[] tabArrays) {
        if (EmptyUtils.isEmpty(tabArrays)) return this;


        int i = 0;
        for (String str : tabArrays) {
            MediumTabItem tabWrapper = (MediumTabItem) LayoutInflater.from(getContext()).inflate(R.layout.dl_unslidingtab_wrapper, new LinearLayout(getContext()), false);
            LayoutParams wrapLayoutParam;
            wrapLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            tabWrapper.setLayoutParams(wrapLayoutParam);
            tabWrapper.setOnClickListener(this);
            tabWrapper.getTitleView().setText(str);
            tabWrapper.getTitleView().setBold(1);
            tabWrapper.setTag(i);
            if (tabArrays.length > 1 && i != tabArrays.length - 1) {
                tabWrapper.setPadding(0, 0, horizontalSpace, 0);
            }
            //指示器
            LinearLayout.LayoutParams indicaotrLp = (LayoutParams) tabWrapper.getIndicatorView().getLayoutParams();
            indicaotrLp.topMargin = indicatorTopPadding;
            indicaotrLp.width=indicatorWidth;
            indicaotrLp.height=indicatorHeight;
            tabWrapper.getIndicatorView().setBackgroundColor(indicatorColor);


            addView(tabWrapper);

            if (i == 0) {
                select(i);
            } else {
                unSelect(i);
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
        if (tabListener != null) tabListener.select(position, v);
    }


    public void switchTo(int position) {
        currentPosition = position;
        for (int i = 0; i < getChildCount(); i++) {
            if (i == position) {
                select(i);
            } else {
                unSelect(i);
            }
        }

    }


    private void select(int position) {
        MediumTabItem tabWrapper = (MediumTabItem) getChildAt(position);
        tabWrapper.setBackground(selectBgDrawable);
        tabWrapper.getTitleView().setTextColor(selectTextColor);
        tabWrapper.getTitleView().setTextSize(selectTextSize);
        tabWrapper.getIndicatorView().setVisibility(VISIBLE);
        if (textBoldStyle == TEXT_BOLD_BOTH || textBoldStyle == TEXT_BOLD_WHEN_SELECT) {
            tabWrapper.getTitleView().setBold(selectTextBoldValue);
        } else{
            tabWrapper.getTitleView().setBold(0);
        }
    }

    private void unSelect(int position) {
        MediumTabItem tabWrapper = (MediumTabItem) getChildAt(position);
        tabWrapper.setBackground(normalBgDrawable);
        tabWrapper.getTitleView().setTextColor(normalTextColor);
        tabWrapper.getTitleView().setTextSize(normalTextSize);
        tabWrapper.getIndicatorView().setVisibility(INVISIBLE);
        if (textBoldStyle == TEXT_BOLD_BOTH) {
            tabWrapper.getTitleView().setBold(normalTextBoldValue);
        } else{
            tabWrapper.getTitleView().setBold(0);
        }
    }


    public interface SelectTabListener {
        void select(int position, View view);
    }

    SelectTabListener tabListener;

    public void setSelectTabListener(SelectTabListener listener) {
        tabListener = listener;

    }


    public static class Builder {
        private int selectTextSize = 14;
        private int normalTextSize = 14;
        private int horizontalSpace = 3;
        private Drawable selectBgDrawable;
        private Drawable normalBgDrawable;
        private int selectTextColor;
        private int normalTextColor;
        private int normalTextBoldValue = 1;
        private int selectTextBoldValue = 1;
        private int textBoldStyle = TEXT_BOLD_NONE;
        private Context mContext;
        private int indicatorTopMargin;
        private int indicatorWidth=20;
        private int indicatorHeight=2;
        private int indicatorColor=Color.BLACK;


        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTabHorizontalSpace(int horizontalSpace) {
            this.horizontalSpace = horizontalSpace;
            return this;
        }


        public Builder setTabTextSize(int normalTextSize, int selectTextSize) {
            this.normalTextSize = normalTextSize;
            this.selectTextSize = selectTextSize;
            return this;
        }


        public Builder setTabBackgroundDrawable(Drawable normalBgDrawable, Drawable selectBgDrawable) {
            this.normalBgDrawable = normalBgDrawable;
            this.selectBgDrawable = selectBgDrawable;
            return this;
        }


        public Builder setTabTextColor(int normalTextColor, int selectTextColor) {
            this.normalTextColor = normalTextColor;
            this.selectTextColor = selectTextColor;
            return this;
        }

        public Builder setTabTextBoldStyle(@BoldStyle int boldStyle, int normalTextBoldValue, int selectTextBoldValue) {
            textBoldStyle = boldStyle;
            this.normalTextBoldValue = normalTextBoldValue;
            this.selectTextBoldValue = selectTextBoldValue;
            return this;
        }


        public Builder setIndicatorTopMargin(int indicatorTopPadding) {
            this.indicatorTopMargin = indicatorTopPadding;
            return this;
        }

        public Builder setIndicatorWidth(int indicatorWidth){
            this.indicatorWidth=indicatorWidth;
            return this;
        }

        public Builder setIndicatorHeight(int indicatorHeight) {
            this.indicatorHeight = indicatorHeight;
            return this;
        }

        public Builder setIndicatorColor(int indicatorColor) {
            this.indicatorColor = indicatorColor;
            return this;
        }

        public TabSwitcher createTab(String[] tabArrays, SelectTabListener listener) {
            TabSwitcher tabSwitcher = new TabSwitcher(mContext);
            tabSwitcher.setHorizontalSpace(horizontalSpace);
            tabSwitcher.setDrawable(selectBgDrawable, normalBgDrawable);
            tabSwitcher.setSelectTabListener(listener);
            tabSwitcher.setTabTextSize(selectTextSize, normalTextSize);
            tabSwitcher.setTabTextColor(selectTextColor, normalTextColor);
            tabSwitcher.setTabTextBoldStyle(textBoldStyle, normalTextBoldValue,selectTextBoldValue);
            tabSwitcher.setIndicatorTopMargin(indicatorTopMargin);
            tabSwitcher.setIndicatorWidth(indicatorWidth);
            tabSwitcher.setIndicatorHeight(indicatorHeight);
            tabSwitcher.setIndicatorColor(indicatorColor);
            tabSwitcher.createTab(tabArrays);
            return tabSwitcher;
        }

    }

}
