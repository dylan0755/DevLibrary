package com.dankal.mylibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dankal.mylibrary.ui.testactivity.CustomTittleUitlActivity;

/**
 * Created by Dylan on 2016/11/10.
 */

public class Test1 extends Fragment {
    private View contentView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView=inflater.inflate(R.layout.activity_test1,container,false);
        contentView.findViewById(R.id.tv_cutomTitleUtil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), CustomTittleUitlActivity.class);
                startActivity(intent);
            }
        });


        return contentView;
    }
}
