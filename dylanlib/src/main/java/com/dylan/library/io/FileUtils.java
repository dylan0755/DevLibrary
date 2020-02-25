package com.dylan.library.io;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.dylan.library.device.SDCardUtils;
import com.dylan.library.exception.ELog;
import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Dylan on 2016/12/31.
 */

public class FileUtils {


    public static boolean isExists(String path) {
        return new File(path).exists();
    }

    public static boolean delete(String path){
        File file=new File(path);
        if (file.exists()){
            if (file.isFile()){
                //如果是m3u8 则删除ts 文件
                 if (path.endsWith(".m3u8")||path.endsWith(".M3U8")){
                     String dirPath=getTSFileDirPath(path);
                     if (isExists(dirPath)){
                         delectDirFile(dirPath);
                     }
                     //ts 有可能和当前的m3u8 同一个文件夹，所以删除了文件夹还要判断
                     // m3u8 是否存在，存在则继续删除
                     if (file.exists()){
                         return file.delete();
                     }else{
                         return true;
                     }
                 }else{
                     return file.delete();
                 }
            }else if (file.isDirectory()){
                return delectDirFile(path);
            }
        }
        return false;
    }
    //通知更新单个文件
    public static void notifyScanFile(Context context,String filePath){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }






    public static String getFileNameFromPath(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    public static String getFileNameWithoutSuffix(String filePath){
        int startIndex=filePath.lastIndexOf("/") + 1;
        int endIndex=filePath.lastIndexOf(".");
        endIndex=endIndex>0?endIndex:filePath.length();
        return filePath.substring(startIndex,endIndex);
    }

    public static String getFileSuffixFromPath(String filePath){
        int pointIndex=filePath.lastIndexOf(".");
        return pointIndex>0?filePath.substring( pointIndex+ 1):filePath;
    }

    //递归获取文件夹大小
    public static long getTotalSizeOfFilesInDir(File dirFile) {
        if (dirFile.isFile())
            return dirFile.length();
        final File[] children = dirFile.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }


    //从指定目录查找指定文件
    public static List<File> getSpecFilesFromDir(String dirPath, String suffix) {
        return getSpecFilesFromDir(dirPath,new String[]{suffix});
    }

    //从指定目录查找指定文件
    public static List<File> getSpecFilesFromDir(String dirPath, String[] suffixs) {
        List<File> fileList = new ArrayList<>();
        if (EmptyUtils.isEmpty(suffixs))return fileList;
        File file = new File(dirPath);
        if (!file.exists()) return fileList;
        File[] files = file.listFiles();
        if (files == null || files.length == 0) return fileList;
        for (File f : files) {
            String path = f.getPath();
            if (f.isDirectory()) {
                if (!path.contains("/.")) {//过滤忽略文件夹
                    List<File> list = getSpecFilesFromDir(path, suffixs);
                    if (EmptyUtils.isNotEmpty(list)) fileList.addAll(list);
                }
            } else {
                for (String suffix:suffixs){
                    if (path.endsWith(suffix)) {
                        fileList.add(new File(path));
                    }
                }

            }
        }
        return fileList;
    }


   //m3u8 里头的Ts 文件
    public static List<String> getAllTSFilePath(String m3u8FilePath){
        List<String> tsFileList=new ArrayList<>();
        String filePath = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(m3u8FilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("file://")&&line.endsWith(".ts")) {
                    tsFileList.add(line);
                }
            }
            reader.close();
            IOCloser.closeIO(fileInputStream);
            return tsFileList;
        } catch (Exception e) {
            e.printStackTrace();
            IOCloser.closeIO(fileInputStream);
        }
        return tsFileList;
    }

    //获取m3u8 里头的ts 所在的文件夹
    public static String getTSFileDirPath(String m3u8FilePath) {
        String filePath = "";
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(m3u8FilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            //找到就退出
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("file://")&&line.endsWith("ts")) {
                    //获取目录路径
                    filePath = new File(line).getParentFile().getPath();
                    filePath=filePath.substring("file://".length()-1);
                    //  Logger.e(filePath);
                    break;
                }

            }
            reader.close();
            IOCloser.closeIO(fileInputStream);
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            IOCloser.closeIO(fileInputStream);
        }
        return "";
    }

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

    public static void notifyScanImageFile(Context context, String desFilePath) {
        notifyScanImageFile(context, desFilePath, false);
    }

    /**
     * 一般图片放在sd卡根目录，或者根目录的文件夹内只要通知刷新即可，
     * 如果要放在深层次的文件目录中调用刷新大部分情况下是不会有效果的，
     * 比如在私有目录，Android/data/包名/
     *
     * @param context
     * @param desFilePath
     * @param insertRecord
     */
    public static void notifyScanImageFile(Context context, String desFilePath, boolean insertRecord) {
        if (insertRecord) {
            try {
                //手动往媒体库插入记录
                String suffix = desFilePath.substring(desFilePath.lastIndexOf(".") + 1);
                if (EmptyUtils.isNotEmpty(suffix)) {
                    suffix = suffix.toLowerCase();
                    if ("png".equals(suffix) || "jpg".equals(suffix) || "jpeg".equals(suffix)) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DATA, desFilePath);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + (suffix.equals("jpg") ? "jpeg" : suffix));
                        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    }
                }
            } catch (Exception e) {
                ELog.e(e);
            }
        }
        //通知更新
        MediaScannerConnection.scanFile(context, new String[]{desFilePath}, null, null);
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

    private static boolean delectDirFile(String dirPath) {
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


    public static boolean createDirIfNotExists(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return false;
    }

    public static boolean createFileIfNotExists(String filePath) {
        File file = new File(filePath);
        //先判断父目录
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        //创建目标文件
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
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
