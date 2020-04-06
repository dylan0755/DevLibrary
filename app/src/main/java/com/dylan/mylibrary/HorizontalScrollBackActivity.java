package com.dylan.mylibrary;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;

import com.dylan.library.widget.HorizontalScrollBackLayout;

/**
 * Created by Dylan on 2017/11/7.
 */

public class HorizontalScrollBackActivity extends Activity implements HorizontalScrollBackLayout.OnActivitySmoothCallBack{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontalscrollback);
        HorizontalScrollBackLayout backLayout= (HorizontalScrollBackLayout) findViewById(R.id.rootView);
        backLayout.setonActivitySmoothCallBack(this);
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public Window attchActivityWindow() {
        return getWindow();
    }
}
