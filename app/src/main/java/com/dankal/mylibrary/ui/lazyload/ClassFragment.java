package com.dankal.mylibrary.ui.lazyload;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dankal.mylibrary.R;
import com.dylan.library.fragment.LazyFragment;

/**
 * Created by Dylan on 2018/2/8.
 */

public class ClassFragment extends LazyFragment {
   private TextView tvPage;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView=inflater.inflate(R.layout.fragment_lazyload,container,false);
        initView(contentView);
        return contentView;
    }

    private void initView(View contentView) {
        tvPage= (TextView) contentView.findViewById(R.id.tv_page);
    }

    @Override
    public void firstVisibleLoad() {
        final Bundle bundle=getArguments();
        if (bundle!=null){
            final int pageIndex=bundle.getInt("pageIndex");
            final ProgressDialog dialog=new ProgressDialog(getContext());
            dialog.setMessage("加载中...");
            dialog.show();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    tvPage.setText("第"+pageIndex+"页");
                }
            },2000);
        }

    }
}
