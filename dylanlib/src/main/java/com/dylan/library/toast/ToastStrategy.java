package com.dylan.library.toast;

import android.app.AppOpsManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.toast.style.ToastBlackStyle;
import com.dylan.library.utils.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Author: Dylan
 * Date: 2021/03/10
 * Desc:
 */
public class ToastStrategy extends Handler implements IToastStrategy {
    public Application mApplication;
    /**
     * 延迟时间
     */
    private static final int DELAY_TIMEOUT = 200;

    /**
     * 显示吐司
     */
    private static final int TYPE_SHOW = 1;
    /**
     * 继续显示
     */
    private static final int TYPE_CONTINUE = 2;
    /**
     * 取消显示
     */
    private static final int TYPE_CANCEL = 3;

    //循环展示Toast,自定义时长
    private static final int TYPE_SHOW_TOAST_PERIOD=4;

    /**
     * 队列最大容量
     */
    private static final int MAX_TOAST_CAPACITY = 3;


    /**
     * 吐司队列
     */
    private volatile ArrayBlockingQueue<ToastMsg> mQueue;



    /**
     * 吐司对象
     */
    private Toast mToast;

    public ToastStrategy(Application application) {
        super(Looper.getMainLooper());
        mQueue = getToastQueue();
        mApplication=application;
        if (mToast!=null){
            mToast.cancel();
        }
        mToast=create();
        mToast.setView(createTextView(mApplication,new ToastBlackStyle(mApplication)));

    }

    public Toast create() {

        Toast toast;
        // 初始化吐司
        if (Build.VERSION.SDK_INT >= 30) {
            // 适配 Android 11 无法使用自定义 Toast 的问题
            // 官方文档：https://developer.android.google.cn/preview/features/toasts
            toast = new CustomToast(mApplication);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            // 处理 Android 7.1 上 Toast 在主线程被阻塞后会导致报错的问题
            toast = new SafeToast(mApplication);
        } else {
            boolean check =
                    // 对比不同版本的 NMS 的源码发现这个问题在 Android 9.0 已经被谷歌修复了
                    Build.VERSION.SDK_INT >= 29 ||
                            // 判断当前应用是否有通知栏权限，如果关闭会导致弹 Toast 无法显示
                            areNotificationsEnabled(mApplication) ||
                            // 判断当前是否是小米手机，因为只有小米手机做了特殊处理，就算没有通知栏权限也能弹吐司
                            "xiaomi".equals(Build.MANUFACTURER.toLowerCase());
            if (check) {
                // 检查通过，返回正常类型的 Toast 即可
                toast = new NormalToast(mApplication);
            } else {
                // 修复关闭通知栏权限后 Toast 不显示的问题
                toast = new CustomToast(mApplication);
            }
        }
        return toast;
    }



    @Override
    public void show(ToastMsg toastMsg) {
        if (mQueue.isEmpty() || !containContent(toastMsg)) {
            // 添加一个元素并返回true，如果队列已满，则返回false
            if (!mQueue.offer(toastMsg)) {
                // 移除队列头部元素并添加一个新的元素
                mQueue.poll();
                mQueue.offer(toastMsg);
            }
        }

            // 延迟一段时间之后再执行，因为在没有通知栏权限的情况下，Toast 只能显示当前 Activity
            // 如果当前 Activity 在 HJQToastUtils.show 之后进行 finish 了，那么这个时候 Toast 可能会显示不出来
            // 因为 Toast 会显示在销毁 Activity 界面上，而不会显示在新跳转的 Activity 上面
            sendEmptyMessageDelayed(TYPE_SHOW, DELAY_TIMEOUT);
    }


