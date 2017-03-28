package com.dylan.library.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by Dylan on 2017/3/28.
 */

public abstract class DLPopupWindow extends PopupWindow {
    private View contentView;

    public DLPopupWindow(Context context) {
        super(context);
        contentView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
    }

    public abstract void initView();
    public abstract int getLayoutId();

    public View findViewById(int resId) {
        return contentView.findViewById(resId);
    }



    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        if (Build.VERSION.SDK_INT < 24) {
            super.showAsDropDown(anchor);
        }else{
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            int y = location[1];
            int height = y + anchor.getHeight();
            showAtLocation(anchor, Gravity.NO_GRAVITY, 0, height);
        }
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT < 24) {
            super.showAsDropDown(anchor,xoff,yoff);
        }else{
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            int y = location[1];
            int height = y + anchor.getHeight()+yoff;
            showAtLocation(anchor, Gravity.NO_GRAVITY, xoff, height);
        }
    }
}
