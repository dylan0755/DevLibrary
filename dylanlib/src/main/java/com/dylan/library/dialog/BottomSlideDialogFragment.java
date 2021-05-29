package com.dylan.library.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Author: Dylan
 * Date: 2021/05/29
 * Desc:
 */
public abstract class BottomSlideDialogFragment extends DialogFragment {
    protected View contentView;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,com.dylan.library.R.style.DLBottomSheetDialogStyle);

    }

    @Override
    public void onStart() {
        super.onStart();
        setWindowLayout(getDialog().getWindow());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        //去除标题栏
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.horizontalMargin = 0;
        getDialog().getWindow().setAttributes(params);
        contentView = inflater.inflate(getLayoutId(), container, false);
        hasInitContentView(contentView);
        onFragmentCreate();
        return contentView;
    }

    public abstract @LayoutRes
    int getLayoutId();

    public abstract  void  setWindowLayout(Window window);

    public abstract void onFragmentCreate();




    public View findViewById(int resId) {
        if (contentView == null) return null;
        return contentView.findViewById(resId);
    }

    @Override
    public Context getContext() {
        if (contentView != null) {
            return contentView.getContext();
        } else {
            return super.getContext();
        }

    }


    protected void hasInitContentView(View view){

    }



}
