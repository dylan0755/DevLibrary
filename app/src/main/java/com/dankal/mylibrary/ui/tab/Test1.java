package com.dankal.mylibrary.ui.tab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dankal.mylibrary.R;

/**
 * Created by Dylan on 2016/11/10.
 */

public class Test1 extends Fragment {
    private View contentView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView=inflater.inflate(R.layout.activity_test1,container,false);
        return contentView;
    }
}
