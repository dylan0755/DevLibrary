package com.dylan.library.widget.tab;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dylan.library.R;


/**
 * Created by Dylan on 2016/9/7.
 */
public class TabGroup extends LinearLayout {
    private Context mContext;
    private int normalColor;
    private int selectColor;
    private int currentIndex;
    private int tabItemCount;
    private int bagedviewId = 100;
    private int default_width = 130;
    private int deault_height = 106;
    private ScaleUtil mScaleUtil;


    public TabGroup(Context context) {
        this(context, null);
    }

    public TabGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScaleUtil = new ScaleUtil(context);
    }


    public void addItem(@NonNull String tabtext, int picNormalId, int picSelectId, boolean isbageview) {
        LayoutParams lp_out = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        lp_out.weight = 1;
        createTabItem(tabtext, picNormalId, picSelectId, lp_out, tabItemCount, isbageview,false);
        tabItemCount++;
    }

    public void addItem(@NonNull String tabtext, int picNormalId, int picSelectId, boolean isbageview,boolean isNumBadgeView){
        LayoutParams lp_out = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        lp_out.weight = 1;
        createTabItem(tabtext, picNormalId, picSelectId, lp_out, tabItemCount, isbageview,isNumBadgeView);
        tabItemCount++;
    }

    /**
     * @param title
     * @param picNormalId
     * @param picSelectId
     * @param lp_out
     * @param tabId       添加id，相当于布局中  android:below="@id/xxx"  bageview要显示在tabitem的右边
     * @param isBadgeView 该TabItem是否附加BageView
     *     @param isNumBadgeView 该BadgeView能否显示数字
     */
    private void createTabItem(String title, int picNormalId, int picSelectId, LayoutParams lp_out, int tabId, boolean isBadgeView,boolean isNumBadgeView) {
        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setLayoutParams(lp_out);
        RelativeLayout.LayoutParams lp_int = new RelativeLayout.LayoutParams(mScaleUtil.toScaleSize(default_width), mScaleUtil.toScaleSize(deault_height));
        lp_int.addRule(RelativeLayout.CENTER_IN_PARENT);


        //icon和文本
        TabItem tabItem = new TabItem(mContext);
        tabItem.setId(tabId + 1);
        tabItem.setLayoutParams(lp_int);
        tabItem.setOnClickListener(new TabClick());
        tabItem.setTag(tabId);
        tabItem.setTab(title, picNormalId, picSelectId);
        if (normalColor != 0) tabItem.setTabNormalColor(normalColor);
        if (selectColor != 0) tabItem.setTabSelectColor(selectColor);
        relativeLayout.addView(tabItem);

        if (isBadgeView) {
            TextView textView = new TextView(getContext());
            textView.setId(tabId + bagedviewId);
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.tabgroup_bageview));
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(9);
            textView.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams lp_bageview=null;
            if (isNumBadgeView){//显示数字的红点
                lp_bageview= new RelativeLayout.LayoutParams(mScaleUtil.toScaleSize(48), mScaleUtil.toScaleSize(48));
                lp_bageview.addRule(RelativeLayout.RIGHT_OF, tabItem.getId());
                lp_bageview.leftMargin = (int) getResources().getDimension(R.dimen.bageview_left_margin);
                lp_bageview.topMargin = (int) getResources().getDimension(R.dimen.numbageview_top_margin);
            }else{//提示小红点
                lp_bageview= new RelativeLayout.LayoutParams(mScaleUtil.toScaleSize(18), mScaleUtil.toScaleSize(18));
                lp_bageview.addRule(RelativeLayout.RIGHT_OF, tabItem.getId());
                lp_bageview.leftMargin = (int) getResources().getDimension(R.dimen.bageview_left_margin);
                lp_bageview.topMargin = (int) getResources().getDimension(R.dimen.minbageview_top_margin);
            }


            textView.setLayoutParams(lp_bageview);
            textView.setSingleLine();
            textView.setEllipsize(TextUtils.TruncateAt.END);
            relativeLayout.addView(textView);
        }

        addView(relativeLayout);
    }


    private TabItem getTabItem(int index) {
        ViewGroup vg = (ViewGroup) getChildAt(index);
        return (TabItem) vg.getChildAt(0);
    }

    public void setBadgeViewText(int index, int textValue) throws NullPointerException {
        TextView textView = findBadegeView(index);
        if (textView == null) throw new NullPointerException("can not find the badgevew");
        else {
            textView.setVisibility(VISIBLE);
            String textStr=Integer.toString(textValue);
            if (textStr.length()>=3){
                textStr="99+";
                textView.setTextSize(8);
            }else{
                textView.setTextSize(9);
            }
            textView.setText(textStr);
        }
    }



    public void setBadgeViewText(int index,int textValue,String max_value){
        TextView textView = findBadegeView(index);
        if (textView == null) throw new NullPointerException("can not find the badgevew");
        else {
            textView.setVisibility(VISIBLE);
            String textStr=Integer.toString(textValue);
            if (textStr.length()>=3){
                textStr=max_value;
                textView.setTextSize(8);
            }else{
                textView.setTextSize(9);
            }
            textView.setText(textStr);
        }
    }

    public void hideBadgeView(int index) {
        TextView textView = findBadegeView(index);
        if (textView != null) {
            textView.setVisibility(INVISIBLE);
        }
    }


    public void showBadgeView(int index) {
        TextView textView = findBadegeView(index);
        if (textView != null) {
            textView.setVisibility(VISIBLE);
        }
    }

    private TextView findBadegeView(int index) {
        ViewGroup vg = (ViewGroup) getChildAt(index);
        TextView textView = (TextView) vg.findViewById(index + bagedviewId);
        return textView;
    }

    public void setTabIconSize(int width, int height) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getTabItem(i) instanceof TabItem) {
                TabItem tabItem = getTabItem(i);
                tabItem.setIconSize(width, height);
            }
        }
    }

    public void setTabFontSize(int px) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getTabItem(i) instanceof TabItem) {
                TabItem tabItem = getTabItem(i);
                tabItem.setTabTitleSize(px);
            }
        }
    }

    public void setTabSelectColor(int selectcolor) {
        selectColor = selectcolor;
        for (int i = 0; i < getChildCount(); i++) {
            if (getTabItem(i) instanceof TabItem) {
                TabItem tabItem = getTabItem(i);
                tabItem.setTabSelectColor(selectColor);
            }
        }
    }


    public void setTabNormalColor(int normalcolor) {
        normalColor = normalcolor;
        for (int i = 0; i < getChildCount(); i++) {
            if (getTabItem(i) instanceof TabItem) {
                TabItem tabItem = getTabItem(i);
                tabItem.setTabNormalColor(normalColor);
            }
        }
    }

    public void setSelect(int index) {
        for (int i = 0; i < getChildCount(); i++) {
            TabItem tabItem = getTabItem(i);
            int position = (int) tabItem.getTag();
            if (position == index) {
                currentIndex = index;
                tabItem.setSelect(true);
            } else {
                tabItem.setSelect(false);
            }
        }
    }

    class TabClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            TabItem tabItem = (TabItem) v;
            int position = (int) tabItem.getTag();
            if (currentIndex == position) {
                return;
            }
            //选中图标状态
            for (int i = 0; i < getChildCount(); i++) {
                if (getTabItem(i) instanceof TabItem) {
                    TabItem item = getTabItem(i);
                    int tag = (int) item.getTag();
                    if (tag != position) {
                        item.setSelect(false);
                    } else {
                        item.setSelect(true);
                    }

                }
            }
            currentIndex = position;
            if (mSelectListener != null) mSelectListener.select(position);

        }
    }


    /*****************************
     * TabItem
     *******************************************/

    public class TabItem extends LinearLayout {

        private int tabIconWidth = 80;
        private int tabIconHeight = 80;
        private int text_size = 12;
        private float ratio = 0.074f;
        private ImageView mIconView;
        private TextView mTabLabel;
        private int picNormalId;
        private int picSelectId;
        private int normalLabelColor;
        private int selectLabelColor;
        private boolean isSelect;

        public TabItem(Context context, int iconWidth, int iconheight) {
            this(context);
            tabIconWidth = iconWidth;
            tabIconWidth = iconheight;
        }

        public TabItem(Context context) {
            this(context, null);
        }

        public TabItem(Context context, AttributeSet attrs) {
            super(context, attrs);
            float width = context.getResources().getDisplayMetrics().widthPixels;
            tabIconWidth = (int) (width * ratio);
            tabIconHeight = (int) (width * ratio);
            setOrientation(VERTICAL);
            initTab(context);
        }


        private void initTab(Context context) {
            LayoutParams lp_icon = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
            lp_icon.weight = 0.6f;
            LayoutParams lp_label = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
            lp_label.weight = 0.4f;
            lp_icon.gravity = Gravity.CENTER_HORIZONTAL;
            lp_label.gravity = Gravity.CENTER_HORIZONTAL;
            mIconView = new ImageView(context);
            mIconView.setLayoutParams(lp_icon);
            mTabLabel = new TextView(context);
            mTabLabel.setLayoutParams(lp_label);
            mTabLabel.setTextSize(text_size);
            selectLabelColor = Color.BLACK;
            normalLabelColor = mTabLabel.getCurrentTextColor();
            addView(mIconView);
            addView(mTabLabel);
        }

        public void setTab(String label, int picNormalId, int picSelectId) {
            this.picNormalId = picNormalId;
            this.picSelectId = picSelectId;
            mTabLabel.setText(label);
            mIconView.setImageResource(picNormalId);
        }

        public void setIconSize(int width, int height) {
            mIconView.getLayoutParams().width = width;
            mIconView.getLayoutParams().height = height;
            invalidate();
        }

        public void setTabTitleSize(int px) {
            mTabLabel.setTextSize(px);
            invalidate();
        }

        public void setTabSelectColor(int selectColor) {
            selectLabelColor = selectColor;
            setSelect(isSelect);
        }

        public void setTabNormalColor(int normalColor) {
            normalLabelColor = normalColor;
            setSelect(isSelect);
        }

        public void setSelect(boolean bl) {
            isSelect = bl;
            if (bl) {
                mIconView.setImageResource(picSelectId);
                mTabLabel.setTextColor(selectLabelColor);
            } else {
                mIconView.setImageResource(picNormalId);
                mTabLabel.setTextColor(normalLabelColor);
            }
        }

    }


    private TabGroupSelectListener mSelectListener;

    public interface TabGroupSelectListener {
        void select(int position);
    }

    public void setTabGroupSelectListener(TabGroupSelectListener listener) {
        mSelectListener = listener;
    }


    /**
     * 适配
     */
    private class ScaleUtil {
        private Context mContext;
        private final int BASE_WIDTH = 1080;
        private float BASE_RATIO = 1;

        public ScaleUtil(Context context) {
            mContext = context;

            boolean isPortail = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
            if (isPortail)
                BASE_RATIO = 1.0f * mContext.getResources().getDisplayMetrics().widthPixels / BASE_WIDTH;
            else
                BASE_RATIO = 1.0f * mContext.getResources().getDisplayMetrics().heightPixels / BASE_WIDTH;
        }

        public int toScaleSize(int px) {
            return (int) (BASE_RATIO * px);
        }
    }
}
