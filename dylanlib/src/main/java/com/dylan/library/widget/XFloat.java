package com.dylan.library.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.dylan.library.utils.ContextUtils;


public class XFloat {

    private View contentView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    public void show(View contentView, int offsetX, int offsetY) {
        show(contentView, Gravity.LEFT | Gravity.TOP, offsetX, offsetY,false);
    }

    public void show(Context context,int layoutId, int offsetX, int offsetY) {
        View contentView=View.inflate(context,layoutId,new FrameLayout(context));
        show(contentView, Gravity.LEFT | Gravity.TOP, offsetX, offsetY,false);
    }

    public void show(View contentView, int offsetX, int offsetY,boolean canDrag) {
        show(contentView, Gravity.LEFT | Gravity.TOP, offsetX, offsetY,canDrag);
    }

    public void show(Context context,int layoutId, int offsetX, int offsetY,boolean canDrag) {
        View contentView=View.inflate(context,layoutId,new FrameLayout(context));
        show(contentView, Gravity.LEFT | Gravity.TOP, offsetX, offsetY,canDrag);
    }

    public void showCenter(View contentView,boolean canDrag) {
        show(contentView, Gravity.CENTER, 0, 0,canDrag);
    }

    public void showCenter(Context context,int layoutId,boolean canDrag) {
        View contentView=View.inflate(context,layoutId,new FrameLayout(context));
        show(contentView, Gravity.CENTER, 0, 0,canDrag);
    }

    @SuppressLint("RestrictedApi")
    public void show(View contentView, int gravity, int offsetX, int offsetY,boolean canDrag) {
        if (contentView==null)return;
        this.contentView=contentView;
        Context context=contentView.getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                Activity activity = ContextUtils.getActivity(context);
                if (activity != null) {
                    activity.startActivityForResult(intent, 100);
                } else {
                    context.startActivity(intent);
                }
                return;
            }
        }

        if (canDrag){
            contentView.setOnTouchListener(new FloatingOnTouchListener());
        }



        layoutParams = new WindowManager
                .LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, 0, 0, PixelFormat.TRANSPARENT);
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity =gravity;
        layoutParams.x = offsetX;
        layoutParams.y = offsetY;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(contentView, layoutParams);
    }


    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int startX;
        private int startY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - startX;
                    int movedY = nowY - startY;
                    startX = nowX;
                    startY = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;

                    // 更新悬浮窗控件布局
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    public void dismiss(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (contentView!=null&&contentView.isAttachedToWindow()&&windowManager!=null){
                windowManager.removeView(contentView);
                windowManager=null;
                contentView=null;
            }
        }
    }
}
