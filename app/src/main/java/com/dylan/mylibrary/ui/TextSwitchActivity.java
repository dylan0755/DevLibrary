package com.dylan.mylibrary.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextSwitcher;

import com.dylan.mylibrary.R;
import com.dylan.library.utils.helper.VerticalTextSwitcherHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2019/3/20
 * Desc:
 */

public class TextSwitchActivity extends Activity {
    TextSwitcher textSwitcher;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textswitch);
        textSwitcher=findViewById(R.id.tvSwitcher);
        List<String> stringList=new ArrayList<>(5);
        for (int i=0;i<5;i++){
            stringList.add("活动"+(1+i));
        }
        VerticalTextSwitcherHelper textSwitcherUtils=new VerticalTextSwitcherHelper(textSwitcher,stringList);
        textSwitcherUtils.setSwitchDuration(3000);
        textSwitcherUtils.toStartSwitch();

    }
}
