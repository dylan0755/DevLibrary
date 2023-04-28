package com.dylan.mylibrary.ui.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dylan.mylibrary.domain.RestApi;


/**
 * Created by Dylan on 2016/9/20.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener{
    protected Context context;
    private View mContentView;
    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    protected RestApi mRestApi;
    protected String TAG;
    protected boolean hasLoaded;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        mContainer = container;
        context=getContext();
        mRestApi= RestApi.Factory.getInstance(RestApi.Factory.STRING_CONVERTER);
        TAG=getClass().getSimpleName();
        return onCreateView(savedInstanceState);
    }

    public abstract View onCreateView(Bundle savedInstanceState);
    public abstract void init();



    public View setContentView(int layoutId) {
        mContentView = mInflater.inflate(layoutId, mContainer, false);
        init();
        return mContentView;
    }

    public View setContentView(View view) {
        mContentView = view;
        init();
        return mContentView;
    }



    public View findViewById(int resId){
        return  mContentView.findViewById(resId);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
