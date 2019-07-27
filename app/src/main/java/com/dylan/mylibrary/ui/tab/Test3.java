package com.dylan.mylibrary.ui.tab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.widget.VideoView;

/**
 * Created by Dylan on 2016/11/10.
 */

public class Test3 extends Fragment {
    private View view;
    private VideoView  videoView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_test3,container,false);
        videoView= (VideoView) view.findViewById(R.id.vv);
        videoView.attachActivity(getActivity());
        return view;
    }
}
