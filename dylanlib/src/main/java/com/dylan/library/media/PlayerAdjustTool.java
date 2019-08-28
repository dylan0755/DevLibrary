package com.dylan.library.media;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dylan.library.R;


/**
 * @author Dylan
 *
 */
public class PlayerAdjustTool {
    private Context mContext;
    private Activity mActivity;
    private SoundLightView mSoundLightView;
    private View anchorView;

    private PopupWindow mSLwindow;
    private float mLightProgress = 0;
    private float downY;
    private WindowManager.LayoutParams params;
    private int mMeasureWidth;
    private boolean isSound;
    private AudioManager audioManager;
    private int maxVolume;
    private int currentVolume;
    private float moveY;
    private int initOffsetX;
    private int initOffsetY ;
    private int maxTouchHeight;
    private float mBrightness;
    private boolean soundonLeft;
    private ScaleUtil mScaleUtil;

    public PlayerAdjustTool(Context context, View anchorView) {
        mContext = context;
        this.anchorView = anchorView;
        mSLwindow = new PopupWindow(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mSoundLightView = new SoundLightView(context);
        mSoundLightView.getBackground().setAlpha(175);
        mSLwindow.setContentView(mSoundLightView);
        initOffsetX= mSoundLightView.getScaleWidth()/2;
        initOffsetY= mSoundLightView.getScaleHeight()/2;
        Log.e( "PlayerAdjustTool: ",""+initOffsetX+" initOffsetY "+initOffsetY );
        mScaleUtil = new ScaleUtil(context);
        initAudioManager(context);
    }

    private void initAudioManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        // 获取最大音乐音量
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }


    public void attachActivity(Activity activity) {
        mActivity = activity;
    }

    public void setMeasureWidth(int measureWidth){
        mMeasureWidth=measureWidth;
    }





