package com.dylan.library.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Dylan on 2018/2/8.
 */

//Fragment生命周期中，setUserVisbleHint先于onCreateView执行
public abstract class LazyFragmentBase extends Fragment {
    private  boolean isViewCreated = false;
    private boolean isUIVisible = false;
    private boolean hasLoad = false;

    //UI加载完毕
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        toLoad();
    }



    //UI可见
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isUIVisible = true;
            toLoad();
        } else {
            isUIVisible = false;
        }
    }


    @Override
    public abstract View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    private void toLoad() {
        if (!isViewCreated || !isUIVisible || hasLoad) {
            return;
        }
        hasLoad=true;
        firstVisibleLoad();
    }

    public abstract void firstVisibleLoad();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        isUIVisible = false;
        hasLoad=false;
    }

}
