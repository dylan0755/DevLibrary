package com.xm.vbrowser.app.util;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.dialog.BottomSlideDialog;
import com.dylan.library.utils.ViewUtils;
import com.dylan.mylibrary.R;
import com.xm.vbrowser.app.adapter.FoundVideoItemAdapter;
import com.xm.vbrowser.app.entity.VideoInfo;

import java.util.SortedMap;

/**
 * Author: Dylan
 * Date: 2022/3/18
 * Desc:
 */

public class FoundItemDialog extends BottomSlideDialog {
   ListView listView;
   private FoundVideoItemAdapter mFoundVideoItemAdapter;
    public FoundItemDialog(Context context, SortedMap<String, VideoInfo> foundVideoInfoMap) {
        super(context);
        setContentView(R.layout.dialog_detect_video_item);
        listView=findViewById(R.id.listView);
        mFoundVideoItemAdapter=new FoundVideoItemAdapter(context, foundVideoInfoMap);
        listView.setAdapter(mFoundVideoItemAdapter);
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

    @Override
    public void show() {
        super.show();
        mFoundVideoItemAdapter.notifyDataSetChanged();
    }
}
