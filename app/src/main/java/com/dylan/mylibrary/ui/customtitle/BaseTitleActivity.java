package com.dylan.mylibrary.ui.customtitle;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dylan.mylibrary.R;
import com.dylan.library.utils.CustomTitleUtil;

import butterknife.ButterKnife;

/**
 * Created by Dylan on 2016/12/10.
 */

public abstract class BaseTitleActivity extends AppCompatActivity {
    private TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme2);
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        ButterKnife.bind(this);
        CustomTitleUtil.init(this,R.layout.base_title);
        onInitData();
        findViewById(R.id.iv_top_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle= (TextView) findViewById(R.id.tv_base_title);
        tvTitle.setText(getActivityTitle());
    }


    public abstract String getActivityTitle();


     @LayoutRes
    public abstract int getLayoutId();

    public abstract void onInitData();
}
