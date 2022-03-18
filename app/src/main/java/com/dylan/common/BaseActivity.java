package com.dylan.common;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;


import com.dylan.library.utils.DensityFontUtils;
import com.dylan.library.utils.SoftKeyboardUtils;
import com.dylan.mylibrary.MyApplication;
import com.dylan.mylibrary.R;
import com.gyf.barlibrary.ImmersionBar;

import butterknife.ButterKnife;

/**
 * Author: Dylan
 * Date: 2021/1/25
 * Desc:
 */
public abstract class BaseActivity extends AppCompatActivity {
    private int mCurrentDp=375;
    public abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DensityFontUtils.setCustomActivityDensityInWidth(mCurrentDp,this, (Application) MyApplication.getApplication());
        setSoftInputMode();
        initCommonImmersionBar(false);
        if (getLayoutId()!=0)setContentView(getLayoutId());
        ButterKnife.bind(this);
        onActivityCreate(savedInstanceState);
    }

    public abstract void onActivityCreate(Bundle savedInstanceState);

    //禁止软键盘顶起布局
    protected void setSoftInputMode() {
        if (getWindow() != null)
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }


    protected void initCommonImmersionBar(boolean keyboardEnable){
        ImmersionBar immersionBar=ImmersionBar.with(this)
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                .statusBarColor(R.color.colorPrimary);
                if (keyboardEnable)immersionBar.keyboardEnable(true);
        immersionBar.init();
    }



    public void onHeadLeftButtonClick(View view) {
        SoftKeyboardUtils.hideSoftInput(view);
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DensityFontUtils.setCustomActivityDensityInWidth(mCurrentDp,this,MyApplication.getApplication());
    }


}
