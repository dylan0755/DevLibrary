package com.dylan.library.widget.irecycler;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;


/**
 * Author: Dylan
 * Date: 2021/04/12
 * Desc:
 */

public class DragSortItemTouchHelper extends ItemTouchHelper.Callback {
    private RecyclerView.Adapter mAdapter;
    private  Vibrator mVibrator;
    private Drawable backgroundDrawable;
    private int pressColor=Color.LTGRAY;
    private OnAfterDragCallBack mDragCallBack;
    private int excludeAdapterPosition=-10;
    private int dragOrientation=ItemTouchHelper.UP | ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
    private ItemTouchHelper mItemTouchHelper;

    public DragSortItemTouchHelper() {

    }

    public void attachToRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter){
        mAdapter = adapter;
        mVibrator = (Vibrator) recyclerView.getContext().getSystemService(Context.VIBRATOR_SERVICE);//震动
        mItemTouchHelper=new ItemTouchHelper(this);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }



    public void setPressColor(int pressColor) {
        this.pressColor = pressColor;
    }

    public void setExcludeAdapterPosition(int excludeAdapterPosition) {
        this.excludeAdapterPosition = excludeAdapterPosition;
    }

    public void setDragOrientation(int dragOrientation) {
        this.dragOrientation = dragOrientation;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int position=viewHolder.getAdapterPosition();
        if(position==excludeAdapterPosition){
            return 0;   //这里模拟，第一个位置不允许拖动
        }
        return makeMovementFlags(dragOrientation, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        if (viewHolder1.getAdapterPosition()==excludeAdapterPosition)return false;
        mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), viewHolder1.getAdapterPosition());
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



    public static class Builder{


    }
}
