package com.dylan.library.utils.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import com.dylan.library.dialog.PhotoSelectBottomSheetDialog;
import com.dylan.library.io.FileUtils;
import com.dylan.library.utils.ContextUtils;

import java.io.File;



/**
 * Created by Dylan on 2016/9/24.
 */
public class PhotoSelector {
    private final int FILE_REQUEST_CODE_PICTURE = 2;
    private final int PICTURE_CUT = 3;//裁剪
    private final int TAKE_PHOTO = 4;
    private String tempFilePath = "";
    private int mWidth = 240;
    private int mHeight = 240;
    private boolean crop;
    private boolean operateOriginal = true;//操作原图
    private PhotoPickerCallBack pickerCallBack;
    private String mTag="";//当前标签，一个页面可能有几处上传，比如身份证正反面等等，tag表示来源

    public PhotoSelector(PhotoPickerCallBack callBack) {
        if (callBack==null)return;
        pickerCallBack =callBack;
        String rootPath= Environment.getExternalStorageDirectory().toString()+"/temp";
        File file=new File(rootPath);
        if (!file.exists())file.mkdirs();
        tempFilePath =rootPath+"/dl_temp.jpg";
    }


    public PhotoSelectBottomSheetDialog showDialog(String tag){
       return showDialog(false,0,0,tag);
    }

    public PhotoSelectBottomSheetDialog showDialog(boolean crop,int cropWidth,int cropHeight,String tag) {
        if (pickerCallBack ==null|| pickerCallBack.getActivityContext()==null)return null;
        this.crop = crop;
        mTag=tag;
        PhotoSelectBottomSheetDialog bottomSheetDialog =new PhotoSelectBottomSheetDialog(pickerCallBack.getActivityContext());
        bottomSheetDialog.show(new PhotoSelectBottomSheetDialog.SelectListener() {
            @Override
            public void selectCamera() {
                selectByCamera();
            }

            @Override
            public void selectPhotoAlbum() {
                toPick();
            }
        });
        return bottomSheetDialog;
    }





    public void selectByCamera() {
        //用意图打开系统照相机
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        //设置相片存储路径
        File file = new File(tempFilePath);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        ContextUtils.getActivity(pickerCallBack.getActivityContext()).startActivityForResult(intent, TAKE_PHOTO);

    }


    private void toPick() {
        Intent intent_picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ContextUtils.getActivity(pickerCallBack.getActivityContext()).startActivityForResult(intent_picture, FILE_REQUEST_CODE_PICTURE);
    }

    //裁剪图片
    private void toCrop(Uri uri, int width, int height) {
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
        ContextUtils.getActivity(pickerCallBack.getActivityContext()).startActivityForResult(intent, PICTURE_CUT);
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (FILE_REQUEST_CODE_PICTURE == requestCode && data != null && resultCode == Activity.RESULT_OK) {
            Uri selectUri = data.getData();
            //拿到相册图片uri
            if (selectUri != null) {
                if (!crop && operateOriginal) {//操作原图 ,直接返回原图路径，只能是在不裁剪的前提下
                    if (pickerCallBack != null) {
                        File file=FileUtils.getFileByUri(selectUri, pickerCallBack.getActivityContext());
                        if (file!=null) pickerCallBack.onPickerResult(file,mTag);

                    }
                } else {
                    if (!pickerCallBack.onInterceptCropIntent(selectUri)){
                        toCrop(selectUri, mWidth, mHeight);
                    }
                }
            } else {
                Toast.makeText(pickerCallBack.getActivityContext(), "没有选择文件", Toast.LENGTH_SHORT).show();
            }
        } else if (TAKE_PHOTO == requestCode) {//手机拍照
            if (crop) {
                File file = new File(tempFilePath);
                Uri uri = Uri.fromFile(file);
                if (uri != null&&!pickerCallBack.onInterceptCropIntent(uri)) {
                    toCrop(uri, mWidth, mHeight);
                }
            } else {
                pickerCallBack.onPickerResult(new File(tempFilePath),mTag);
            }
        } else if (PICTURE_CUT == requestCode && data != null && resultCode == -1) {//裁剪成功后返回
            pickerCallBack.onPickerResult(new File(tempFilePath),mTag);
        }
    }

    public void deleteTempFile() {
        File file = new File(tempFilePath);
        if (file.exists()) {
            file.delete();
        }
    }



    public static abstract class PhotoPickerCallBack {
        public abstract Context getActivityContext();
        public abstract void onPickerResult(File pickFile,String tag);
        //是否拦截调往系统裁剪的请求，如果拦截了 onPickerResult就不会再回调了
        public  boolean onInterceptCropIntent(Uri selectPicUri){
            return false;
        }
    }







}
