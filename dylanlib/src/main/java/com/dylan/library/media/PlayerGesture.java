package com.dylan.library.media;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.PointF;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dylan.library.R;
import com.dylan.library.utils.Logger;


/**
 * @author Dylan
 */
public class PlayerGesture {
    private static final int MSG_HIDE = 7262;
    private Activity mActivity;
    private SoundLightSpeedView mSoundLightSpeedView;
    private View anchorView;
    private PopupWindow mSLwindow;
    private PointF downPoint = new PointF();
    private WindowManager.LayoutParams params;
    private int mMeasureWidth;
    private boolean isSound;
    private boolean isChangeVertical;//纵向滑动
    private boolean isChangeHorization;//横向滑动
    private AudioManager audioManager;
    private int maxVolume;
    private int currentVolume;
    private float currentBrightness = -1;
    private int soundTounchProgress = -1;
    private float distanceX, distanceY;
    private int maxTouchHeight;
    private int touchSlop;//触发移动的最小距离
    //如滑动屏幕宽度的30%, 视频总长*0.3f*widthDurationRatio，用来控制手势控制快进快退的幅度
    private float widthDurationRatio = 0.3f;
    private boolean soundonLeft;
    private boolean soundMuteIconEnable = true;
    private long currentPosition;
    private long totalDuration;
    private long recordProgress;
    private Handler mHandler;

