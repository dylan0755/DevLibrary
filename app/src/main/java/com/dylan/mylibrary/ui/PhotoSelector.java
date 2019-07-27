package com.dylan.mylibrary.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.dylan.mylibrary.R;

import java.io.File;



/**
 * Created by Dylan on 2016/9/24.
 */
public class PhotoSelector implements View.OnClickListener {
    private Dialog mDialog;
    private Activity mActivity;
    private final int FILE_REQUEST_CODE_PICTURE = 2;
    private final int PICTURE_CUT = 3;//裁剪
    private final int TAKE_PHOTO = 4;
    private String tempFilePath = "";
    private int mWidth = 240;
    private int mHeight = 240;
    private boolean crop;
    private boolean operateOriginal = true;//操作原图

    public PhotoSelector(Activity activity) {
        mActivity = activity;
        String rootPath= Environment.getExternalStorageDirectory().toString()+"/temp";
        File file=new File(rootPath);
        if (!file.exists())file.mkdirs();
        tempFilePath =rootPath+"/temp.jpg";

        mDialog = new Dialog(mActivity, R.style.DLDialogStyle);
        View contentView = View.inflate(mActivity, R.layout.dialog_head_choice, null);
        mDialog.setContentView(contentView);
        Window win = mDialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        contentView.findViewById(R.id.tv_photograph).setOnClickListener(this);
        contentView.findViewById(R.id.tv_album).setOnClickListener(this);
        contentView.findViewById(R.id.tv_cancel).setOnClickListener(this);
    }


    public PhotoSelector(Activity activity, int width, int height) {
        this(activity);
        mWidth = width;
        mHeight = height;
    }

    public void showDialog(boolean crop) {
        this.crop = crop;
        mDialog.show();
    }

    public void deattachActivity(){
        mActivity=null;
    }

    public void selectByCamera(boolean crop) {
        this.crop = crop;
        //用意图打开系统照相机
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        //设置相片存储路径
        File file = new File(tempFilePath);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        mActivity.startActivityForResult(intent, TAKE_PHOTO);

    }

    /**
     * @param crop            是否裁剪
     * @param operateOriginal 是否操作原图，如需要上传原图到云服务器
     */
    public void selectByPhotoAlbum(boolean crop, boolean operateOriginal) {
        this.crop = crop;
        this.operateOriginal = operateOriginal;
        toPick();
    }

    private void toPick() {
        Intent intent_picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mActivity.startActivityForResult(intent_picture, FILE_REQUEST_CODE_PICTURE);
    }

    //裁剪图片
    private void startPhotoZoom(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        File file=new File(tempFilePath);
        Uri temUri= Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, temUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        mActivity.startActivityForResult(intent, PICTURE_CUT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_photograph:
                mDialog.dismiss();
                selectByCamera(crop);
                break;
            case R.id.tv_album://从相册选择
                mDialog.dismiss();
                toPick();
                break;
            case R.id.tv_cancel:
                mDialog.dismiss();
                break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (FILE_REQUEST_CODE_PICTURE == requestCode && data != null && resultCode == mActivity.RESULT_OK) {
            Uri selectUri = data.getData();
            //拿到相册图片uri
            if (selectUri != null) {
                if (!crop && operateOriginal) {//操作原图 ,直接返回原图路径，只能是在不裁剪的前提下
                    if (mPickerListener != null) {
                        mPickerListener.onPickerResult(getRealFilePath(mActivity, selectUri));
                    }
                } else {
                    //Log.e( "onActivityResult: ","selectUri "+selectUri );
                    startPhotoZoom(selectUri, mWidth, mHeight);
                }
            } else {
                Toast.makeText(mActivity, "没有选择文件", Toast.LENGTH_SHORT).show();
            }
        } else if (TAKE_PHOTO == requestCode) {//手机拍照
            if (crop) {
                File file = new File(tempFilePath);
                Uri uri = Uri.fromFile(file);
                if (uri != null) {
                    startPhotoZoom(uri, mWidth, mHeight);
                }
            } else {
                if (mPickerListener != null) {
                    mPickerListener.onPickerResult(tempFilePath);
                }

            }

        } else if (PICTURE_CUT == requestCode && data != null && resultCode == -1) {//裁剪成功后返回
            //Bitmap bitmap= BitmapFactory.decodeFile(tempFilePath);
            if (mPickerListener != null) {
                mPickerListener.onPickerResult(tempFilePath);
            }
        }
    }

    public void deleteTempFile() {
        File file = new File(tempFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    private PhotoPickerListener mPickerListener;

    public interface PhotoPickerListener {
        void onPickerResult(String filepath);
    }


    public void setPhotoPickerListener(PhotoPickerListener listener) {
        mPickerListener = listener;
    }



    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }



}
