package com.dylan.library.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Dylan on 2016/12/30.
 */

public class BitmapUtils {


    private static String dirPath ;

    public static Bitmap getBitmap(String imgPath) {
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            float hh = 720;
            float ww = 480;
            int be = 1;//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0) be = 1;
            newOpts.inSampleSize = be;//设置缩放比例
            bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String comPressBitmap(Context context,Bitmap bitmap, String fileName) {
        if (bitmap == null) return "";
        if (dirPath==null){
            dirPath=Environment.getExternalStorageDirectory().toString() + "/Android/data/"+
                    context.getPackageName()+"/upload/";
        }
        File fileDir=new File(dirPath);
        if (!fileDir.exists())fileDir.mkdirs();
        String path=dirPath+fileName;

        try {
            File file = new File(path);
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)){
                fos.close();
                return path;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
