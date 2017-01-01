package com.dylan.library.file;

import android.content.Context;

import com.dylan.library.device.SDCardUtils;
import com.dylan.library.io.IOCloser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Dylan on 2016/12/31.
 */

public class FileUtils {
    /**
     *
     * @param context
     * @param assetsFileName
     * @param outputFilePath 必须是一个文件的路径，而不是文件夹的路径
     */
    public static void copyAssets2SDcard(Context context, String assetsFileName, String outputFilePath) {
        if (context==null)return;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFilePath);
            byte[] buffer = new byte[1024];
            InputStream in = context.getAssets().open(assetsFileName);
            int count;
            while((count= in.read(buffer))!=-1){
                out.write(buffer, 0, count);
            }
            IOCloser.closeIOArray(in,out);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String getSDCardDir(){
          return SDCardUtils.getSDcardDir();
    }


    public static void mkdirs(String dirPath){
         File file=new File(dirPath);
         if (!file.exists())file.mkdirs();
    }

    public static void writeString2Sdcard(String text,String filePath){

        if (text!=null&&!text.isEmpty()){
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(filePath);
                fos.write(text.getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }






}
