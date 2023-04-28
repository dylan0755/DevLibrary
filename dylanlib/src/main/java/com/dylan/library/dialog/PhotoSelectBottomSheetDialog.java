package com.dylan.library.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dylan.library.R;


/**
 * 选择图片的dialog
 */
public class PhotoSelectBottomSheetDialog extends Dialog implements View.OnClickListener {
    private TextView tvCamera;
    private TextView tvPhotoAlbum;
    private TextView tvCancel;
    private SelectListener mLister;

    public PhotoSelectBottomSheetDialog(@NonNull Context context) {
        super(context, R.style.DLBottomSheetDialogStyle);
        init(context);
    }


    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dl_dialog_local_pic_select, null);
        tvCamera = view.findViewById(R.id.tvCamera);
        tvPhotoAlbum = view.findViewById(R.id.tvPhotoAlbum);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvCamera.setOnClickListener(this);
        tvPhotoAlbum.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        setContentView(view);

    }


    public TextView getCameraTextView() {
        return tvCamera;
    }

    public TextView getPhotoAlbumTextView() {
        return tvPhotoAlbum;
    }

    public TextView getPhotoCancelTextView() {
        return tvCancel;
    }

    public void show(SelectListener lister) {
        mLister = lister;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.horizontalMargin = 0;
        params.dimAmount = 0.3f;
        getWindow().setAttributes(params);
        super.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvCamera) {
            dismiss();
            if (mLister != null) mLister.selectCamera();
        } else if (v.getId() == R.id.tvPhotoAlbum) {
            dismiss();
            if (mLister != null) mLister.selectPhotoAlbum();
        } else if (v.getId() == R.id.tvCancel) {
            dismiss();
        }
    }


    public interface SelectListener {
        void selectCamera();          //拍照

        void selectPhotoAlbum();      //相册
    }

}
