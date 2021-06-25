package com.dylan.library.media;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan on 2016/9/22.
 */
public class LocalMediaLoader {
    /**
     *
     *加载本地视频
     */
    public static List<VideoItem> getLocalVideo(Context context){
        if (context==null)return new ArrayList<>();
        ContentResolver mResolver=context.getContentResolver();
            String[] projects={
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE
            };
        String orderBy = MediaStore.Video.Media.DISPLAY_NAME;
        Uri uri=MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor =mResolver.query(uri, projects, null,  null, orderBy);
        if (cursor==null)  return null;
         List<VideoItem> videoItemList=new ArrayList<VideoItem>();
        while (cursor.moveToNext()) {
            String id=cursor.getString(cursor.getColumnIndex(projects[0]));
            String path=cursor.getString(cursor.getColumnIndex(projects[1]));
            String name=cursor.getString(cursor.getColumnIndex(projects[2]));
            long duration=cursor.getLong(cursor.getColumnIndex(projects[3]));
            long size=cursor.getLong(cursor.getColumnIndex(projects[4]));
            VideoItem videoItem=new VideoItem();
            videoItem.setVideoName(name);
            videoItem.setDuration(duration);
            videoItem.setVideoPath(path);
            String videoSize=Formatter.formatFileSize(context,size);
            videoItem.setSize(videoSize);
            videoItemList.add(videoItem);
        }
       cursor.close();
        return videoItemList;
    }

    /**
     *
     * 加载本地歌曲
     */
    public static List<AudioItem> getLocalAudio(Context context){
        if (context==null)return new ArrayList<>();
        ContentResolver mResolver=context.getContentResolver();
        String[] projects={
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID


        };
        String orderBy = MediaStore.Audio.Media.DISPLAY_NAME;
        Uri uri=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor =mResolver.query(uri, projects, null,  null, orderBy);
        if (cursor==null)  return null;
        List<AudioItem> audioItemList=new ArrayList<AudioItem>();
        while (cursor.moveToNext()) {
            long id=cursor.getLong(cursor.getColumnIndex(projects[0]));
            String path=cursor.getString(cursor.getColumnIndex(projects[1]));
            String name=cursor.getString(cursor.getColumnIndex(projects[2]));
            long duration=cursor.getLong(cursor.getColumnIndex(projects[3]));
            long size=cursor.getLong(cursor.getColumnIndex(projects[4]));
            long albumId = cursor.getInt(cursor.getColumnIndex(projects[5]));
            AudioItem audioItem=new AudioItem();
            audioItem.setId(id);
            audioItem.setAlbum_id(albumId);
            audioItem.setAudioName(name);
            audioItem.setDuration(duration);
            audioItem.setAudioPath(path);
            String videoSize=Formatter.formatFileSize(context,size);
            audioItem.setSize(videoSize);
            audioItemList.add(audioItem);
        }
        cursor.close();
        return audioItemList;
    }



    /**
     *
     * 加载本地歌曲
     */
    public static List<ImageItem> getLocalImage(Context context){
        if (context==null)return new ArrayList<>();
        ContentResolver mResolver=context.getContentResolver();
        String[] projects={
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
        };
        String orderBy = MediaStore.Images.Media.DISPLAY_NAME;
        Uri uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor =mResolver.query(uri, projects, null,  null, orderBy);
        if (cursor==null)  return null;
        List<ImageItem> audioItemList=new ArrayList<ImageItem>();
        while (cursor.moveToNext()) {
            long id=cursor.getLong(cursor.getColumnIndex(projects[0]));
            String path=cursor.getString(cursor.getColumnIndex(projects[1]));
            String name=cursor.getString(cursor.getColumnIndex(projects[2]));
            long size=cursor.getLong(cursor.getColumnIndex(projects[3]));

            ImageItem imageItem=new ImageItem();
            imageItem.setId(id);
            imageItem.setImagepath(path);
            imageItem.setImagename(name);
            String imagesize=Formatter.formatFileSize(context,size);
            imageItem.setSize(imagesize);
            audioItemList.add(imageItem);
        }
        cursor.close();
        return audioItemList;
    }









    /**
     * 视频类
     */
    public static class VideoItem implements Serializable {
        private String videoPath;
        private String videoName;
        private String size;
        private long duration;

        public String getVideoPath() {
            return videoPath;
        }

        public void setVideoPath(String videoPath) {
            this.videoPath = videoPath;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public String getVideoName() {
            return videoName;
        }

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public String getFileSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return "VideoItem{" +
                    "videoPath='" + videoPath + '\'' +
                    ", videoName='" + videoName + '\'' +
                    ", size='" + size + '\'' +
                    ", duration=" + duration +
                    '}';
        }
    }

    /**
     * 音频类、歌曲
     */

    public static class AudioItem implements Serializable {
        private String audioPath;
        private String audioName;
        private String size;
        private long duration;
        private long id;

