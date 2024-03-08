package com.dylan.library.utils.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;
import com.dylan.library.dialog.PhotoSelectBottomSheetDialog;
import com.dylan.library.io.FileUtils;
import com.dylan.library.utils.AndroidManifestUtils;
import com.dylan.library.utils.AppUtils;
import com.dylan.library.utils.ContextUtils;
import com.dylan.library.utils.PermissionRequestBuilder;
import com.hjq.toast.Toaster;

import java.io.File;


/**
 * Created by Dylan on 2016/9/24.
 */
public class PhotoSelector {
    public final int REQUEST_PER_CAMERA_WRITE = 100;
    public final int REQUEST_PER_EXTERNAL = 101;


    private final int REQUEST_PHOTOABLUM = 1;//从相册选择
    private final int REQUEST_CAMEAR = 2;//拍照
    private final int PICTURE_CUT = 3;//裁剪
    private String tempFilePath = "";
    private int mWidth = 240;
    private int mHeight = 240;
    private boolean crop;
    private boolean operateOriginal = true;//操作原图
    private PhotoPickerCallBack pickerCallBack;
    private String mTag="";//当前标签，一个页面可能有几处上传，比如身份证正反面等等，tag表示来源
    private PermissionRequestBuilder requestBuilder;


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
                selectFromCamera();
            }

            @Override
            public void selectPhotoAlbum() {
                selectFromPhotoAlbum();
            }
        });
        return bottomSheetDialog;
    }


    public void selectFromCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestBuilder = new PermissionRequestBuilder(ContextUtils.getActivity(pickerCallBack.getActivityContext()));
            requestBuilder.addPerm(Manifest.permission.CAMERA, true);
            requestBuilder.addPerm(Manifest.permission.READ_EXTERNAL_STORAGE, true);
            requestBuilder.addPerm(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            boolean needRequest = requestBuilder.startRequest(REQUEST_PER_CAMERA_WRITE);
            if (needRequest) {
                return;
            }
        }
        selectByCamera();
    }


    public void selectFromPhotoAlbum(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestBuilder = new PermissionRequestBuilder(ContextUtils.getActivity(pickerCallBack.getActivityContext()));
            requestBuilder.addPerm(Manifest.permission.READ_EXTERNAL_STORAGE, true);
            requestBuilder.addPerm(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            boolean needRequest = requestBuilder.startRequest(REQUEST_PER_EXTERNAL);
            if (needRequest) {
                return;
            }
        }
        toPick();
    }


    private void selectByCamera() {
        //用意图打开系统照相机
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        //设置相片存储路径
        File file = new File(tempFilePath);
        Uri uri = getUriFromFile( file);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            //临时共享权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        ContextUtils.getActivity(pickerCallBack.getActivityContext()).startActivityForResult(intent, REQUEST_CAMEAR);

    }


    private void toPick() {
        Intent intent_picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ContextUtils.getActivity(pickerCallBack.getActivityContext()).startActivityForResult(intent_picture, REQUEST_PHOTOABLUM);
    }

    //裁剪图片
    private void toCrop(Uri uri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*"); //大于7.0，这个Uri 是得通过FileProvider得到
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            //临时共享权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        File file=new File(tempFilePath);
        Uri temUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, temUri);//这个uri 不能用FileProvider
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        ContextUtils.getActivity(pickerCallBack.getActivityContext()).startActivityForResult(intent, PICTURE_CUT);
    }

    private Uri getUriFromFile(File file) {
        Uri temUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = AndroidManifestUtils.getFileProviderAuthority(pickerCallBack.getActivityContext());
            temUri = FileProvider.getUriForFile(pickerCallBack.getActivityContext(), authority, file);
        } else {
            temUri = Uri.fromFile(file);
        }
        return temUri;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_PHOTOABLUM == requestCode && data != null && resultCode == Activity.RESULT_OK) {
            Uri selectUri = data.getData();
            //拿到相册图片uri
            if (selectUri != null) {
                if (!crop && operateOriginal) {//操作原图 ,直接返回原图路径，只能是在不裁剪的前提下
                    if (pickerCallBack != null) {
                        File file=FileUtils.getFileByUri(pickerCallBack.getActivityContext(),selectUri);
                        if (file!=null) pickerCallBack.onPickerResult(file,mTag==null?"":mTag);

                    }
                } else {
                    if (!pickerCallBack.onInterceptCropIntent(selectUri)){
                        toCrop(selectUri, mWidth, mHeight);
                    }
                }
            } else {
                Toast.makeText(pickerCallBack.getActivityContext(), "没有选择文件", Toast.LENGTH_SHORT).show();
            }
        } else if (REQUEST_CAMEAR == requestCode&& resultCode == Activity.RESULT_OK) {//手机拍照
            if (crop) {
                File file = new File(tempFilePath);
                Uri uri = getUriFromFile(file);
                if (uri != null&&!pickerCallBack.onInterceptCropIntent(uri)) {
                    toCrop(uri, mWidth, mHeight);
                }
            } else {
                pickerCallBack.onPickerResult(new File(tempFilePath),mTag==null?"":mTag);
            }
        } else if (PICTURE_CUT == requestCode && data != null && resultCode == -1) {//裁剪成功后返回
            pickerCallBack.onPickerResult(new File(tempFilePath),mTag);
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode==REQUEST_PER_CAMERA_WRITE){
          PermissionRequestBuilder.RequestReuslt result = requestBuilder.onRequestPermissionsResult(permissions, grantResults);
          if (result.hasRejectForceNeed) {
              if (result.duration<500){
                  final Context context = pickerCallBack.getActivityContext();
                  new AlertDialog.Builder(context)
                          .setMessage("检测到您拒绝掉相机权限，拍照功能无法正常使用，是否前往开启?")
                          .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  AppUtils.gotoPermission(context);
                              }
                          })
                          .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  dialog.dismiss();
                              }
                          })
                          .show();
              }else{
                  Toaster.show("相机权限未允许");
              }
          } else {
              selectByCamera();
          }
      }else if (requestCode==REQUEST_PER_EXTERNAL){
          PermissionRequestBuilder.RequestReuslt result = requestBuilder.onRequestPermissionsResult(permissions, grantResults);
          if (result.hasRejectForceNeed) {
              Toaster.show("存储权限未允许");
          } else {
              toPick();
          }

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
