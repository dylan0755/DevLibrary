package com.dylan.library.io;

import android.content.Context;

import com.dylan.library.device.SDCardUtils;
import com.dylan.library.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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


    public static void mkdirsIfNotExist(String dirPath){
         File file=new File(dirPath);
         if (!file.exists())file.mkdirs();
    }

    public static void writeString2Sdcard(String text,String outputFilePath){

        if (text!=null&&!text.isEmpty()){
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFilePath);
                fos.write(text.getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    public static void unzip(String srcZipPath,String dirName) throws IOException {
        ZipFile zipFile=new ZipFile(srcZipPath);
        for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
            ZipEntry entry = entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zipFile.getInputStream(entry);
            // String outPath = (desDir + name +"/"+ zipEntryName).replaceAll("\\*", "/");
            String outPath = (dirName+"/"+ zipEntryName).replaceAll("\\*", "/");
            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            if (new File(outPath).isDirectory()) {
                continue;
            }
            FileOutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        Logger.e("解压完毕.......");
        return;

    }



}
