package com.dylan.library.widget.tab;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dylan.library.R;
import com.dylan.library.widget.MediumTextView;

/**
 * Author: Dylan
 * Date: 2021/09/06
 * Desc:
 */
public class MediumTabItem extends LinearLayout {
    private MediumTextView mTitleView;
    private View mIndicatorView;

    public MediumTabItem(Context context) {
        this(context,null);
    }

    public MediumTabItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView=findViewById(R.id.tvTitle);
        mIndicatorView=findViewById(R.id.indicatorView);
    }


    public MediumTextView getTitleView() {
        return mTitleView;
    }

    public View getIndicatorView() {
        return mIndicatorView;
    }
}
