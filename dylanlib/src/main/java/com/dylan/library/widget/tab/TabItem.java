package com.dylan.library.widget.tab;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dylan.library.screen.ScaleUtils;


/**
 * Created by Dylan on 2017/12/11.
 */

public class TabItem extends LinearLayout {
    private TextView tabTitle;


    public TabItem(Context context) {
        this(context, null);
    }

    public TabItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        tabTitle = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        tabTitle.setLayoutParams(lp);
        tabTitle.setGravity(Gravity.CENTER);
        addView(tabTitle);
    }


    public TabItem setTabTitle(String title) {
        tabTitle.setText(title);
        return this;
    }

    public void setTextSize(float size) {
        tabTitle.setTextSize(size);
    }

    public void setTextColor(int color) {
        tabTitle.setTextColor(color);
    }

    public int getCurrentTextColor() {
        return tabTitle.getCurrentTextColor();
    }




    public TextView getTitleView() {
        return tabTitle;
    }

    public String getCurrentTitle() {
        return tabTitle.getText().toString();
    }

    public TabItem setTabIconRight(int resId) {
        ScaleUtils scaleUtil = new ScaleUtils(getContext());
        Drawable rightDrawable = ContextCompat.getDrawable(getContext(), resId);
        if (rightDrawable != null) {
            //必须设置 setBound 否则无法显示
            rightDrawable.setBounds(0, 0, scaleUtil.toScaleSize(40), scaleUtil.toScaleSize(40));
            tabTitle.setCompoundDrawables(null, null, rightDrawable, null);
        }
        return this;
    }

    public TabItem blodStyle(){
        tabTitle.getPaint().setFakeBoldText(true);
        return this;
    }

    public void setIconColorFilter(int color){
        setRightIconColorFilter(color);
    }

    private void setRightIconColorFilter(int color){
           Drawable[] drawables= tabTitle.getCompoundDrawables();
           if (drawables.length>0&&drawables[2]!=null){
               setTint(drawables[2],color);
           }
    }

    private static void setTint(Drawable drawable, int color) {
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }



}
