package com.dylan.mylibrary.ui;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.widget.TextView;

import com.dylan.common.BaseActivity;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.SpannableStringUtils;
import com.dylan.library.widget.DotLoadingTextView;
import com.dylan.mylibrary.R;

public class TextDotLoadingActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_text_dot_loading;
    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        DotLoadingTextView dotLoading = findViewById(R.id.tvContent);
        dotLoading.setEllipsize(TextUtils.TruncateAt.END);
        dotLoading.startLoading("仿写中");
    }
}
