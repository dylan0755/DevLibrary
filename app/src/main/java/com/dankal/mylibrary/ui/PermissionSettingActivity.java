package com.dankal.mylibrary.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dankal.mylibrary.R;
import com.dylan.library.utils.AppUtils;

/**
 * Created by Dylan on 2017/8/2.
 */

public class PermissionSettingActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gotopermission);
    }

    public void gotoPermission(View view) {
        AppUtils.gotoPermission(view.getContext());
    }

    public void gotoApplicationSetting(View view) {
        AppUtils.gotoApplicationSetting(view.getContext());
    }
}
