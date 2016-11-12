package com.dylan.library.tab;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Dylan on 2016/9/7.
 */
public class TabGroup extends LinearLayout {
    private Context mContext;
    private int normalColor;
    private int selectColor;
    private int currentIndex;
    private int default_width =130;
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


    public void addTabs(String[] titles, int[] picNormalID, int[] picSelectID) {
        LayoutParams lp_out = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        lp_out.weight = 1;
        for (int i = 0; i < titles.length; i++) {
            createTabItem(titles[i], picNormalID[i], picSelectID[i], lp_out, i);
        }
    }

    /**
     * @param title
     * @param picNormalId
     * @param picSelectId
     * @param lp_out
     * @param i
     */
    private void createTabItem(String title, int picNormalId, int picSelectId, LayoutParams lp_out, int i) {
        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setLayoutParams(lp_out);
        RelativeLayout.LayoutParams lp_int = new RelativeLayout.LayoutParams(mScaleUtil.toScaleSize(default_width), mScaleUtil.toScaleSize(deault_height));
        lp_int.addRule(RelativeLayout.CENTER_IN_PARENT);

        TabItem tabItem = new TabItem(mContext);
        tabItem.setLayoutParams(lp_int);
        tabItem.setOnClickListener(new TabClick());
        tabItem.setTag(i);
        tabItem.setTab(title, picNormalId, picSelectId);
        if (normalColor != 0) tabItem.setTabNormalColor(normalColor);
        if (selectColor != 0) tabItem.setTabSelectColor(selectColor);
        relativeLayout.addView(tabItem);
        addView(relativeLayout);
    }


    private TabItem getTabItem(int index) {
        ViewGroup vg = (ViewGroup) getChildAt(index);
        return (TabItem) vg.getChildAt(0);
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
            if (position==index){
                currentIndex=index;
                tabItem.setSelect(true);
            }else{
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
                Log.e("onClick: ", "sss");
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
    public class ScaleUtil {
        private Context mContext;
        private final int BASE_WIDTH = 1080;
        private float BASE_RATIO = 1;

        public ScaleUtil(Context context) {
            mContext = context;
            BASE_RATIO = 1.0f * mContext.getResources().getDisplayMetrics().widthPixels / BASE_WIDTH;
        }

        public int toScaleSize(int px) {
            return (int) (BASE_RATIO * px);
        }
    }
}