    public void onTouchEvent(MotionEvent event){
        if (anchorView==null||mActivity==null||mMeasureWidth==0)return;
        switch (event.getAction()){
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
        if (anchorView == null || mSoundLightView == null) return;
        maxTouchHeight = (int) (anchorView.getMeasuredHeight() * 0.7f);
        downY = event.getY();
        //获取当前亮度
        if (mLightProgress == 0) {
            params = mActivity.getWindow().getAttributes();
            mLightProgress = params.screenBrightness;
        }
        //判断触摸位置
        if (event.getX() > mMeasureWidth / 2) {  //点击在右边
            if (soundonLeft) {
                mSoundLightView.setIcon(R.drawable.video_player_brightness_8);
            } else {
                isSound = true;
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (currentVolume == 0) {
                    mSoundLightView.setIcon(R.drawable.video_player_volume_no);
                } else {
                    mSoundLightView.setIcon(R.drawable.video_player_volume_100);
                }
            }
        } else {//点击在左边
            if (soundonLeft) {
                isSound = true;
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (currentVolume == 0) {
                    mSoundLightView.setIcon(R.drawable.video_player_volume_no);
                } else {
                    mSoundLightView.setIcon(R.drawable.video_player_volume_100);
                }
            } else {
                mSoundLightView.setIcon(R.drawable.video_player_brightness_8);
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void onActionMove(MotionEvent event) {
        float deY = downY - event.getY();
        moveY += deY;
        downY = event.getY();
        //亮度
        if (!isSound) {
            if (Math.abs(deY) > dip2px(2)) {
                show();
                float percent = moveY / maxTouchHeight;
                if (mBrightness < 0) {
                    mBrightness = params.screenBrightness;
                    if (mBrightness <= 0.00f) mBrightness = 0.00f;
                }
                params.screenBrightness = mBrightness + percent;
                if (params.screenBrightness > 1.0f)
                    params.screenBrightness = 1.0f;
                else if (params.screenBrightness < 0.01f)
                    params.screenBrightness = 0.01f;
                mActivity.getWindow().setAttributes(params);
                float ratio = params.screenBrightness;
                mSoundLightView.setText((int) (ratio * 100) + "%");
            }
            //声音
        } else {
            if (Math.abs(deY) > dip2px(2)) {
                show();
                if (currentVolume == -1) {
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (currentVolume < 0) currentVolume = 0;
                }
                float percent = (moveY / maxTouchHeight);
                int index = (int) (percent * maxVolume) + currentVolume;
                if (index > maxVolume) index = maxVolume;
                else if (index < 0) index = 0;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, AudioManager.FLAG_PLAY_SOUND);
                mSoundLightView.setIcon(R.drawable.video_player_volume_100);
                float ratio = index * 1.0f / maxVolume;
                int per = (int) (ratio * 100);
                if (per == 0) mSoundLightView.setIcon(R.drawable.video_player_volume_no);
                mSoundLightView.setText(per + "%");
            }
        }
    }





    private void onActionUp() {
        isSound = false;
        moveY = 0;
        currentVolume = -1;
        mBrightness = -1f;
        dismiss();
    }
    private void show() {
        int arcX = 0;
        int arcY = 0;
        if (mSoundLightView.getMeasuredWidth() == 0) {
            arcX = anchorView.getMeasuredWidth() / 2 - initOffsetX;
            arcY = anchorView.getMeasuredHeight() / 2 + initOffsetY;
        } else {
            arcX = anchorView.getMeasuredWidth() / 2 - mSoundLightView.getMeasuredWidth() / 2;
            arcY = anchorView.getMeasuredHeight() / 2 + mSoundLightView.getMeasuredHeight() / 2;
        }

        mSLwindow.showAsDropDown(anchorView, arcX, -arcY);
    }

    public void setSoundOnLeft() {
        soundonLeft = true;
    }

    public void setBackgroundAlpa(int value) {
        mSoundLightView.getBackground().setAlpha(value);
    }


    private void dismiss() {
        mSLwindow.dismiss();
    }


    private int dip2px(float dipValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }




    private class SoundLightView extends LinearLayout {
        private ImageView iv_icon;
        private TextView tv_percent;
        private int mWidth = 300;
        private int mHeight = 120;
        private int iconWidth = 72;
        private int iconHeight = 72;
        private int icon_leftMargin = 50;
        private int tv_leftMargin = 150;
        private ScaleUtil mScaleUtil;

        public SoundLightView(Context context) {
            this(context, null);
        }

        public SoundLightView(Context context, AttributeSet attrs) {
            super(context, attrs);
            mScaleUtil = new ScaleUtil(context);
            initSize();
            setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_tv_soundlight));
            initView(context);
        }

        private void initSize() {
            mWidth = mScaleUtil.toScaleSize(mWidth);
            mHeight = mScaleUtil.toScaleSize(mHeight);
            iconWidth = mScaleUtil.toScaleSize(iconWidth);
            iconHeight = mScaleUtil.toScaleSize(iconHeight);
            icon_leftMargin = mScaleUtil.toScaleSize(icon_leftMargin);
            tv_leftMargin = mScaleUtil.toScaleSize(tv_leftMargin);
        }

        private void initView(Context context) {

            RelativeLayout relativeLayout = new RelativeLayout(context);
            LayoutParams lp = new LayoutParams(mWidth, mHeight);
            relativeLayout.setLayoutParams(lp);


            RelativeLayout.LayoutParams lp_icon = new RelativeLayout.LayoutParams(iconWidth, iconHeight);
            lp_icon.addRule(RelativeLayout.CENTER_VERTICAL);
            lp_icon.leftMargin = icon_leftMargin;
            iv_icon = new ImageView(context);
            iv_icon.setLayoutParams(lp_icon);

            RelativeLayout.LayoutParams lp_text = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp_text.addRule(RelativeLayout.CENTER_VERTICAL);
            lp_text.leftMargin = tv_leftMargin;
            tv_percent = new TextView(context);
            tv_percent.setLayoutParams(lp_text);
            tv_percent.setTextColor(Color.WHITE);
            tv_percent.setTextSize(16);


            relativeLayout.addView(iv_icon);
            relativeLayout.addView(tv_percent);
            addView(relativeLayout);
        }


        public void setIcon(int id) {
            iv_icon.setImageResource(id);
        }

        public void setText(String text) {
            tv_percent.setText(text);
        }


        public int getScaleWidth() {
            return mWidth;
        }

        public int getScaleHeight(){
            return mHeight;
        }

    }


    private class ScaleUtil {
        private final int BASE_WIDTH = 1080;
        private float BASE_RATIO = 1;
        public ScaleUtil(Context context) {
            if (context==null)return;
            //判断现在是横屏还是竖屏状态
            int width=0;
            boolean flag= mContext.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT;
            if (flag){
                width=context.getResources().getDisplayMetrics().widthPixels;
            }else{
                width=context.getResources().getDisplayMetrics().heightPixels;
            }
            BASE_RATIO = 1.0f * width/ BASE_WIDTH;
        }
        public int toScaleSize(int px) {
            return (int) (BASE_RATIO * px);
        }
    }
}
