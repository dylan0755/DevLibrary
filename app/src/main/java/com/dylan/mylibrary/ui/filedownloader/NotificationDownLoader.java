package com.dylan.mylibrary.ui.filedownloader;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.dylan.library.utils.ToastUtils;
import com.dylan.mylibrary.R;
import com.dylan.library.io.FileDownLoader;

/**
 * Created by Dylan on 2017/12/6.
 */

public class NotificationDownLoader implements FileDownLoader.DownLoadListener {
    private String mTitleName;
    private boolean hasStart;
    public static final int NOTIFY_ID = 25;
    private Handler mNotificationHandler;
    private final int UPDATE_NOTIFICATION_WHAT=20;
    private Notification mNotification;
    private int currentProgress;
    NotificationManager mNotificationManager;
    private Context mContext;
    private FileDownLoader mFileDownLoader;
    private DownLoadNotificationBroadCast mBroadCast;

    public NotificationDownLoader(Context context) {
        mContext = context;
        mFileDownLoader = new FileDownLoader();
        mFileDownLoader.setDownLoadListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownLoadNotificationBroadCast.ACTION_CANCEL_DOWNLOAD);
        filter.addAction(DownLoadNotificationBroadCast.ACTION_PAUSE_DOWNLOAD);
        mBroadCast = new DownLoadNotificationBroadCast();
        context.registerReceiver(mBroadCast, filter);
        mNotificationHandler=new Handler(Looper.getMainLooper()){
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void handleMessage(Message msg) {
                 if (msg.what==UPDATE_NOTIFICATION_WHAT){
                     if (!isPause()){
                         RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_download);
                         mNotification.bigContentView = remoteViews;
                         mNotification.bigContentView.setTextViewText(R.id.title_download, mTitleName);
                         mNotification.bigContentView.setImageViewResource(R.id.download_logo, R.mipmap.ic_launcher);
                         mNotification.bigContentView.setTextViewText(R.id.progress_download, currentProgress + "%");
                         mNotification.bigContentView.setProgressBar(R.id.pb_download, 100, currentProgress, false);
                         mNotification.bigContentView.setTextViewText(R.id.tv_pause, "暂停");
                         mNotification.bigContentView.setOnClickPendingIntent(R.id.cancel_download, PendingIntent.getBroadcast(mContext, 0, new Intent(DownLoadNotificationBroadCast.ACTION_CANCEL_DOWNLOAD), 0));
                         Intent intent = new Intent(DownLoadNotificationBroadCast.ACTION_PAUSE_DOWNLOAD);
                         intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                         mNotification.bigContentView.setOnClickPendingIntent(R.id.pause_download, PendingIntent.getBroadcast(mContext, 0,intent , 0));
                         mNotificationManager.notify(NOTIFY_ID, mNotification);
                         if (currentProgress!=100){
                             mNotificationHandler.sendEmptyMessageDelayed(UPDATE_NOTIFICATION_WHAT,1000);
                         }
                     }

                 }
                super.handleMessage(msg);
            }
        };
    }

    public void downLoad(Context context, String downloadUrl, String downLoadFileName) {
        mTitleName =downLoadFileName;
        mFileDownLoader.downLoad(context, downloadUrl,downLoadFileName);
    }


    @Override
    public void onDownLoadFileName(String fileName) {
        mTitleName =fileName;

    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onError(int erroType, String error) {
        if (erroType==FileDownLoader.ERROR_DONWLOAD){
            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(mContext);
            mNotification=notifyBuilder.setSmallIcon(R.mipmap.ic_launcher) .setOngoing(false).build();
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_download_erro);
            mNotification.bigContentView = remoteViews;
            mNotification.bigContentView.setImageViewResource(R.id.download_logo,R.mipmap.ic_launcher);
            mNotification.bigContentView.setTextViewText(R.id.title_download, mTitleName);
            mNotificationManager.notify(NOTIFY_ID, mNotification);
            mNotificationHandler.removeMessages(UPDATE_NOTIFICATION_WHAT);
        }else{
            ToastUtils.show(error);
        }
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onProgress(long totalSize, long hasDownLoadSize, final int progressPercent) {
        currentProgress = progressPercent;
        if (!hasStart) {
            hasStart = true;
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
            builder.setOngoing(true);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            mNotification = builder.build();
            // 指定个性化视图 使用 bigContentView  不要使用contentView
            mNotification.bigContentView = new RemoteViews(mContext.getPackageName(), R.layout.notification_download);
            mNotification.bigContentView.setImageViewResource(R.id.download_logo, R.mipmap.ic_launcher);
            mNotification.bigContentView.setTextViewText(R.id.title_download, mTitleName);
            mNotification.bigContentView.setProgressBar(R.id.pb_download, 100, 0, false);
            mNotification.bigContentView.setTextViewText(R.id.tv_pause, "暂停");
            mNotification.bigContentView.setOnClickPendingIntent(R.id.cancel_download, PendingIntent.getBroadcast(mContext, 0, new Intent(DownLoadNotificationBroadCast.ACTION_CANCEL_DOWNLOAD), 0));
            mNotification.bigContentView.setOnClickPendingIntent(R.id.pause_download, PendingIntent.getBroadcast(mContext, 1, new Intent(DownLoadNotificationBroadCast.ACTION_PAUSE_DOWNLOAD), 0));
            mNotificationManager.notify(NOTIFY_ID, mNotification);
            mNotificationHandler.sendEmptyMessage(UPDATE_NOTIFICATION_WHAT);
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onComplete(long totalSize, String downLoadFilePath) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(mContext);
        mNotification=notifyBuilder.setSmallIcon(R.mipmap.ic_launcher) .setOngoing(false).build();
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_download_complete);
        mNotification.bigContentView = remoteViews;
        mNotification.bigContentView.setImageViewResource(R.id.download_logo, R.mipmap.ic_launcher);
        mNotification.bigContentView.setTextViewText(R.id.title_download, mTitleName);
        mNotificationManager.notify(NOTIFY_ID, mNotification);
        mNotificationHandler.removeMessages(UPDATE_NOTIFICATION_WHAT);
    }



    class DownLoadNotificationBroadCast extends BroadcastReceiver {

        public static final String ACTION_CANCEL_DOWNLOAD = "cancel_download";
        public static final String ACTION_PAUSE_DOWNLOAD = "pause_download";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_CANCEL_DOWNLOAD.equals(intent.getAction())) {//取消下载
                cancel();
            } else if (ACTION_PAUSE_DOWNLOAD.equals(ACTION_PAUSE_DOWNLOAD)) {//暂停下载
                if (mFileDownLoader.isPause()) {
                    restart();
                } else {
                    pause();

                }

            }
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void restart() {
        mFileDownLoader.restart();
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_download);
        mNotification.bigContentView = remoteViews;
        mNotification.bigContentView.setTextViewText(R.id.title_download, mTitleName);
        mNotification.bigContentView.setImageViewResource(R.id.download_logo, R.mipmap.ic_launcher);
        mNotification.bigContentView.setTextViewText(R.id.tv_pause, "暂停");
        mNotification.bigContentView.setTextViewText(R.id.progress_download, currentProgress + "%");
        mNotification.bigContentView.setProgressBar(R.id.pb_download, 100, currentProgress, false);
        mNotification.bigContentView.setOnClickPendingIntent(R.id.cancel_download, PendingIntent.getBroadcast(mContext, 0, new Intent(DownLoadNotificationBroadCast.ACTION_CANCEL_DOWNLOAD), 0));
        mNotification.bigContentView.setOnClickPendingIntent(R.id.pause_download, PendingIntent.getBroadcast(mContext, 0, new Intent(DownLoadNotificationBroadCast.ACTION_PAUSE_DOWNLOAD), 0));
        mNotificationManager.notify(NOTIFY_ID, mNotification);
        mNotificationHandler.removeMessages(UPDATE_NOTIFICATION_WHAT);
        mNotificationHandler.sendEmptyMessage(UPDATE_NOTIFICATION_WHAT);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void pause() {
        mFileDownLoader.pause();
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_download);
        mNotification.bigContentView = remoteViews;
        mNotification.bigContentView.setImageViewResource(R.id.download_logo, R.mipmap.ic_launcher);
        mNotification.bigContentView.setTextViewText(R.id.title_download, mTitleName);
        mNotification.bigContentView.setTextViewText(R.id.tv_pause, "继续");
        mNotification.bigContentView.setTextViewText(R.id.progress_download, currentProgress + "%");
        mNotification.bigContentView.setProgressBar(R.id.pb_download, 100, currentProgress, false);
        mNotification.bigContentView.setOnClickPendingIntent(R.id.cancel_download, PendingIntent.getBroadcast(mContext, 0, new Intent(DownLoadNotificationBroadCast.ACTION_CANCEL_DOWNLOAD), 0));
        mNotification.bigContentView.setOnClickPendingIntent(R.id.pause_download, PendingIntent.getBroadcast(mContext, 0, new Intent(DownLoadNotificationBroadCast.ACTION_PAUSE_DOWNLOAD), 0));
        mNotificationManager.notify(NOTIFY_ID, mNotification);
        mNotificationHandler.removeMessages(UPDATE_NOTIFICATION_WHAT);
    }

    public boolean isPause() {
        return mFileDownLoader.isPause();
    }

    public void cancel() {
        mFileDownLoader.cancel();
        mNotificationManager.cancel(NOTIFY_ID);
        mNotificationHandler.removeMessages(UPDATE_NOTIFICATION_WHAT);
    }

    public void destroy() {
        mContext.unregisterReceiver(mBroadCast);
        mFileDownLoader.setDownLoadListener(null);
        mNotificationHandler.removeMessages(UPDATE_NOTIFICATION_WHAT);
        mFileDownLoader = null;
        mContext = null;
        mNotification = null;
        mNotificationManager = null;
        mNotificationHandler=null;
    }




}
