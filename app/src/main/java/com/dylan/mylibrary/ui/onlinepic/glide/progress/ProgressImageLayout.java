package com.dylan.mylibrary.ui.onlinepic.glide.progress;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.onlinepic.PhotoView;

/**
 * Author: Dylan
 * Date: 2019/7/25
 * Desc:
 */

public class ProgressImageLayout extends RelativeLayout {
    private PhotoView photoView;
    private CircleProgressView progressView;

    private ProgressImageLayout(Context context) {
        super(context);
    }

    public ProgressImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        photoView=findViewById(R.id.photoView);
        progressView=findViewById(R.id.progressView);
    }


    public PhotoView getPhotoView() {
        return photoView;
    }

    public CircleProgressView getProgressView() {
        return progressView;
    }


}
