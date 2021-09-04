package com.dylan.library.io;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import com.dylan.library.device.SDCardUtils;
import com.dylan.library.exception.ELog;
import com.dylan.library.graphics.BitmapHelper;
import com.dylan.library.manager.ExternalStorageDir;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.utils.MD5Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by Dylan on 2016/12/31.
 */

public class FileUtils {


    public static String getCameraRootDir(){
        return ExternalStorageDir.CameraRootDir;
    }





    public static boolean isPicture(String fileNameOrPath) {
        String suffix = fileNameOrPath.substring(fileNameOrPath.lastIndexOf(".") + 1).toLowerCase();
        if (suffix.equals("jpg") || suffix.equals("png")
                || suffix.equals("jpeg") || suffix.equals("gif")) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isExists(String path) {
        return new File(path).exists();
    }

    public static boolean delete(String path) {
        if (EmptyUtils.isEmpty(path)) return false;
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                //如果是m3u8 则删除ts 文件
                if (path.endsWith(".m3u8") || path.endsWith(".M3U8")) {
                    String dirPath = getTSFileDirPath(path);
                    if (isExists(dirPath)) {
                        delectDirFile(dirPath);
                    }
                    //ts 有可能和当前的m3u8 同一个文件夹，所以删除了文件夹还要判断
                    // m3u8 是否存在，存在则继续删除
                    if (file.exists()) {
                        return file.delete();
                    } else {
                        return true;
                    }
                } else {
                    return file.delete();
                }
            } else if (file.isDirectory()) {
                return delectDirFile(path);
            }
        }
        return false;
    }

    //通知更新单个文件
    public static void notifyScanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }


    public static String getFileNameFromPath(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    public static String getFileNameWithoutSuffix(String filePath) {
        int startIndex = filePath.lastIndexOf("/") + 1;
        int endIndex = filePath.lastIndexOf(".");
        endIndex = endIndex > 0 ? endIndex : filePath.length();
        return filePath.substring(startIndex, endIndex);
    }

    public static String getFileSuffixFromPath(String filePath) {
        int index=filePath.lastIndexOf("/");
        if (index>0){
            String fileName=filePath.substring(index+1);
            int pointIndex = fileName.lastIndexOf(".");
            return pointIndex > 0 ? fileName.substring(pointIndex + 1) : "";
        }
        return "";
    }

    public static String getReNameFilePath(String path, String fileName) {
        String dirPath = new File(path).getParentFile().getAbsolutePath();
        String suffix = getFileSuffixFromPath(path);
        if (fileName == null || fileName.isEmpty()) fileName = "" + System.currentTimeMillis();
        return dirPath + "/" + fileName +"."+suffix;
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
        return getSpecFilesFromDir(dirPath, new String[]{suffix});
    }

    //从指定目录查找指定文件
    public static List<File> getSpecFilesFromDir(String dirPath, String[] suffixs) {
        List<File> fileList = new ArrayList<>();
        if (EmptyUtils.isEmpty(suffixs)) return fileList;
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
                for (String suffix : suffixs) {
                    if (path.endsWith(suffix)) {
                        fileList.add(new File(path));
                    }
                }

            }
        }
        return fileList;
    }


    //m3u8 里头的Ts 文件
    public static List<String> getAllTSFilePath(String m3u8FilePath) {
        List<String> tsFileList = new ArrayList<>();
        String filePath = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(m3u8FilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("file://") && line.endsWith(".ts")) {
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
                if (line.startsWith("file://") && line.endsWith("ts")) {
                    //获取目录路径
                    filePath = new File(line).getParentFile().getPath();
                    filePath = filePath.substring("file://".length() - 1);
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


    //升序
    @SuppressWarnings("all")
    public static List<File> sortASC(List<File> fileList) {
        if (fileList == null || fileList.isEmpty()) return fileList;
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                try {
                    //  jdk 7 的排序，不能使用下面的，否则会报错
                    return o1.lastModified() == o2.lastModified() ? 0 :
                            (o1.lastModified() > o2.lastModified() ? 1 : -1);
                } catch (Exception e) {
                    return 0;
                }
            }
        });
        return fileList;
    }

    //降序
    @SuppressWarnings("all")
    public static List<File> sortDESC(List<File> fileList) {
        if (fileList == null || fileList.isEmpty()) return fileList;

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                try {
                    //  jdk 7 的排序，不能使用下面的，否则会报错
                    return o1.lastModified() == o2.lastModified() ? 0 :
                            (o1.lastModified() > o2.lastModified() ? -1 : 1);
                } catch (Exception e) {
                    return 0;
                }
            }
        });
        return fileList;
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void copyFile(File src, File dest) throws IOException {
        copyFile(new FileInputStream(src), dest);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void copyFile(InputStream is, File dest) throws IOException {
        if (is == null) {
            return;
        }
        if (dest.exists()) {
            dest.delete();
        }
        try (BufferedInputStream bis = new BufferedInputStream(is); BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {
            byte[] bytes = new byte[1024 * 10];
            int length;
            while ((length = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, length);
            }
            bos.flush();
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


    public static String getStringFromInputStream(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int i = -1;
            while (true) {
                if (!((i = is.read()) != -1)) break;
                baos.write(i);
            }
            String result = baos.toString();
            baos.close();
            is.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void unZip(String srcZipPath, String dirName) throws IOException {
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


    public static void unZipAssetsFolder(Context context, String zipFileName, String outPathString) throws Exception {

        ZipInputStream inZip = new ZipInputStream(context.getAssets().open(zipFileName));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }


    public static File getFileByUri(Uri uri, Context context) {
        String filePath=null;
        try {
            filePath= getPathByUri4kitkat(context, uri);
        }catch (Exception e){
            filePath=GetRealPath.getFPUriToPath(context,uri);
        }
        if (EmptyUtils.isNotEmpty(filePath)) return new File(filePath);
        return null;
    }

    //从Uri获取文件绝对路径
    @SuppressLint("NewApi")
    private static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            // (and
            // general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            } catch (Exception e) {

            }
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }



    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
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

    public static String getFileMD5String(File file) throws IOException {
        return MD5Utils.getFileMD5String(file);
    }


    public static String modifyFileMD5String(File file) {
        //在文本文本中追加内容
        BufferedWriter out = null;
        try {
            String content = UUID.randomUUID().toString().replace("-", "");
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.newLine();//换行
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //返回一个获取视频的MD5
        try {
            return FileUtils.getFileMD5String(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getFileSHA1String(File file) {
        int bufferSize = 1024;
        FileInputStream fis = null;
        DigestInputStream dis = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis, messageDigest);
            byte[] buffer = new byte[bufferSize];

            while (dis.read(buffer) > 0) ;
            messageDigest = dis.getMessageDigest();
            byte[] array = messageDigest.digest();
            StringBuilder hex = new StringBuilder(array.length * 2);
            for (byte b : array) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Formatter.formatFileSize  这个类会出现 单位为中文的情况，如 637KB 变成 637千字节
     *
     * @param size
     * @return
     */
    public static String getFormatFileSize(long size) {
        double kiloByte = size * 1.0 / 1024;
        if (kiloByte < 1) {
            return "0 B";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + " GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + " TB";
    }





}
