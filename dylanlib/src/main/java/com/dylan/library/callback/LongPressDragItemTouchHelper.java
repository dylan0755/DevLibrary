package com.dylan.library.callback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.dylan.library.adapter.BaseRecyclerAdapter;

/**
 * Author: Dylan
 * Date: 2021/04/12
 * Desc:
 */

/**
 * ItemTouchHelper  mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(this,mAdapter));
 * mItemTouchHelper.attachToRecyclerView(recyclerView);
 */
public class LongPressDragItemTouchHelper extends ItemTouchHelper.Callback {
    private BaseRecyclerAdapter mAdapter;
    private final Vibrator mVibrator;
    private Drawable backgroundDrawable;
    private int pressColor=Color.LTGRAY;
    private OnAfterDragCallBack mDragCallBack;

    public LongPressDragItemTouchHelper(Context context, BaseRecyclerAdapter adapter) {
        mAdapter = adapter;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);//震动
    }
    public LongPressDragItemTouchHelper(Context context, int pressColor, BaseRecyclerAdapter adapter) {
        this.pressColor=pressColor;
        mAdapter = adapter;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);//震动
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), viewHolder1.getAdapterPosition());
        return false;
    }

    //长按
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            mVibrator.vibrate(60);
            backgroundDrawable=viewHolder.itemView.getBackground();
            if (backgroundDrawable!=null){
                viewHolder.itemView.setBackgroundColor(pressColor);
            }

        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //松开
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (backgroundDrawable!=null){
            viewHolder.itemView.setBackground(backgroundDrawable);
        }
        if (mDragCallBack!=null){
            mDragCallBack.afterDrag();
        }

    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }



    public interface OnAfterDragCallBack{
        void afterDrag();
    }


    public void setOnAfterDragCallBack(OnAfterDragCallBack callBack){
        mDragCallBack=callBack;
    }
}