    public PlayerGesture(Context context, View anchorView) {
        this.anchorView = anchorView;
        mSLwindow = new PopupWindow(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mSoundLightSpeedView = new SoundLightSpeedView(context);
        mSoundLightSpeedView.getBackground().setAlpha(200);
        mSLwindow.setContentView(mSoundLightSpeedView);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        initAudioManager(context);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_HIDE) {
                    dismiss();
                }
            }
        };
    }



    public void setAnchorView(View anchorView) {
        this.anchorView = anchorView;
    }

    private void initAudioManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        // 获取最大音乐音量
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }


    public void attachActivity(Activity activity) {
        mActivity = activity;
        //获取系统亮度,设置初始的屏幕亮度与系统一致
        params = mActivity.getWindow().getAttributes();
        params.screenBrightness = getSystemBrightness(mActivity) * 1.0f / 255;
    }

    public void setMeasureWidth(int measureWidth) {
        mMeasureWidth = measureWidth;
    }


    public void onTouchEvent(MotionEvent event) {
        if (anchorView == null || mActivity == null || mMeasureWidth == 0) return;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp();
                break;
        }

    }

    private void onActionDown(MotionEvent event) {
        if (anchorView == null || mSoundLightSpeedView == null) return;
        maxTouchHeight = (int) (anchorView.getMeasuredHeight() * 0.7f);
        downPoint.x = event.getRawX();
        downPoint.y = event.getRawY();
        //判断触摸位置
        if (event.getX() > mMeasureWidth / 2) {  //点击在右边
            if (soundonLeft) {
                currentBrightness = params.screenBrightness;
                mSoundLightSpeedView.showLight(R.drawable.video_player_brightness_8);
            } else {
                isSound = true;
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                soundTounchProgress = (int) ((currentVolume * 1.0f / maxVolume) * 100);
                if (soundMuteIconEnable) {
                    if (currentVolume == 0) {
                        mSoundLightSpeedView.showSound(R.drawable.video_player_volume_no);
                    } else {
                        mSoundLightSpeedView.showSound(R.drawable.video_player_volume_100);
                    }
                } else {
                    mSoundLightSpeedView.showSound(R.drawable.video_player_volume_100);
                }
            }
        } else {//点击在左边
            if (soundonLeft) {
                isSound = true;
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                soundTounchProgress = (int) ((currentVolume * 1.0f / maxVolume) * 100);
                if (soundMuteIconEnable) {
                    if (currentVolume == 0) {
                        mSoundLightSpeedView.showSound(R.drawable.video_player_volume_no);
                    } else {
                        mSoundLightSpeedView.showSound(R.drawable.video_player_volume_100);
                    }
                } else {
                    mSoundLightSpeedView.showSound(R.drawable.video_player_volume_100);
                }
            } else {
                currentBrightness = params.screenBrightness;
                mSoundLightSpeedView.showLight(R.drawable.video_player_brightness_8);
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void onActionMove(MotionEvent event) {
        float deX = downPoint.x - event.getRawX();
        float deY = downPoint.y - event.getRawY();

        //在没有横向滑动的情况下，触发纵向滑动或者已经处于纵向滑动则调节 亮度和音频
        if (Math.abs(deY) > Math.abs(deX) && !isChangeHorization) {
            if (!isShowing()) {//没有显示的情况下
                if (Math.abs(distanceY) > touchSlop) {
                    show();//显示弹窗
                    changedSoundOrLight();
                }
            } else {
                changedSoundOrLight();
            }
        } else {
            if (Math.abs(deX) > Math.abs(deY) && !isChangeVertical) {
                if (!isShowing()) {
                    if (Math.abs(distanceX) > touchSlop) {
                        show();//显示弹窗
                        changeSpeed();
                    }
                } else {
                    changeSpeed();
                }
            }

        }
        distanceX += deX;
        distanceY += deY;
        downPoint.x = event.getRawX();
        downPoint.y = event.getRawY();

    }


    private void onActionUp() {
        isSound = false;
        distanceY = 0;
        distanceX = 0;
        currentVolume = -1;
        if (isChangeHorization) {//水平滑动
            if (mGestureListener != null) mGestureListener.seekTo(recordProgress);
        }
        isChangeVertical = false;
        isChangeHorization = false;
        //置零，保证每次触摸滑动都重新获取进度
        currentPosition = 0;
        recordProgress = 0;
        totalDuration = 0;
        Message message = Message.obtain();
        message.what = MSG_HIDE;
        mHandler.sendMessageDelayed(message, 500);
    }

    /**
     * 改变亮度和声音
     */
    private void changedSoundOrLight() {
        isChangeVertical = true;
        isChangeHorization = false;
        if (!isSound) {//改变亮度
            float percent = distanceY / maxTouchHeight;
            params.screenBrightness = currentBrightness + percent;
            if (params.screenBrightness > 1.0f)
                params.screenBrightness = 1.0f;
            else if (params.screenBrightness < 0.01f)
                params.screenBrightness = 0.01f;
            mActivity.getWindow().setAttributes(params);
            float ratio = params.screenBrightness;
            mSoundLightSpeedView.setLightProgress((int) (ratio * 100));
            //  Log.e("PlayerGesture", "onActionMove: " + ratio);
            //声音
        } else {

            //显示进度，这里显示的进度使用触摸滑动的距离而非 音频进度，
            // 因为音频进度每次只能加1，换成进度条显示就是，10,20,30,xxxxx,滑动上视觉不够顺畅
            float percent = (distanceY / maxTouchHeight);
            int newProgress = (int) (soundTounchProgress + percent * 100);
            if (newProgress < 0) newProgress = 0;
            if (newProgress > 100) newProgress = 100;
            mSoundLightSpeedView.setSoundText(newProgress);

            //改变音量
            int index = (int) (percent * maxVolume) + currentVolume;
            if (index > maxVolume) index = maxVolume;
            else if (index < 0) index = 0;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, AudioManager.FLAG_PLAY_SOUND);
            mSoundLightSpeedView.showSound(R.drawable.video_player_volume_100);

            if (soundMuteIconEnable){
                float ratio = index * 1.0f / maxVolume;
                int per = (int) (ratio * 100);
                if (per == 0) mSoundLightSpeedView.showSound(R.drawable.video_player_volume_no);
            }



        }
    }


    /**
     * 改变播放进度
     */
    private void changeSpeed() {
        isChangeHorization = true;
        isChangeVertical = false;
        mSoundLightSpeedView.showSpeed();
        //排除触摸滑动过程中间停住的情况
        long targetPosition;
        float ratio = Math.abs(distanceX) / mMeasureWidth;
        if (distanceX < 0) {//向右滑动
            targetPosition = (long) (currentPosition + totalDuration * ratio * widthDurationRatio);
        } else {//向左滑动
            targetPosition = (long) (currentPosition - totalDuration * ratio * widthDurationRatio);
        }
        if (currentPosition < 0) currentPosition = 0;
        if (targetPosition < 0) targetPosition = 0;
        recordProgress = targetPosition;
        mSoundLightSpeedView.showSpeed();
    }


    private void show() {
        if (!mSLwindow.isShowing()) mSLwindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
        else mHandler.removeMessages(MSG_HIDE);
    }


    private void dismiss() {
        mSLwindow.dismiss();
    }

    public boolean isShowing() {
        return mSLwindow.isShowing();
    }

    //是否设置音量在左边控制
    public void setSoundOnLeft(boolean bl) {
         soundonLeft = bl;
    }

    //音量为0，是否显示静音
    public void setSoundMuteIconEnable(boolean bl) {
           soundMuteIconEnable=bl;
    }

    //用来控制手势滑动的快进，快退幅度大小
    public void setWidthDurationRatio(float percent) {
        if (percent > 1) percent = 1;
        widthDurationRatio = percent;
    }



    public void setBackgroundAlpa(int value) {
        mSoundLightSpeedView.getBackground().setAlpha(value);
    }





    private class SoundLightSpeedView extends LinearLayout {
        private ImageView ivSound, ivLight;
        private TextView tvSpeed;
        private LinearLayout llSound, llLight, llSpeed;
        private ProgressBar pbSound, pbLight, pbSpeed;
        private View mContentView;

        public SoundLightSpeedView(Context context) {
            this(context, null);
        }

        public SoundLightSpeedView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            setBackgroundDrawable(getResources().getDrawable(R.drawable.dl_shape_soundlight));
            initView(context);
        }


        private void initView(Context context) {
            mContentView = LayoutInflater.from(context).inflate(R.layout.dl_sound_light_speed, this, false);
            ivSound = mContentView.findViewById(R.id.ivSound);
            ivLight = mContentView.findViewById(R.id.ivLight);

            tvSpeed = mContentView.findViewById(R.id.tvSpeed);
            llLight = mContentView.findViewById(R.id.llLight);
            llSound = mContentView.findViewById(R.id.llSound);
            llSpeed = mContentView.findViewById(R.id.llSpeed);
            pbSound = mContentView.findViewById(R.id.pbSound);
            pbSpeed = mContentView.findViewById(R.id.pbSpeed);
            pbLight = mContentView.findViewById(R.id.pbLight);
            addView(mContentView);
        }

        public void showSound(int redId) {
            llSound.setVisibility(VISIBLE);
            llLight.setVisibility(GONE);
            llSpeed.setVisibility(GONE);
            ivSound.setImageResource(redId);
        }

        public void showLight(int redId) {
            llLight.setVisibility(VISIBLE);
            llSound.setVisibility(GONE);
            llSpeed.setVisibility(GONE);
            ivLight.setImageResource(redId);
        }

        public void showSpeed() {
            llSpeed.setVisibility(VISIBLE);
            llLight.setVisibility(GONE);
            llSound.setVisibility(GONE);
            if (mGestureListener != null) {
                if (totalDuration == 0 && currentPosition == 0) {
                    currentPosition = mGestureListener.getCurrentPostion();
                    totalDuration = mGestureListener.getDuration();
                    recordProgress = currentPosition;
                }
            }

            tvSpeed.setText(MediaTools.getDurationMinuteFormat(recordProgress));
            pbSpeed.setProgress((int) (recordProgress * 100.0f / totalDuration));
        }


        public void setSoundText(int progress) {
            pbSound.setProgress(progress);
        }

        public void setLightProgress(int progress) {
            pbLight.setProgress(progress);
        }
    }


    private OnPlayerGestureListener mGestureListener;

    public interface OnPlayerGestureListener {
        long getDuration();

        long getCurrentPostion();


        void seekTo(long position);
    }


    public void setOnPlayerSpeedGestureListener(OnPlayerGestureListener listener) {
        mGestureListener = listener;
    }


    public static int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

}
