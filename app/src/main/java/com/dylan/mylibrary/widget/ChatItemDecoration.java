package com.dylan.mylibrary.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatItemDecoration extends RecyclerView.ItemDecoration {

    private int spacing;

    public ChatItemDecoration(Context context, int spacings) {
        spacing = spacings;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = spacing;
        outRect.bottom = spacing;
    }
}
