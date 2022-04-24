package com.dylan.mylibrary.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dylan.library.widget.DanMuView;
import com.dylan.mylibrary.R;


/**
 * Author: Dylan
 * Date: 2022/4/23
 * Desc:
 */

public class HotSearchDanMu extends DanMuView<String> {
    private ImageView ivPlatformLogo;
    private TextView tvFromSource;
    private TextView tvTitle;
    public HotSearchDanMu(@NonNull Context context) {
        super(context);
    }


    @Override
    public int getDanMuContentLayoutId() {
        return R.layout.item_hot_search_danmu;
    }

    @Override
    public void initView(View danMuView) {
        ivPlatformLogo=getDanMuView().findViewById(R.id.ivPlatformLogo);
        tvFromSource=getDanMuView().findViewById(R.id.tvFromSource);
        tvTitle=getDanMuView().findViewById(R.id.tvTitle);

    }

    @Override
    public void setData(String string) {
        tvTitle.setText(string);

    }


}
