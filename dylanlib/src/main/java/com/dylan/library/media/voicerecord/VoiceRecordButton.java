package com.dylan.library.media.voicerecord;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.dylan.library.R;

import static android.R.attr.action;


public class VoiceRecordButton extends Button {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_CANCEL = 3;
    private static final int DISTANCE_CANCEL_Y = 50;
    private int currentState = STATE_NORMAL;
    private VoiceRecorder voiceRecorder;
    private String text_state_normal;
    private String text_state_recording;
    private String text_state_wantcancel;
    // 是否触发LongClick
    private boolean isReady = false;

    public VoiceRecordButton(Context context) {
        this(context, null);
    }

    public VoiceRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        text_state_normal =context.getString(R.string.btn_recorder_normal);
        text_state_recording=context.getString(R.string.btn_recorder_recording);
        text_state_wantcancel=context.getString(R.string.btn_recorder_want_cancel);
        setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (voiceRecorder!=null){
                    isReady = true;
                    voiceRecorder.startRecord(v.getContext());
                }

                return false;
            }
        });
    }

    public void setVoiceRecorder(VoiceRecorder dialog) {
        voiceRecorder = dialog;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (voiceRecorder==null)  return super.onTouchEvent(event);
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                // 已经开始录音
                if (voiceRecorder.isRecording()) {
                    // 根据X，Y的坐标判断是否想要取消
                    if (wantCancel(x, y)) {
                        changeState(STATE_WANT_CANCEL);
                        voiceRecorder.stateWantCancel();
                    } else {
                        changeState(STATE_RECORDING);
                        voiceRecorder.stateRecording();
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                // 没有触发longClick
                if (!isReady) {
                    resetState();
                    return super.onTouchEvent(event);
                }
                // prepare未完成就up,录音时间过短
                if (!voiceRecorder.isRecording() || voiceRecorder.getRecordTime() < 1000) {
                    voiceRecorder.stateLengthShort();
                } else if (currentState == STATE_RECORDING) { // 正常录制结束
                    voiceRecorder.finishRecord();
                } else if (currentState == STATE_WANT_CANCEL) {
                    voiceRecorder.cancelRecord();
                }
                resetState();
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复标志位
     */
    private void resetState() {
        changeState(STATE_NORMAL);
        isReady = false;
        voiceRecorder.setNoRecording();
        voiceRecorder.clearRecordTime();
    }

    private boolean wantCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        // 零点在左下角？
        if (y < -DISTANCE_CANCEL_Y || y > getHeight() + DISTANCE_CANCEL_Y) {
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        if (currentState != state) {
            currentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setText(text_state_normal);
                    break;
                case STATE_RECORDING:
                    setText(text_state_recording);
                    if (voiceRecorder.isRecording()) {
                        voiceRecorder.stateRecording();
                    }
                    break;
                case STATE_WANT_CANCEL:
                    setText(text_state_wantcancel);
                    voiceRecorder.stateWantCancel();
                    break;

                default:
                    break;
            }
        }
    }

    public void setStateText(String normal, String wantCancle, String recording) {
        this.text_state_normal = normal;
        this.text_state_recording = recording;
        this.text_state_wantcancel = wantCancle;
    }

}
