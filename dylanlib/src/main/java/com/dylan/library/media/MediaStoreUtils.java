package com.dylan.library.media;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.dylan.library.io.FileUtils;
import com.dylan.library.utils.EmptyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dylan
 * Date: 2020/2/24
 * Desc:
 */
public class MediaStoreUtils {
    public static final int SOURCE_TYPE_EXTERNAL=0; //sd 文件
    public static final int SOURCE_TYPE_MEDIASTORE_VIDEO=100;  // 视频库
    public static final int SOURCE_TYPE_MEIDASTORE_FILE=200; //文件库

    public static final String[] LocalVideoColumns = {
            MediaStore.Video.Media._ID, // 视频id
            MediaStore.Video.Media.DATA, // 视频路径
            MediaStore.Video.Media.SIZE, // 视频字节大小
            MediaStore.Video.Media.DISPLAY_NAME, // 视频名称 xxx.mp4
            MediaStore.Video.Media.TITLE, // 视频标题
            MediaStore.Video.Media.DURATION, // 视频时长
            MediaStore.Video.Media.MIME_TYPE

    };

    public static boolean insertVideo(Context context,String newPath){
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Video.Media.DATA,newPath);
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME,getFileNameFromPath(newPath));
        contentValues.put(MediaStore.Video.Media.TITLE,getFileNameWithoutSuffix(newPath));
        Uri result=resolver.insert(uri,contentValues);
        return result!=null;
    }


    public static int updateVideoInVideoStore(Context context, String oldPath, String newPath){
        ContentResolver resolver = context.getContentResolver();
        Uri uri = getVideoStoreUri();
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Video.Media.DATA,newPath);
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME,getFileNameFromPath(newPath));
        contentValues.put(MediaStore.Video.Media.TITLE,getFileNameWithoutSuffix(newPath));
        int result=resolver.update(uri,contentValues, MediaStore.Video.Media.DATA+"=?", new String[]{oldPath});
        if (result>0){
            FileUtils.notifyScanFile(context,newPath);
        }
        return result;
    }

    public static int updateVideoInFileStore(Context context, String oldPath, String newPath){
        ContentResolver resolver = context.getContentResolver();
        Uri uri = getFileStoreUri();
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns.DATA,newPath);
        int result=resolver.update(uri,contentValues, MediaStore.Files.FileColumns.DATA+"=?", new String[]{oldPath});
        if (result>0){
            FileUtils.notifyScanFile(context,newPath);
        }
        return result;
    }


    public static List<MediaStoreFile> getVideosFromVideoStore(Context context){
       List<MediaStoreFile> mediaStoreFiles = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(getVideoStoreUri(), LocalVideoColumns,
                null, null, MediaStore.Video.VideoColumns.DATE_ADDED + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MediaStoreFile storeFile = new MediaStoreFile();
                storeFile.setSourceType(MediaStoreUtils.SOURCE_TYPE_MEDIASTORE_VIDEO);
                storeFile.set_ID(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
                storeFile.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                storeFile.setLength(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
                storeFile.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                storeFile.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
                storeFile.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
                storeFile.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
                mediaStoreFiles.add(storeFile);
            }
            cursor.close();
        }

        return mediaStoreFiles;
    }


    //从多媒体文件库中查找
    public static List<MediaStoreFile> getFilesFromFileStore(Context context, String[] suffix, String[] filters) {
        List<MediaStoreFile> mediaStoreFiles = new ArrayList<>();
        //从外存中获取
        Uri fileUri = getFileStoreUri();
        //筛选列，这里只筛选了：文件路径和不含后缀的文件名
        String[] projection = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MIME_TYPE
        };


        //构造筛选语句
        String selection = "";
        for (int i = 0; i < suffix.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + suffix[i] + "'";
        }
        //按时间递增顺序对结果进行排序;待会从后往前移动游标就可实现时间递减
        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;
        //获取内容解析器对象
        ContentResolver resolver = context.getContentResolver();
        //获取游标
        Cursor cursor = resolver.query(fileUri, projection, selection, null, sortOrder);
        if (cursor == null) return mediaStoreFiles;
        //游标从最后开始往前递减，以此实现时间递减顺序（最近访问的文件，优先显示）
        if (cursor.moveToLast()) {
            do {
                //输出文件的完整路径
                int id=cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                String displayName=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String mimeType=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                if (!path.contains("/.")) {
                    boolean isFilter = false;
                    //是否包含过滤条件
                    if (filters != null && filters.length > 0) {
                        for (String filter : filters) {
                            if (EmptyUtils.isEmpty(filter)) continue;
                            if (path.contains(filter)) isFilter = true;
                        }
                    }
                    if (!isFilter){
                        File file=new File(path);
                        String fileName = FileUtils.getFileNameFromPath(path);
                        MediaStoreFile mediaStoreFile =new MediaStoreFile();
                        mediaStoreFile.setSourceType(MediaStoreUtils.SOURCE_TYPE_MEIDASTORE_FILE);
                        mediaStoreFile.set_ID(id);
                        mediaStoreFile.setPath(path);
                        mediaStoreFile.setDisplayName(displayName);
                        mediaStoreFile.setName(fileName);
                        mediaStoreFile.setLength(file.length());
                        mediaStoreFile.setModifyTime(file.lastModified());
                        mediaStoreFile.setMimeType(mimeType);
                        mediaStoreFiles.add(mediaStoreFile);
                    }
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        return mediaStoreFiles;

    }




    //从媒体库中删除视频数据
    public static boolean deleteVideoStoreFile(Context context, String deleteFilePath) {
        if (context == null) return false;
        ContentResolver resolver = context.getContentResolver();
        int delRows = resolver.delete(getVideoStoreUri(), MediaStore.Video.Media.DATA + "=?", new String[]{deleteFilePath});
        return delRows == 1;
    }
    //从媒体库中删除图片数据
    public static boolean deleteImageStoreFile(Context context, String deleteFilePath) {
        if (context == null) return false;
        ContentResolver resolver = context.getContentResolver();
        int delRows = resolver.delete(getImageStoreUri(), MediaStore.Images.Media.DATA + "=?", new String[]{deleteFilePath});
        return delRows == 1;
    }

    //从媒体库中删除音频数据
    public static boolean deleteAudioStoreFile(Context context, String deleteFilePath) {
        if (context == null) return false;
        ContentResolver resolver = context.getContentResolver();
        int delRows = resolver.delete(getAudioStoreUri(), MediaStore.Audio.Media.DATA + "=?", new String[]{deleteFilePath});
        return delRows == 1;
    }

    //从媒体库中删除文件
    public static boolean deleteFileStoreFile(Context context, String deleteFilePath) {
        if (context == null) return false;
        ContentResolver resolver = context.getContentResolver();
        int delRows = resolver.delete(getFileStoreUri(),MediaStore.Files.FileColumns.DATA+ "=?", new String[]{deleteFilePath});
        return delRows == 1;
    }


    public static Uri getImageStoreUri(){
        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    public static Uri getVideoStoreUri(){
        return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    }

    public static Uri getAudioStoreUri(){
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    public static Uri getFileStoreUri(){
        return MediaStore.Files.getContentUri("external");
    }

    private static String getFileNameWithoutSuffix(String filePath){
        return FileUtils.getFileNameWithoutSuffix(filePath);
    }

    private static String getFileNameFromPath(String filePath) {
        return FileUtils.getFileNameFromPath(filePath);
    }





}
