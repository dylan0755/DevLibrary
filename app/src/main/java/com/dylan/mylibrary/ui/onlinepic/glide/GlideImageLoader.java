package com.dylan.mylibrary.ui.onlinepic.glide;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dylan.library.utils.Logger;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.onlinepic.PhotoView;
import com.dylan.mylibrary.ui.onlinepic.glide.progress.CircleProgressView;
import com.dylan.mylibrary.ui.onlinepic.glide.progress.ProgressImageLayout;
import com.dylan.mylibrary.ui.onlinepic.glide.progress.ProgressInterceptor;
import com.dylan.mylibrary.ui.onlinepic.glide.progress.ProgressListener;

/**
 * Author: Dylan
 * Date: 2019/7/24
 * Desc:
 */

public class GlideImageLoader {

    public static void load(final ProgressImageLayout imageLayout, final String picUrl) {
        final CircleProgressView circleProgressView = imageLayout.getProgressView();
        circleProgressView.setBackgroundColor(Color.TRANSPARENT);
        circleProgressView.setVisibility(View.VISIBLE);
        //开始监听某个url
        ProgressInterceptor.addListener(picUrl, new ProgressListener() {
            @Override
            public void onProgress(final int progress) {
                //Log.e("download", progress + "");
                circleProgressView.setProgress(progress);
                if (progress == 100) {
                    circleProgressView.post(new Runnable() {
                        @Override
                        public void run() {
                            circleProgressView.setVisibility(View.GONE);
                        }
                    });
                }


            }
        });

      final PhotoView photoView= imageLayout.getPhotoView();
        //开始展示
        GlideApp.with(imageLayout.getContext())
                .asBitmap()
                .load(picUrl)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        //加载失败 移除监听
                        ProgressInterceptor.removeListener(picUrl);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        //成功 移除监听
                       // Log.e("onResourceReady", "url----" + model + "----" + dataSource);
                        ProgressInterceptor.removeListener(picUrl);
                        circleProgressView.setVisibility(View.GONE);
                        return false;
                    }
                }).into(photoView);
    }

}