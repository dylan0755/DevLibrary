package com.dylan.mylibrary.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dylan.library.adapter.BaseRecyclerAdapter;
import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.dialog.BottomSlideDialog;
import com.dylan.library.utils.ContextUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.widget.irecycler.DragSortItemTouchHelper;
import com.dylan.mylibrary.R;
import com.dylan.mylibrary.adapter.SelectCarouselPicAdapter;
import com.dylan.photopicker.app.PhotoPickerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: Dylan
 * Date: 2022/04/20
 * Desc:
 */
public class NoPeopleImgCarouselDialog extends BottomSlideDialog {
    public static final int REQUEST_CAROUSEL_PIC_NO_PEOPLE=821;
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.rvCategoryItem)
    RecyclerView rvCategoryItem;
    private SelectCarouselPicAdapter mSelectPicAdapter;

    public NoPeopleImgCarouselDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_nopeople_imgcarousel);
        ButterKnife.bind(this);
        setCancelable(false);
        ivClose.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dismiss();
            }
        });
        mSelectPicAdapter=new SelectCarouselPicAdapter();
        rvCategoryItem.setLayoutManager(new GridLayoutManager(context,4));
        DragSortItemTouchHelper mItemTouchHelper = new DragSortItemTouchHelper();
        mItemTouchHelper.attachToRecyclerView(rvCategoryItem,mSelectPicAdapter);
        mItemTouchHelper.setExcludeAdapterPosition(0);


        rvCategoryItem.setAdapter(mSelectPicAdapter);
        mSelectPicAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<String>() {
            @Override
            public void onClick(String s, int i) {
                Intent intent = new Intent(getContext(), PhotoPickerActivity.class);
                intent.putExtra(PhotoPickerActivity.EXTRA_ALLOW_SWITCH_DIR, false);
                intent.putExtra(PhotoPickerActivity.EXTRA_MAX_SELECT, 1);
                intent.putExtra(PhotoPickerActivity.EXTRA_CAN_PREVIEW_ON_SINGLE_CHOICE, false);
                intent.putExtra(PhotoPickerActivity.EXTRA_INCLUDE_VIDEO, false);
                ContextUtils.getActivity(getContext()).startActivityForResult(intent, REQUEST_CAROUSEL_PIC_NO_PEOPLE);


            }
        });
        List<String> list = new ArrayList<>();
        list.add("");
        mSelectPicAdapter.bind(list);
    }


    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAROUSEL_PIC_NO_PEOPLE) {
            final String[] array = data.getStringArrayExtra(PhotoPickerActivity.IMAGE_SELECT_ARRAY);
            if (EmptyUtils.isNotEmpty(array)) {
                File originalFile = new File(array[0]);
                if (originalFile == null) return true;
                mSelectPicAdapter.getDataList().add(originalFile.getAbsolutePath());
                mSelectPicAdapter.notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }


}
