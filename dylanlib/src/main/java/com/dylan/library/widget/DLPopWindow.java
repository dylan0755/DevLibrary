package com.dylan.library.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableRow;

import com.dylan.library.utils.ContextUtils;

/**
 * Created by Dylan on 2017/3/28.
 */

public abstract class DLPopWindow extends PopupWindow {
    private View contentView;
    private float dimAmount=0.3f;
    private boolean enableLight=true;

    public DLPopWindow(Context context) {
        super(context);
        LinearLayout parentLayout = new LinearLayout(context);
        contentView = LayoutInflater.from(context).inflate(getLayoutId(), parentLayout);
        setContentView(contentView);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable(context.getResources(),(Bitmap)null));
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        initView();
        super.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOff();
            }
        });

    }


    @Override
    public void setOnDismissListener(final OnDismissListener onDismissListener) {
          super.setOnDismissListener(new OnDismissListener() {
              @Override
              public void onDismiss() {
                  lightOff();
                  onDismissListener.onDismiss();
              }
          });
    }

    public abstract void initView();
    public abstract int getLayoutId();


    public View findViewById(int resId) {
        return contentView.findViewById(resId);
    }

    public View getContentView(){
        return contentView;
    }


    public void setDimAmount(@FloatRange(from = 0f,to = 1.0f) float dimAmount){
             this.dimAmount=dimAmount;
    }


    public void setEnableLight(boolean enableLight){
         this.enableLight=enableLight;
    }

    @Override
    public void showAsDropDown(View anchor) {
        lightOn();
        if (Build.VERSION.SDK_INT < 24) {
            super.showAsDropDown(anchor);
        }else{
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            int y = location[1];
            int height = y + anchor.getHeight();
            showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], height);
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
            showAtLocation(anchor, Gravity.NO_GRAVITY,location[0]+xoff, height);
        }
    }


    public void lightOn(){
        setActivityLight(dimAmount);
    }


    public void lightOn(float dimAmount){
        setActivityLight(dimAmount);
    }

    public void lightOff(){
        setActivityLight(0f);
    }


    private void setActivityLight(float light){
        if (!enableLight)return;
        if (contentView==null)return;
        Activity activity= ContextUtils.getActivity(contentView.getContext());
        if (activity==null)return;
        WindowManager.LayoutParams params=activity.getWindow().getAttributes();
        params.alpha=1-light;
        activity.getWindow().setAttributes(params);
    }
}
