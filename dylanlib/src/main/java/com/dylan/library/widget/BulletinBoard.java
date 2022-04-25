package com.dylan.library.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2017/5/2.
 */

public class BulletinBoard extends FrameLayout {
    //30毫秒刷新3dp在视觉上移动比较平滑
    private static final int INTERVAL_TIME = 30;//线程刷新间隔时间
    private static int MOVE_SPACE = 3; //每次刷新时向左平移多长距离
    private TextView mTextView;
    private int heightMeasureSpec;
    private int parentWidth;
    private boolean hasMeasure;
    private boolean runFlag = true;
    private int scrollRange;//可移动的距离
    private int needTime; //当前消息滚动完毕需要的时间
    private int currentTime;//当前滚动消息已经使用了多少时间
    private int currentMessageIndex;//当前显示的是第几个消息
    private List<String> insertList;
    private List<String> messageQuenue;
    private ScrollThread mScrollThread;
    private Handler mHandler;
    private MoveCallBack callback;

    public BulletinBoard(@NonNull Context context) {
        this(context, null);
    }

    public BulletinBoard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTextView=new TextView(context,attrs);
        LayoutParams lp=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        //layout_gravity
        lp.gravity= Gravity.CENTER_VERTICAL;
        mTextView.setLayoutParams(lp);
        //gravity
        mTextView.setGravity(Gravity.CENTER_VERTICAL);
        mTextView.setSingleLine();
        mTextView.setTextColor(Color.WHITE);
        mTextView.setBackgroundColor(Color.TRANSPARENT);
        addView(mTextView);

        MOVE_SPACE = new ScaleUtil(context).toScaleSize(3);//在不同分辨率下移动速度一致
        insertList = new ArrayList<>();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1001) { //切换下一条消息
                    reset();
                    if (callback != null) callback.onNest(currentMessageIndex, messageQuenue.get(currentMessageIndex));
                } else if (msg.what == 1002) {//所有消息已经滚动完毕。
                    stop();
                    currentMessageIndex = 0;
                    currentTime = 0;
                    messageQuenue.clear();
                    //原消息数组已经显示完毕，那么判断是否有新消息，如果没有则回调Finish
                    if (!insertList.isEmpty()) {
                        messageQuenue.addAll(insertList);
                        insertList.clear();
                        try {
                            restart(messageQuenue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (callback != null) callback.onFinish();
                    }
                }else{
                    scrollBy(MOVE_SPACE, 0);
                    currentTime += INTERVAL_TIME;
                }

            }
        };
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.heightMeasureSpec = heightMeasureSpec;
        parentWidth = getMeasuredWidth();
        measureTextView();
        if (!hasMeasure) {
            if (messageQuenue != null && !messageQuenue.isEmpty()) {
                scrollToParentRight();
                caculateScrollTime();
                startScroller();
                hasMeasure = true;
            }
        }
    }



    //重置
    private void reset() {
        final String message = messageQuenue.get(currentMessageIndex); //重设消息文本
        mTextView.setText(message);
        measureTextView();//重新计算滚动的距离
        currentTime = 0;//重置时间
        scrollTo(0, 0);
        scrollToParentRight();
        caculateScrollTime();//重新计算滚动时间
    }

    /**
     * 定位到最右边
     */
    private void scrollToParentRight() {
        scrollBy(-parentWidth, 0);
    }

    /**
     * 测量和设置TextView的宽度
     */
    public void measureTextView() {
        int childMeasureSpec = MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mTextView.measure(childMeasureSpec, heightMeasureSpec);
        scrollRange = mTextView.getMeasuredWidth() + parentWidth;
    }

    /**
     * 计算滚动的时间
     */
    public void caculateScrollTime() {
        needTime = scrollRange / MOVE_SPACE * INTERVAL_TIME;
    }

    /**
     * 开启线程
     */
    private void startScroller() {
        mScrollThread = new ScrollThread();
        mScrollThread.start();
    }


    public void appendMessages(List<String> messageList) throws Exception {
        if (messageList == null || messageList.isEmpty()) {
            throw new Exception("messageList is empty");
        }
        if (mScrollThread == null) {//未曾启动
            firstStart(messageList);
        } else {//启动过了，那么线程此时是暂停还是运行状态
            if (runFlag) {//正在滚动，直接往备用集合中添加数据,因为messageQuenue在循环线程中使用，不能直接往里面塞数据
                insertList.addAll(messageList);
            } else {//已经滚动完了，需要重新释放线程锁
                restart(messageList);
            }
        }
    }


    /**
     * 第一次启动
     *
     * @param messageList
     */
    private void firstStart(List<String> messageList) {
        messageQuenue = messageList;
        mTextView.setText(messageQuenue.get(0));
    }

    private void restart(List<String> messageList) throws Exception {
        messageQuenue = messageList;
        reset();
        mScrollThread.restart(messageList);
    }


    public void stop() {
        runFlag = false;
    }


    public class ScrollThread extends Thread {

        public void restart(List<String> messageList) throws Exception {

            //线程恢复
            synchronized (ScrollThread.this) {
                runFlag = true;
                notifyAll();
            }
        }


        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (ScrollThread.this) {
                        if (!runFlag) {
                            wait();
                        }
                    }
                    if (currentTime >= needTime) {//该条消息已经滚动显示完毕，判断是不是最后一条
                        if (currentMessageIndex == messageQuenue.size() - 1) {
                            mHandler.sendEmptyMessage(1002);
                        } else {
                            currentMessageIndex += 1;
                            mHandler.sendEmptyMessage(1001);
                        }
                    } else {
                        mHandler.sendEmptyMessage(1000);
                    }
                    Thread.sleep(INTERVAL_TIME);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public interface MoveCallBack {
        void onNest(int position, String message); //该方法在切换的时候才会相应，即每组消息中的第二个数据开始

        void onFinish();
    }

    public void setMoveCallBack(MoveCallBack callback) {
        this.callback = callback;
    }


    private static class ScaleUtil {
        private final int BASE_WIDTH = 1080;
        private float BASE_RATIO = 1;

        public ScaleUtil(Context context) {
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                BASE_RATIO = 1.0f * context.getResources().getDisplayMetrics().widthPixels / BASE_WIDTH;
            else
                BASE_RATIO = 1.0f * context.getResources().getDisplayMetrics().heightPixels / BASE_WIDTH;
        }

        public int toScaleSize(int px) {
            return (int) (BASE_RATIO * px);
        }
    }

}
