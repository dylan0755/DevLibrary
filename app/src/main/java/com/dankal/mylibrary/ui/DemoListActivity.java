package com.dankal.mylibrary.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dankal.mylibrary.R;
import com.dankal.mylibrary.ui.customtitle.CustomTittleUitlActivity;
import com.dankal.mylibrary.ui.date.DateTestActivity;
import com.dankal.mylibrary.ui.edittext.EditNumberActivity;
import com.dankal.mylibrary.ui.tab.TabActivity;

/**
 * Created by Dylan on 2016/12/16.
 */

public class DemoListActivity extends Activity implements View.OnClickListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demolist);
        initEvent();
    }

    private void initEvent() {
        findViewById(R.id.btn_mainactivity).setOnClickListener(this);
        findViewById(R.id.btn_cutomTitleUtil).setOnClickListener(this);
        findViewById(R.id.btn_date).setOnClickListener(this);
        findViewById(R.id.btn_editnumber).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_mainactivity:
                intent=new Intent(this,TabActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_cutomTitleUtil:
                 intent=new Intent(this, CustomTittleUitlActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_date:
                intent=new Intent(this, DateTestActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_editnumber:
                intent=new Intent(this, EditNumberActivity.class);
                startActivity(intent);
                break;

        }
    }
}