    public boolean containContent(ToastMsg toastMsg){
         if (mQueue.isEmpty())return false;
        Iterator<ToastMsg>  iterator=mQueue.iterator();
        while (iterator.hasNext()){
            ToastMsg msg=iterator.next();
            if (msg.getText().equals(toastMsg.getText())){
                return true;
            }
        }
        return false;
    }
    @Override
    public void cancel() {
          if (mToast!=null){
              mToast.cancel();
          }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case TYPE_SHOW:
                // 返回队列头部的元素，如果队列为空，则返回null
                ToastMsg toastMsg = mQueue.peek();
                if (toastMsg != null && EmptyUtils.isNotEmpty(toastMsg.getText())) {
                    if (mToast!=null){
                        mToast.cancel();
                        if (!(mToast instanceof CustomToast)){//使用系统的Toast,则重新创建
                            mToast=create();
                        }
                    }

                    mToast.setGravity(toastMsg.getGravity(),toastMsg.getOffsetX(),toastMsg.getOffsetY());
                    mToast.setText(toastMsg.getText());
                    if (toastMsg.getDuration() <= 2000) {
                        mToast.setDuration(Toast.LENGTH_SHORT);
                        mToast.show();
                    } else if (toastMsg.getDuration() == 3500) {
                        mToast.setDuration(Toast.LENGTH_LONG);
                        mToast.show();
                    } else {
                        mToast.setDuration(Toast.LENGTH_LONG);
                        showToastByPeriod();
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mToast.cancel();
                                removeMessages(TYPE_SHOW_TOAST_PERIOD);
                            }
                        },toastMsg.getDuration());
                    }

                    // 等这个 Toast 显示完后再继续显示，要加上一些延迟
                    // 不然在某些手机上 Toast 可能会来不及消失就要进行显示，这样是显示不出来的
                    sendEmptyMessageDelayed(TYPE_CONTINUE, DELAY_TIMEOUT);
                }
                break;
            case TYPE_CONTINUE:
                // 移除并返问队列头部的元素，如果队列为空，则返回null
                mQueue.poll();
                if (!mQueue.isEmpty()) {
                    sendEmptyMessage(TYPE_SHOW);
                }
                break;
            case TYPE_SHOW_TOAST_PERIOD:
                mToast.show();
                sendEmptyMessageDelayed(TYPE_SHOW_TOAST_PERIOD, 3500);
                break;
            case TYPE_CANCEL:
                mQueue.clear();
                mToast.cancel();
                break;
            default:
                break;
        }
    }


    private void showToastByPeriod(){
        mToast.show();
        sendEmptyMessageDelayed(TYPE_SHOW_TOAST_PERIOD, 3500);
    }

    /**
     * 获取吐司队列
     */
    public ArrayBlockingQueue<ToastMsg> getToastQueue() {
        return new ArrayBlockingQueue<>(MAX_TOAST_CAPACITY);
    }




    /**
     * 检查通知栏权限有没有开启
     * <p>
     * 参考 SupportCompat 包中的方法： NotificationManagerCompat.from(context).areNotificationsEnabled();
     */
    private static boolean areNotificationsEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getSystemService(NotificationManager.class).areNotificationsEnabled();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Method method = appOps.getClass().getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
                Field field = appOps.getClass().getDeclaredField("OP_POST_NOTIFICATION");
                int value = (Integer) field.get(Integer.class);
                return ((int) method.invoke(appOps, value, context.getApplicationInfo().uid, context.getPackageName())) == AppOpsManager.MODE_ALLOWED;
            } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | RuntimeException ignored) {
                return true;
            }
        } else {
            return true;
        }
    }


    /**
     * 根据样式生成默认的 TextView 对象
     */
    private static TextView createTextView(Context context, IToastStyle style) {
        TextView textView = new TextView(context);
        textView.setId(android.R.id.message);
        textView.setTextColor(style.getTextColor());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.getTextSize());

        // 适配布局反方向特性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setPaddingRelative(style.getPaddingStart(), style.getPaddingTop(), style.getPaddingEnd(), style.getPaddingBottom());
        } else {
            textView.setPadding(style.getPaddingStart(), style.getPaddingTop(), style.getPaddingEnd(), style.getPaddingBottom());
        }

        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        GradientDrawable drawable = new GradientDrawable();
        // 设置背景色
        drawable.setColor(style.getBackgroundColor());
        // 设置圆角大小
        drawable.setCornerRadius(style.getCornerRadius());

        // setBackground API 版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(drawable);
        } else {
            textView.setBackgroundDrawable(drawable);
        }

        // 设置 Z 轴阴影
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setZ(style.getZ());
        }

        // 设置最大显示行数
        if (style.getMaxLines() > 0) {
            textView.setMaxLines(style.getMaxLines());
        }

        return textView;
    }

}
