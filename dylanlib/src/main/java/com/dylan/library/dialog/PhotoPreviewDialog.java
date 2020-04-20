package com.dylan.library.dialog;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dylan.library.R;
import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.ContextUtils;
import com.dylan.library.widget.callback.AnimatorEndListener;
import com.dylan.library.widget.callback.OnTouchCallBack;
import com.dylan.library.widget.photoview.ScaleUpPhotoView;
import com.dylan.library.widget.photoview.ViewLocation;

/**
 * Author: Dylan
 * Date: 2020/3/1
 * Desc:
 */
public class PhotoPreviewDialog extends Dialog {
    private View rootView;
    private ScaleUpPhotoView scaleUpPhotoView;
    private ViewLocation viewLocation;

    public PhotoPreviewDialog(Context context) {
        this(context,0);
    }

    public PhotoPreviewDialog(Context context, int themeResId) {
        super(context, 0);
        setContentView(R.layout.dl_dialog_photo_preview);
        rootView=findViewById(R.id.rootView);
        scaleUpPhotoView=findViewById(R.id.ivShow);
        Window window=getWindow();
        window.setBackgroundDrawable(null);
        window.setDimAmount(0.5f);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams layoutParams=window.getAttributes();
        layoutParams .width= WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height=WindowManager.LayoutParams.MATCH_PARENT;
           setOnKeyListener(new OnKeyListener() {
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
        //单击消失
        scaleUpPhotoView.addOnTouchCallBack(new OnTouchCallBack() {
            @Override
            public void singleActionUp() {
                scaleUpPhotoView.startExitAnim(viewLocation, new AnimatorEndListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dismiss();
                    }
                });
            }
        });
    }

    public void setBackgroundColor(int color){
        rootView.setBackgroundColor(color);
    }



    public void show(ImageView fromView,int statusBarHeight){
        int[] p = new int[2];
        fromView.getLocationInWindow(p);
        ViewLocation viewLocation = new ViewLocation();
        viewLocation.setX(p[0]);
        viewLocation.setY(p[1] - statusBarHeight);
        viewLocation.setWidth(fromView.getMeasuredWidth());
        viewLocation.setHeight(fromView.getMeasuredHeight());
        Bitmap bitmap= BitmapHelper.getBitmapFromImageView(fromView);
        show(bitmap,viewLocation);
    }


    public void show(ImageView fromView){
        int[] p = new int[2];
        fromView.getLocationInWindow(p);
        ViewLocation viewLocation = new ViewLocation();
        viewLocation.setX(p[0]);
        viewLocation.setY(p[1] - ScreenUtils.getStatusBarHeight(ContextUtils.getActivity(fromView.getContext())));
        viewLocation.setWidth(fromView.getMeasuredWidth());
        viewLocation.setHeight(fromView.getMeasuredHeight());
        Bitmap bitmap= BitmapHelper.getBitmapFromImageView(fromView);
        show(bitmap,viewLocation);
    }

    public void show(Bitmap bitmap, ViewLocation location) {
        super.show();
        this.viewLocation=location;
        scaleUpPhotoView.setLocationInfo(location);
        scaleUpPhotoView.setImageBitmap(bitmap);

    }






}
