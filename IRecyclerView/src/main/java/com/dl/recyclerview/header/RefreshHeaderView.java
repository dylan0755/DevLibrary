package com.dl.recyclerview.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dl.recyclerview.CircleIndicatorView;
import com.dl.recyclerview.R;
import com.dl.recyclerview.RefreshTrigger;


public class RefreshHeaderView extends RelativeLayout implements RefreshTrigger {

    private CircleIndicatorView indicatorView;
    private TextView tvRefresh;


    private int mHeight;

    public RefreshHeaderView(Context context) {
        this(context, null);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.refresh_header_view, this);

        tvRefresh = findViewById(R.id.tvRefresh);
        indicatorView = findViewById(R.id.indicatorView);


    }


    public CircleIndicatorView getIndicatorView(){
        return indicatorView;
    }

    @Override
    public void onStart(boolean automatic, int headerHeight, int finalHeight) {
        this.mHeight = headerHeight;
    }

    @Override
    public void onMove(boolean isComplete, boolean automatic, int moved) {
        if (!isComplete) {
            indicatorView.setVisibility(VISIBLE);
            indicatorView.setProgress((int) ((moved * 1.0f / mHeight) * 100));
            if (moved <= mHeight) {
                tvRefresh.setText("下拉刷新");
            } else {
                tvRefresh.setText("松开刷新");

            }
        }
    }

    @Override
    public void onRefresh() {
        indicatorView.refresh();
        tvRefresh.setText("正在刷新");
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
        indicatorView.completeRefresh();
        tvRefresh.setText("更新完成");
    }

    @Override
    public void onReset() {

    }
}
