package com.dylan.library.tab;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dylan.library.screen.ScaleUtils;


/**
 * Created by Dylan on 2017/12/11.
 */

public class TabItem extends LinearLayout {
    private TextView tabTitle;
    private ImageView tabIconRight;


    public TabItem(Context context) {
        this(context,null);
    }

    public TabItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        tabTitle =new TextView(getContext());
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        tabTitle.setLayoutParams(lp);
        tabTitle.setGravity(Gravity.CENTER);
        addView(tabTitle);
    }




    public TabItem setTabTitle(String title){
        tabTitle.setText(title);
        return this;
    }

    public void setTextSize(float size){
        tabTitle.setTextSize(size);
    }

    public void setTextColor(int color){
        tabTitle.setTextColor(color);
    }

    public int  getCurrentTextColor(){
        return tabTitle.getCurrentTextColor();
    }

    public TextView getTitleView(){
        return tabTitle;
    }

    public String getCurrentTitle(){
        return tabTitle.getText().toString();
    }

    public TabItem setTabIconRight(int resId){
        tabIconRight=new ImageView(getContext());
        ScaleUtils scaleUtil=new ScaleUtils(getContext());
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(scaleUtil.toScaleSize(40),scaleUtil.toScaleSize(40));
        lp.gravity= Gravity.CENTER;
        tabIconRight.setLayoutParams(lp);
        tabIconRight.setImageResource(resId);
        addView(tabIconRight);
        return this;
    }

    public ImageView getRightIconView(){
        return tabIconRight;
    }
}
