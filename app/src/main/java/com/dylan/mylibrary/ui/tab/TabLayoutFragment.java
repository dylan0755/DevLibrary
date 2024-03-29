package com.dylan.mylibrary.ui.tab;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dylan.mylibrary.R;


/**
 * Created by Dylan on 2017/12/11.
 */

public class TabLayoutFragment extends Fragment {
    private View contentView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView=inflater.inflate(R.layout.fragment_tablayout,container,false);


        Bundle bundle=getArguments();
        if (bundle!=null){
            TextView textView= (TextView) contentView.findViewById(R.id.tv_pagename);
            String str=bundle.getString("page","");
            textView.setText(str);
        }


        return contentView;
    }
}
