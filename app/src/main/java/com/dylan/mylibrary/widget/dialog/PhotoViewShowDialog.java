package com.dylan.mylibrary.widget.dialog;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.widget.callback.AnimatorEndListener;
import com.dylan.library.widget.photoview.ScaleUpPhotoView;
import com.dylan.library.widget.photoview.ViewLocation;
import com.dylan.library.widget.photoview.callback.SimpleOnScaleTransAnimListener;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2020/3/1
 * Desc:
 */
public class PhotoViewShowDialog extends Dialog {
    private ScaleUpPhotoView scaleUpPhotoView;
    private ViewLocation viewLocation;

    public PhotoViewShowDialog( Context context) {
        this(context,0);
    }

    public PhotoViewShowDialog( Context context, int themeResId) {
        super(context, 0);
        setContentView(R.layout.dialog_photoview_show);
        scaleUpPhotoView=findViewById(R.id.ivShow);
        Window window=getWindow();
        window.setBackgroundDrawable(null);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams layoutParams=window.getAttributes();
        layoutParams .width= WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height=WindowManager.LayoutParams.MATCH_PARENT;
           setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                     scaleUpPhotoView.startExitAnim(viewLocation, new AnimatorEndListener() {
                         @Override
                         public void onAnimationEnd(Animator animation) {
                             dismiss();
                         }
                     });

                }
                return true;
            }
        });
    }


    public void show(Bitmap bitmap, ViewLocation location) {
        super.show();
        this.viewLocation=location;
        scaleUpPhotoView.setLocationInfo(location);
        scaleUpPhotoView.setImageBitmap(bitmap);

    }




}
