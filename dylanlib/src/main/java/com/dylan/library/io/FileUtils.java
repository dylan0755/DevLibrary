package com.dylan.library.io;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.dylan.library.device.SDCardUtils;
import com.dylan.library.exception.ELog;
import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.net.UrlUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
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
     * @param context
     * @param assetsFileName
     * @param outputFilePath 必须是一个文件的路径，而不是文件夹的路径
     */
    public static void copyAssets2SDcard(Context context, String assetsFileName, String outputFilePath) {
        if (context == null) return;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFilePath);
            byte[] buffer = new byte[1024];
            InputStream in = context.getAssets().open(assetsFileName);
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            IOCloser.closeIOArray(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String getSDCardDir() {
        return SDCardUtils.getSDcardDir();
    }


    public static void mkdirsIfNotExist(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) file.mkdirs();
    }

    public static void writeTextToSDRootPath(String text, String fileName) throws Exception {
        if (EmptyUtils.isEmpty(text)) throw new Exception("text is empty!!!");
        String sdPath = Environment.getExternalStorageDirectory().toString();
        String outPutPath = sdPath + "/" + fileName;
        FileOutputStream fos = new FileOutputStream(outPutPath);
        fos.write(text.getBytes());
        fos.flush();
        fos.close();
    }


    public static boolean writeTextToSdcard(String text, String outputFilePath) throws Exception {
        if (EmptyUtils.isEmpty(text)) throw new Exception("text is empty!!!");

        File outPutFile = new File(outputFilePath);
        if (!outPutFile.getParentFile().exists()) {
            boolean isCreated = outPutFile.getParentFile().mkdirs();
            if (!isCreated) {
                return false;
            }
        }
        FileOutputStream fos = new FileOutputStream(outputFilePath);
        fos.write(text.getBytes());
        fos.flush();
        fos.close();
        return true;

    }


    public static void unzip(String srcZipPath, String dirName) throws IOException {
        ZipFile zipFile = new ZipFile(srcZipPath);
        for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zipFile.getInputStream(entry);
            // String outPath = (desDir + name +"/"+ zipEntryName).replaceAll("\\*", "/");
            String outPath = (dirName + "/" + zipEntryName).replaceAll("\\*", "/");
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


    public static File getFileByUri(Uri uri, Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

    public static void notifyScanFile(Context context, String desFilePath) {
        //手动往媒体库插入记录
        String suffix=desFilePath.substring(desFilePath.lastIndexOf(".")+1);
        if (EmptyUtils.isNotEmpty(suffix)){
            suffix=suffix.toLowerCase();
            if ("png".equals(suffix)||"jpg".equals(suffix)||"jpeg".equals(suffix)){
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, desFilePath);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/"+(suffix.equals("jpg")?"jpeg":suffix));
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(desFilePath))));
    }

    public static synchronized void addIgnore(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        File ignoreFile = new File(dirPath + "/.nomedia");
        if (ignoreFile.exists() && ignoreFile.isFile()) {
            return;
        }

        try {
            ignoreFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean delectDirFile(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.exists()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f.isFile()) {
                        f.delete();
                    } else if (f.isDirectory()) {
                        delectDirFile(f.getAbsolutePath());
                    }

                }
            }
            //删除空的文件夹
            return file.delete();
        } catch (Exception e) {
            ELog.e(e);
        }
        return false;
    }


    //同步保存
    public static void saveBitmapSyncAndNotifyScan(Context context, Bitmap bitmap, String savePath) throws IOException {
        if (EmptyUtils.isEmpty(bitmap) || EmptyUtils.isEmpty(savePath)) return;
        BitmapHelper.saveBitmapSyncAndNotifyScan(context, bitmap, savePath);
    }

    //同步保存
    public static void saveBitmapSync(Bitmap bitmap, String savePath) throws IOException {
        if (EmptyUtils.isEmpty(bitmap) || EmptyUtils.isEmpty(savePath)) return;
        BitmapHelper.saveBitmapSync(bitmap, savePath);
    }


    //异步保存
    public static void saveBitmapASync(Bitmap bitmap, String savePath) {
        if (EmptyUtils.isEmpty(bitmap) || EmptyUtils.isEmpty(savePath)) return;
        BitmapHelper.saveBitmapASync(bitmap, savePath);
    }

    //异步保存
    public static void saveBitmapASync(Bitmap bitmap, String savePath, BitmapHelper.OutPutListenener outPutListenener) {
        if (EmptyUtils.isEmpty(bitmap) || EmptyUtils.isEmpty(savePath)) return;
        BitmapHelper.saveBitmapASync(bitmap, savePath, outPutListenener);
    }


}
