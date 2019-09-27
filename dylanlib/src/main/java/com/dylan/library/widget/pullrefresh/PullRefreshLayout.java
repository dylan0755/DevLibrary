package com.dylan.library.widget.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dylan.library.R;


public class PullRefreshLayout extends BaseRefreshLayout {
    private float maxScrollHeight;
    private float headHeight;
    private float refreshHeight;
    private TextView tvTip;
    private LinearLayout llIcon;
    private CircleView refreshCircleView;
    private String pullingTipText = "下拉刷新";
    private String relaseTipText = "松开刷新";
    private String refreshingTipText = "正在刷新...";
    private String maxScrollToBackTipText = "松开返回";
    private ColorRecord tipTextColor;
    private ColorRecord circleOutRingColor;
    private ColorRecord circleInnerRingColor;
    private boolean supportMaxBack;


    public PullRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        maxScrollHeight = dip2px(getContext(), 180);//不支持下拉返回150，支持下拉返回高度为180
        refreshHeight = dip2px(getContext(), 50);
        headHeight = dip2px(getContext(), 80);
        addRefreshListener();
    }


    @Override
    public View getHeaderView() {
        final View headView = LayoutInflater.from(getContext()).inflate(R.layout.dl_view_refresh_header, null);
        llIcon = headView.findViewById(R.id.llIcon);
        tvTip = headView.findViewById(R.id.tv_tip);
        refreshCircleView = headView.findViewById(R.id.indicatorView);

        if (tipTextColor!= null)tvTip.setTextColor(tipTextColor.color);
        if (circleInnerRingColor!= null) refreshCircleView.setInnerRingColor(circleInnerRingColor.color);
        if (circleOutRingColor !=null) refreshCircleView.setOutRingColor(circleOutRingColor.color);
        setSupportMaxBack(supportMaxBack);
        return headView;
    }


    @Override
    public float getMaxScrollHeight() {
        return maxScrollHeight;
    }

    @Override
    public float getHeaderHeight() {
        return headHeight;
    }

    @Override
    public float getRefreshHeight() {
        return refreshHeight;
    }


    /**
     * 初始化
     */
    private void addRefreshListener() {

        /**
         * 松开后的监听
         */
        setPullToRefreshListener(new PullToRefreshListener() {
            @Override
            public void onPulling(float fraction) {
                refreshCircleView.setProgress((int) (fraction * 100));
                refreshViewTranslated(fraction);
                if (headHeight > (int) (headHeight * limitValue(1, fraction))) {
                    tvTip.setText(pullingTipText);
                } else {
                    tvTip.setText(relaseTipText);
                }

                if (isSupportMaxBack()) {
                    if (fraction >= maxFraction) {
                        tvTip.setText(maxScrollToBackTipText);
                    }
                }

            }

            @Override
            public void onFractionChanged(float fraction) {
                refreshViewTranslated(fraction);

            }

            @Override
            public void onRefresh(float fraction) {
                refreshCircleView.refresh();
                tvTip.setText(refreshingTipText);

                if (pullRefreshListener != null) {
                    pullRefreshListener.onRefresh(PullRefreshLayout.this);
                }
            }

            @Override
            public void onRefreshCompleted() {
                refreshCircleView.completeRefresh();
            }

            @Override
            public void onMaxScrollBack() {
                if (onMaxBackListener != null) onMaxBackListener.onBack(PullRefreshLayout.this);
            }
        });
    }


    private void refreshViewTranslated(float fraction) {
        float transY = headHeight * fraction - dip2px(getContext(), 34);
        llIcon.setTranslationY(transY);

    }


    /**
     * 限定值
     *
     * @param a
     * @param b
     * @return
     */
    private float limitValue(float a, float b) {
        float valve = 0;
        final float min = Math.min(a, b);
        final float max = Math.max(a, b);
        valve = valve > min ? valve : min;
        valve = valve < max ? valve : max;
        return valve;
    }

    public interface PullRefreshListener {
        void onRefresh(PullRefreshLayout refreshLayout);
    }

    private PullRefreshListener pullRefreshListener;

    public void setPullRefreshListener(PullRefreshListener listener) {
        this.pullRefreshListener = listener;
    }

    public interface OnMaxBackListener{
        void onBack(PullRefreshLayout refreshLayout);
    }
    private OnMaxBackListener onMaxBackListener;
    public void setOnMaxBackListener(OnMaxBackListener listener){
        onMaxBackListener=listener;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public void setPullingTipText(String pullingTipText) {
        this.pullingTipText = pullingTipText;
    }

    public void setRelaseTipText(String relaseTipText) {
        this.relaseTipText = relaseTipText;
    }

    public void setRefreshingTipText(String refreshingTipText) {
        this.refreshingTipText = refreshingTipText;
    }

    public void setMaxScrollToBackTipText(String maxScrollToBackTipText) {
        this.maxScrollToBackTipText = maxScrollToBackTipText;
    }

    public void setTipTextColor(int color) {
        tipTextColor=new ColorRecord(color);
    }


    public void setCircleOutRingColor(int color) {
        circleOutRingColor = new ColorRecord(color);
    }


    public void setCircleInnerRingColor(int color) {
        circleInnerRingColor =new ColorRecord(color);
    }

    public void setMaxBackEnable(boolean bl){
        supportMaxBack=bl;
        setSupportMaxBack(supportMaxBack);
    }


    public boolean isRefreshing(){
        return isRefreshing;
    }
}
