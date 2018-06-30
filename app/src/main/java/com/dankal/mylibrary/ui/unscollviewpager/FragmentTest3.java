package com.dankal.mylibrary.ui.unscollviewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dankal.mylibrary.R;

/**
 * Created by Dylan on 2018/6/23.
 */

public class FragmentTest3 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_test,container,false);
        TextView tvTitle= (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("fragment3");
        return view;
    }
}
