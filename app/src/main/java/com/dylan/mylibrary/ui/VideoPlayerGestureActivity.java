package com.dylan.mylibrary.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.dylan.library.media.MediaTools;
import com.dylan.library.media.PlayerGesture;
import com.dylan.library.screen.ScreenUtils;
import com.dylan.library.utils.ToastUtils;
import com.dylan.library.widget.PlayerGestureView;
import com.dylan.mylibrary.R;

/**
 * Author: Dylan
 * Date: 2020/2/17
 * Desc:
 */
public class VideoPlayerGestureActivity extends AppCompatActivity {
    PlayerGestureView gestureView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayergesture);
        ScreenUtils.showInFullScreen(this);
        ScreenUtils.switchOrientationLandscape(this);

        FrameLayout container = findViewById(R.id.flContainer);
        gestureView = findViewById(R.id.playerGestureView);
        gestureView.setAnchorView(container);
        gestureView.setOnPlayerSpeedGestureListener(new PlayerGesture.OnPlayerGestureListener() {
            @Override
            public long getDuration() {
                return 1000*60*26;
            }

            @Override
            public long getCurrentPostion() {
                return 1000*60*7;
            }

            @Override
            public void seekTo(long position) {
                ToastUtils.show("从 "+ MediaTools.getDurationMinuteFormat(position)+"播放");
            }
        });
    }
}