        public long getAlbum_id() {
            return album_id;
        }

        public void setAlbum_id(long album_id) {
            this.album_id = album_id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        private long album_id;//两个id用于获取专辑图片


        @Override
        public String toString() {
            return "AudioItem{" +
                    "audioPath='" + audioPath + '\'' +
                    ", audioName='" + audioName + '\'' +
                    ", size='" + size + '\'' +
                    ", duration=" + duration +
                    ", id='" + id + '\'' +
                    ", album_id='" + album_id + '\'' +
                    '}';
        }


        public String getAudioPath() {
            return audioPath;
        }

        public void setAudioPath(String audioPath) {
            this.audioPath = audioPath;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public String getAudioName() {
            return audioName;
        }

        public void setAudioName(String audioName) {
            this.audioName = audioName;
        }

        public String getFileSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
        public String getSize() {
            return size;
        }
    }

    /**
     *
     * 图片类
     */

    public static class ImageItem implements Serializable{
        private long id;
        private String imagepath;
        private String imagename;
        private String size;


        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getImagepath() {
            return imagepath;
        }

        public void setImagepath(String imagepath) {
            this.imagepath = imagepath;
        }

        public String getImagename() {
            return imagename;
        }

        public void setImagename(String imagename) {
            this.imagename = imagename;
        }

        @Override
        public String toString() {
            return "ImageItem{" +
                    "id=" + id +
                    ", imagepath='" + imagepath + '\'' +
                    ", imagename='" + imagename + '\'' +
                    ", size='" + size + '\'' +
                    '}';
        }
    }



    //视频的缩略图
    public static Bitmap getVideoThumbnail(Context context, String filepath) {
        if (context==null)return null;
        ContentResolver cr=context.getContentResolver();
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        String where = MediaStore.Audio.Media.DATA + "='"+filepath+"'";
        Cursor cursor=cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, where, null, null);
        if (cursor == null|| cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst(); //将游标指向第一条记录，用moveToNext()也是同样的作用效果
        String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));  //image id in image table.s
        if (videoId == null) {
            return null;
        }
        cursor.close();
        long videoIdLong = Long.parseLong(videoId);                           //MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
        bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, videoIdLong, MediaStore.Images.Thumbnails.MINI_KIND, options);
        return bitmap;
    }


    /**
     * 从文件当中获取专辑封面位图
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    public  static Bitmap getArtworkFromFile(Context context, long songid, long albumid){
        if (context==null)return null;
        final Uri albumArtUri = Uri
                .parse("content://media/external/audio/albumart");
        Bitmap bm = null;
        if(albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if(albumid < 0){
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            // 只进行大小判断
            options.inJustDecodeBounds = true;
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            // 我们的目标是在800pixel的画面上显示
            // 所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100;
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }


    public static void loadVideoThumb(Context context, String path,  ImageView imageview){
        if (context==null)return;
        LruCacheUtil.getInstance(context).loadImage(path,imageview);
    }

    public static void recycle(Context context){
        if (context==null)return;
        LruCacheUtil.getInstance(context).recycle();
    }


    private  static class LruCacheUtil {
        public static LruCacheUtil mInstance;
        private LruCache<String, Bitmap> mLruCache;
        private List<String> keyList=new ArrayList<String>();
        private Context mContext;
        private Handler mHandler;
        private LruCacheUtil(Context context) {
            mContext=context;
            mHandler=new Handler();
            int maxmemory = (int) Runtime.getRuntime().maxMemory();
            int cachesize = maxmemory / 8;//设置缓存大小
            mLruCache = new LruCache<String, Bitmap>(cachesize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    int count=value.getByteCount();
                    keyList.add(key);
                    Log.e("sizeOf: ","count "+count );
                    return count;
                }
            };
        }

        public static LruCacheUtil getInstance(Context context) {

            if (mInstance == null) {
                synchronized (LruCacheUtil.class) {
                    if (mInstance == null) {
                        mInstance = new LruCacheUtil(context);
                        return mInstance;
                    }
                }
            }
            return mInstance;
        }


        private Bitmap getBitmapFromCache(String path) {
            return mLruCache.get(path);
        }


        public void loadImage(final String path, final ImageView imageview) {
            final Bitmap bitmap = getBitmapFromCache(path);
            if (bitmap == null) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitm=getVideoThumbnail(mContext,path);
                        if (bitm!=null){
                            mLruCache.put(path, bitm);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageview.setImageBitmap(bitm);
                                }
                            });
                        }else{
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    imageview.setImageResource(0);
                                }
                            });
                        }

                    }
                }).start();

            } else {
                imageview.setImageBitmap(bitmap);
            }

        }


        public  void recycle() {
            for (String key: keyList){
                if (mLruCache.get(key)!=null){
                    mLruCache.get(key).recycle();
                }
            }
            keyList.clear();
            mLruCache.evictAll();
        }

    }
}
