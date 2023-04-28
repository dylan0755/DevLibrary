package com.dylan.mylibrary.ui;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dylan.mylibrary.R;
import com.dylan.mylibrary.ui.voicerecord.MediaManager;
import com.dylan.mylibrary.ui.voicerecord.VoiceListAdapter;
import com.dylan.mylibrary.ui.voicerecord.VoiceRecordButton;
import com.dylan.mylibrary.ui.voicerecord.VoiceRecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2017/11/16.
 */

public class VoiceRecordActivity extends Activity {
    private VoiceRecordButton btnRecord;
    private ListView voiceList;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> mDatas = new ArrayList<Recorder>();

    private AnimationDrawable animation;
    private View voiceAnim;
    private VoiceRecorder voiceRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicerecord);
        voiceList = (ListView) findViewById(R.id.voiceList);
        btnRecord = (VoiceRecordButton) findViewById(R.id.btnRecord);
        voiceRecorder =new VoiceRecorder(false);
        voiceRecorder.setRecordFinishListener(new MyRecordFinishListener());
        btnRecord.setVoiceRecorder(voiceRecorder);
        mAdapter = new VoiceListAdapter(this, mDatas);
        voiceList.setAdapter(mAdapter);
        voiceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                // 播放动画
                if (animation != null) {
                    voiceAnim.setBackgroundResource(R.drawable.icon_voice_ripple);
                    voiceAnim = null;
                }
                voiceAnim = view.findViewById(R.id.voiceAnim);
                voiceAnim.setBackgroundResource(R.drawable.anim_play_audio);
                animation = (AnimationDrawable) voiceAnim.getBackground();
                animation.start();
                // 播放音频
                MediaManager.playSound(mDatas.get(position).filePath,
                        new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                voiceAnim.setBackgroundResource(R.drawable.icon_voice_ripple);
                            }
                        });

            }
        });
    }

     class MyRecordFinishListener implements VoiceRecorder.RecordFinishListener {

        @Override
        public void onFinish(long millisecond, String filePath) {
            Recorder recorder = new Recorder(millisecond, filePath);
            Uri uri=Uri.fromFile(new File(filePath));
            Log.e("onFinish ",uri.toString());
            mDatas.add(recorder);
            mAdapter.notifyDataSetChanged();
            voiceList.setSelection(mDatas.size() - 1);
        }

    }

    public class Recorder {
        private long  audioLength;
        private String filePath;

        public Recorder(long  audioLength, String filePath) {
            super();
            this.audioLength = audioLength;
            this.filePath = filePath;
        }

        public long  getAudioLength() {
            return audioLength;
        }

        public void setAudioLength(long audioLength) {
            this.audioLength = audioLength;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        voiceRecorder.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }
}
