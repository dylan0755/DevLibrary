package com.dylan.library.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.dylan.library.R;
import com.dylan.library.screen.ScreenUtils;

/**
 * Created by Dylan on 2017/10/21.
 */

public abstract class DLTransParentDialog extends Dialog {
    private View mContentView;
    private   int  ORIENTATION_STATE;

    public DLTransParentDialog(@NonNull Context context) {
        super(context, R.style.transparentDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        mContentView = inflater.inflate(getLayoutId(), null, false);
        controllWindowWidth();
        setContentView(mContentView);
        setCanceledOnTouchOutside(true);
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onCanCelCallBack(dialogInterface);
            }
        });
    }


    public View findViewById(int resId) {
        if (mContentView != null)
            return mContentView.findViewById(resId);
        return null;
    }


    @LayoutRes
    public abstract int getLayoutId();

    public void onCanCelCallBack(DialogInterface dialogInterface){

    }


    /**
     * 横竖屏场景下控制Dialog的宽度
     */
    private void controllWindowWidth(){
        Context context=mContentView.getContext();
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (!isPotrait()){
                Window window = getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                wlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                wlp.width = (int) (ScreenUtils.getScreenWidth(context) * 0.85f);
                window.setAttributes(wlp);
                ORIENTATION_STATE=Configuration.ORIENTATION_PORTRAIT;
            }
        } else {
            if (!isLandScape()){
                Window window = getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.CENTER;
                wlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                wlp.width = (int) (ScreenUtils.getScreenWidth(context) * 0.60f);
                window.setAttributes(wlp);
                ORIENTATION_STATE=Configuration.ORIENTATION_LANDSCAPE;
            }
        }
    }


    public boolean isPotrait(){
        if (ORIENTATION_STATE==Configuration.ORIENTATION_PORTRAIT)return true;
        else
            return false;
    }

    private boolean isLandScape(){
        if (ORIENTATION_STATE==Configuration.ORIENTATION_LANDSCAPE)return true;
        else
            return false;
    }
    @Override
    public void show() {
       controllWindowWidth();
        super.show();
    }
}
