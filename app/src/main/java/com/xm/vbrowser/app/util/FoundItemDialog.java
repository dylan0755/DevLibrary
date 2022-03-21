package com.xm.vbrowser.app.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.dialog.BottomSlideDialog;
import com.dylan.library.utils.ViewUtils;
import com.dylan.mylibrary.R;
import com.xm.vbrowser.app.adapter.FoundVideoItemAdapter;
import com.xm.vbrowser.app.entity.VideoInfo;

/**
 * Author: Dylan
 * Date: 2022/3/18
 * Desc:
 */

public class FoundItemDialog extends BottomSlideDialog {
    RecyclerView recyclerView;
   private FoundVideoItemAdapter mFoundVideoItemAdapter;
    public FoundItemDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_detect_video_item);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mFoundVideoItemAdapter=new FoundVideoItemAdapter();
        recyclerView.setAdapter(mFoundVideoItemAdapter);
        ViewUtils.setClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dismiss();
            }
        },findViewById(R.id.ivClose));
    }
    public void setActivityBack(boolean activityBack) {
        mFoundVideoItemAdapter.setActivityBack(activityBack);
    }

    public void show(VideoInfo videoInfo) {
        super.show();
        mFoundVideoItemAdapter.appendNotifyDataChanged(videoInfo);
    }
}
