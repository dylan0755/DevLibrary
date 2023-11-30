package com.dylan.mylibrary.ui.tts;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import com.dylan.common.BaseActivity;
import com.dylan.library.utils.Logger;
import com.dylan.mylibrary.R;

import java.util.Locale;

/**
 * Author: Dylan
 * Date: 2023/11/30
 * Desc:
 */
public class TTsDemoActivity extends BaseActivity {
    private TTSUtil ttsUtil;

    @Override
    public int getLayoutId() {
        return R.layout.activity_tts_demo;
    }

    @Override
    public void onActivityCreate(Bundle savedInstanceState) {
        ttsUtil = new TTSUtil(this, new TTSUtil.TTSListener() {
            @Override
            public void onInitSuccess() {
                // TTS引擎初始化成功
                // 这里可以进行语音合成操作
                Logger.e("TTS  TTSUtil  onInitSuccess 初始化语音成功");
            }

            @Override
            public void onInitFailure() {
                Logger.e("TTS  TTSUtil onInitFailure TTS引擎初始化失败");
            }

            @Override
            public void onSpeechStart() {
                Logger.e("TTS  TTSUtil onSpeechStart 语音合成开始");
            }

            @Override
            public void onSpeechDone() {
                Logger.e("TTS  TTSUtil onSpeechDone 语音合成完成");
            }

            @Override
            public void onSpeechError(String errorMessage) {
                Logger.e("TTS  TTSUtil onSpeechError 语音合成出错 " + errorMessage);
            }
        });

        findViewById(R.id.button).setOnClickListener(v -> {
            TextView textView=findViewById(R.id.textView);
            ttsUtil.speak(textView.getText().toString().trim());
        });

    }




    // 在 Activity 销毁时释放 TTS 资源
    @Override
    protected void onDestroy() {
        ttsUtil.release();
        super.onDestroy();
    }

}