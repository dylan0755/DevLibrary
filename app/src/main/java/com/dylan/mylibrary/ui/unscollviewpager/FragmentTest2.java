package com.dylan.mylibrary.ui.unscollviewpager;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dylan.mylibrary.R;

/**
 * Created by Dylan on 2018/6/23.
 */

public class FragmentTest2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_test,container,false);
        TextView tvTitle= (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("fragment2");
        return view;
    }
}
