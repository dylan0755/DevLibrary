package com.dylan.mylibrary.ui.rebound;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2020/2/18
 * Desc:
 */
public class ReboundActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebound);
        findViewById(R.id.btnNormal).setOnClickListener(this);
        findViewById(R.id.btnRv).setOnClickListener(this);
        findViewById(R.id.btnSv).setOnClickListener(this);
        findViewById(R.id.btnLv).setOnClickListener(this);
        findViewById(R.id.btnVPager).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.btnNormal:
                intent=new Intent(this, ReboundLayoutActivity.class);
                startActivity(intent);
                break;
            case R.id.btnRv:
                intent=new Intent(this, ReboundRecyclerViewActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSv:
                intent=new Intent(this, ReboundScrollViewActivity.class);
                startActivity(intent);
                break;
            case R.id.btnLv:
                intent=new Intent(this, ReboundListViewActivity.class);
                startActivity(intent);
                break;
            case R.id.btnVPager:
                intent=new Intent(this, ReboundViewPagerActivity.class);
                startActivity(intent);
                break;
        }
    }
}
