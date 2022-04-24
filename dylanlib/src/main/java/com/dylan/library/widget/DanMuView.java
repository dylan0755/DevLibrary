package com.dylan.library.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.thread.PauseResumeLoopThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2017/5/2.
 */

public abstract class DanMuView<T> extends FrameLayout {
    private static final int MSG_UPDATE=100;
    private static final int MSG_NEXT=101;
    private static final int MSG_LAST_ONE=102;

    //30毫秒刷新3dp在视觉上移动比较平滑
    private static final int INTERVAL_TIME = 30;//线程刷新间隔时间
    private static int MOVE_SPACE ; //每次刷新时向左平移多长距离
    private int heightMeasureSpec;
    private int parentWidth;
    private boolean isLoop=true;
    private int scrollRange;//可移动的距离
    private int needTime; //当前消息滚动完毕需要的时间
    private int currentTime;//当前滚动消息已经使用了多少时间
    private int currentMessageIndex;//当前显示的是第几个消息
    private List<T> insertList;
    private List<T> messageQuenue;
    private ScrollThreadRe mScrollThread;
    private Handler mHandler;
    private MoveCallBack callback;
    private View danMuView;
    List<DanMuView> danMuViewList;

    public void setDanMuViewList(List<DanMuView> danMuViewList) {
        this.danMuViewList = danMuViewList;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public DanMuView(@NonNull Context context) {
        this(context, null);
    }

    public DanMuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        createDanMu();

        MOVE_SPACE = new ScaleUtil(context).toScaleSize(6);//在不同分辨率下移动速度一致
        insertList = new ArrayList<>();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what ==MSG_NEXT ) { //切换下一条消息
                    reset();
                    if (callback != null) callback.onNest(currentMessageIndex, messageQuenue.get(currentMessageIndex));
                } else if (msg.what == MSG_LAST_ONE) {//所有消息已经滚动完毕。
                    //原消息数组已经显示完毕，那么判断是否有新消息，如果没有则回调Finish
                    if (!insertList.isEmpty()) {
                        messageQuenue.addAll(insertList);
                        insertList.clear();
                        try {
                            currentMessageIndex+=1;
                            reset();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {//重新开始
                        if (isLoop){
                            currentMessageIndex = 0;
                            currentTime = 0;
                            reset();
                        }else{
                            if (callback != null) callback.onFinish();
                        }
                    }
                }else{
                    danMuView.offsetLeftAndRight(-MOVE_SPACE);
                    currentTime += INTERVAL_TIME;
                }

            }
        };
    }

    public abstract int getDanMuContentLayoutId();

    public View getDanMuView() {
        return danMuView;
    }

    private void createDanMu() {
        danMuView = LayoutInflater.from(getContext()).inflate(getDanMuContentLayoutId(), new FrameLayout(getContext()));
        initView(danMuView);
        LayoutParams lp=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        danMuView.setLayoutParams(lp);
        addView(danMuView);
        danMuView.setClickable(true);
        danMuView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDanMuClickListener!=null)mDanMuClickListener.onClick(messageQuenue.get(currentMessageIndex));
                if (EmptyUtils.isNotEmpty(danMuViewList)){
                    for ( DanMuView danMuView:danMuViewList){
                         danMuView.pause();
                    }
                }

            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mScrollThread!=null&&mScrollThread.isPause()){
            return;
        }
        this.heightMeasureSpec = heightMeasureSpec;
        parentWidth = getMeasuredWidth();
        measureDanMu();
        caculateScrollTime();
        scrollToParentRight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mScrollThread!=null&&mScrollThread.isPause()){
            return;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    //重置
    private void reset() {
        setData(messageQuenue.get(currentMessageIndex));
        measureDanMu();//重新计算滚动的距离
        currentTime = 0;//重置时间
        danMuView.layout(0, danMuView.getTop(),
                danMuView.getMeasuredWidth(), danMuView.getBottom());
        scrollToParentRight();
        caculateScrollTime();//重新计算滚动时间
    }

    public List<T> getMessageQuenue() {
        return messageQuenue;
    }

    public int getCurrentMessageIndex() {
        return currentMessageIndex;
    }

    /**
     * 定位到最右边
     */

    private void scrollToParentRight() {
        danMuView.setTranslationX(parentWidth);
    }

    /**
     * 测量和设置TextView的宽度
     */
    public void measureDanMu() {
        int childMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        danMuView.measure(childMeasureSpec, heightMeasureSpec);
        scrollRange = danMuView.getMeasuredWidth() + parentWidth;
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
        mScrollThread = new ScrollThreadRe();
        mScrollThread.start();
    }


    public void appendMessages(List<T> messageList) throws Exception {
        if (messageList == null || messageList.isEmpty()) {
            throw new Exception("messageList is empty");
        }
        if (mScrollThread == null) {//未曾启动
            firstStart(messageList);
        } else {//启动过了，那么线程此时是暂停还是运行状态
            insertList.addAll(messageList);
        }
    }


    /**
     * 第一次启动
     *
     * @param danMuMsgList
     */
    private void firstStart(List<T> danMuMsgList) {
        messageQuenue = danMuMsgList;
        setData(danMuMsgList.get(0));
        danMuView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startScroller();
            }
        },500);


    }

    public abstract void initView(View danMuView);
    public abstract void setData(T t);


    public void resumeThread(){
        if (mScrollThread!=null){
            mScrollThread.resumeThread();
        }
    }
    public void pause() {
        if (mScrollThread!=null){
            mScrollThread.pauseThread();
        }
    }

    public void stop(){
        if (mScrollThread!=null){
            mScrollThread.exitThread();
        }
    }


    public class ScrollThreadRe extends PauseResumeLoopThread {

        @Override
        public void doRun() {
            if (currentTime >= needTime) {//该条消息已经滚动显示完毕，判断是不是最后一条
                if (currentMessageIndex == messageQuenue.size() - 1) {
                    mHandler.sendEmptyMessage(MSG_LAST_ONE);
                } else {
                    currentMessageIndex += 1;
                    mHandler.sendEmptyMessage(MSG_NEXT);
                }
            } else {
                mHandler.sendEmptyMessage(MSG_UPDATE);
            }
            try {
                Thread.sleep(INTERVAL_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public interface MoveCallBack<T> {
        void onNest(int position, T t); //该方法在切换的时候才会相应，即每组消息中的第二个数据开始

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

    private OnDanMuClickListener mDanMuClickListener;
    public interface OnDanMuClickListener<T>{
        void onClick(T t);
    }

    public void setOnDanMuClickListener(OnDanMuClickListener listener){
        mDanMuClickListener=listener;
    }

}
