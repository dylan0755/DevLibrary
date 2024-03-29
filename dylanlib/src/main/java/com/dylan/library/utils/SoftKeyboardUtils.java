package com.dylan.library.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.dylan.library.screen.ScreenUtils;

/**
 * Created by Dylan on 2016/10/9.
 */
public class SoftKeyboardUtils {


    public static void assistActivity(Activity activity) {
        new AdjustPan(activity);
    }



    public static void observeSoftKeyboard(Activity activity, final OnSoftKeyboardChangeListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener=  new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousKeyboardHeight = -1;
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.bottom - rect.top;
                int height = decorView.getHeight();
                int keyboardHeight = height - displayHeight;
                if (previousKeyboardHeight != keyboardHeight) {
                    boolean hide = (double) displayHeight / height > 0.8;
                    listener.onSoftKeyBoardChange(keyboardHeight, !hide);
                }

                previousKeyboardHeight = height;

            }
        };
        decorView.setTag(onGlobalLayoutListener);
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }


    public static void removeObserveSoftKeyboard(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ViewTreeObserver.OnGlobalLayoutListener listener= (ViewTreeObserver.OnGlobalLayoutListener) decorView.getTag();
            if (listener!=null){
                decorView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
            }

        }
    }

    public interface OnSoftKeyboardChangeListener {
        void onSoftKeyBoardChange(int softKeybardHeight, boolean visible);
    }


    public static  void hideSoftInput(View view){
        Context context=view.getContext();
        InputMethodManager manager= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }



    public static void showSoftInputAuto(final EditText editText){
        if (editText==null)return;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                String textLength=editText.getText().toString();
                editText.setSelection(textLength.length());
                InputMethodManager inputManager =
                        (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        },100);

    }



    public static void adustScrollView(final ScrollView scrollView, final int originalBottomMargin){
        if (scrollView==null)return;
        if ( ! (scrollView.getChildAt(0)instanceof LinearLayout))return;
        final Activity activity= ContextUtils.getActivity(scrollView.getContext());
        if (activity==null)return;




        LinearLayout wrapperLayout= (LinearLayout) scrollView.getChildAt(0);
        final View emptyView=new View(scrollView.getContext());
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
        emptyView.setLayoutParams(layoutParams);
        wrapperLayout.addView(emptyView);

        final int statusBarHeight= ScreenUtils.getStatusBarHeight(activity);
        observeSoftKeyboard(activity, new SoftKeyboardUtils.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int keyboardHeight, boolean visible) {
                if (visible){
                    if (ViewUtils.isOnBottom(scrollView)){
                        View focusView=activity.getCurrentFocus();
                        emptyView.getLayoutParams().height= keyboardHeight-statusBarHeight-originalBottomMargin;
                        emptyView.setVisibility(View.VISIBLE);
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        if (focusView!=null)focusView.requestFocus();
                    }else{
                        emptyView.setVisibility(View.GONE);
                    }

                }else{
                    emptyView.setVisibility(View.GONE);
                }

            }
        });
    }



    public static class AdjustPan{

        private View mChildOfContent;
        private int usableHeightPrevious;
        private FrameLayout.LayoutParams frameLayoutParams;
        private int contentHeight;
        private boolean isfirst = true;
        private Activity activity;
        private int statusBarHeight;


        private AdjustPan(Activity activity) {
            //获取状态栏的高度
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
            this.activity = activity;
            FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
            mChildOfContent = content.getChildAt(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mChildOfContent.setBackground(null);
            }
            //界面出现变动都会调用这个监听事件
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (isfirst) {
                        contentHeight = mChildOfContent.getHeight();//兼容华为等机型
                        isfirst = false;
                    }
                    possiblyResizeChildOfContent();
                }
            });

            frameLayoutParams = (FrameLayout.LayoutParams)
                    mChildOfContent.getLayoutParams();
        }

        //重新调整跟布局的高度
        private void possiblyResizeChildOfContent() {

            int usableHeightNow = computeUsableHeight();

            //当前可见高度和上一次可见高度不一致 布局变动
            if (usableHeightNow != usableHeightPrevious) {
                //int usableHeightSansKeyboard2 = mChildOfContent.getHeight();//兼容华为等机型
                int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
                int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                if (heightDifference > (usableHeightSansKeyboard / 4)) {
                    // keyboard probably just became visible
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        //frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
//                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference + statusBarHeight;
//                    } else {
//                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
//                    }
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                } else {
                    frameLayoutParams.height = contentHeight;
                }

                mChildOfContent.requestLayout();
                usableHeightPrevious = usableHeightNow;
            }
        }

        /**
         * 计算mChildOfContent可见高度     ** @return
         */
        private int computeUsableHeight() {
            Rect r = new Rect();
            mChildOfContent.getWindowVisibleDisplayFrame(r);
            return (r.bottom - r.top);
        }


    }
}
