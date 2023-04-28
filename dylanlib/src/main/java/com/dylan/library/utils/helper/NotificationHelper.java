package com.dylan.library.utils.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * Author: Dylan
 * Date: 2020/9/28
 * Desc:
 */
public class NotificationHelper {

    private static NotificationHelper helper;
    private NotificationCompat.Builder builder;
    private Context mContext;

    public static NotificationHelper getInstance(Context context) {
        synchronized (NotificationHelper.class) {
            if (helper == null) {
                helper = new NotificationHelper(context);
            }
        }
        return helper;
    }


    private NotificationHelper(Context context) {
        mContext = context;
        String CHANNEL_ID = context.getPackageName();
        String CHANNEL_NAME = "TEST";
        builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public NotificationHelper setContentInfo(String contentInfo) {
        builder.setContentInfo(contentInfo);
        return this;
    }

    public NotificationHelper setContentTitle(CharSequence title) {
        builder.setContentTitle(title);
        return this;
    }


    public NotificationHelper setContentText(CharSequence title) {
        builder.setContentText(title);
        return this;
    }

    public NotificationHelper setWhen(long when) {
        builder.setWhen(when);
        return this;
    }


    public NotificationHelper setSmallIcon(int icon) {
        builder.setSmallIcon(icon);
        return this;
    }

    public NotificationHelper setSmallIcon(int icon, int level) {
        builder.setSmallIcon(icon, level);
        return this;
    }

    public NotificationHelper setLargeIcon(Bitmap bitmap) {
        builder.setLargeIcon(bitmap);
        return this;
    }

    public NotificationHelper setContentIntent(PendingIntent pendingIntent) {
        builder.setContentIntent(pendingIntent);
        return this;
    }

    public NotificationHelper setSingleTopContentIntent(Class desClass) {
        Intent intent = new Intent(mContext, desClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        return this;
    }

    public NotificationHelper setSingleTopContentIntent(int requestCode, Class desClass) {
        Intent intent = new Intent(mContext, desClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, requestCode, intent, 0);
        builder.setContentIntent(pendingIntent);
        return this;
    }

    public NotificationHelper setContent(RemoteViews contentView) {
        builder.setContent(contentView);
        return this;
    }

    public NotificationHelper setCustomBigContentView(RemoteViews contentView) {
        builder.setCustomBigContentView(contentView);
        return this;
    }


    public NotificationHelper setCustomContentView(RemoteViews contentView) {
        builder.setCustomContentView(contentView);
        return this;
    }

    public NotificationHelper setCustomHeadsUpContentView(RemoteViews contentView) {
        builder.setCustomHeadsUpContentView(contentView);
        return this;
    }

    public NotificationHelper setAutoCancel(boolean autoCancel) {
        builder.setAutoCancel(autoCancel);
        return this;
    }

    public NotificationHelper setColor(int argb) {
        builder.setColor(argb);
        return this;
    }

    public NotificationHelper setBadgeIconType(int icon) {
        builder.setBadgeIconType(icon);
        return this;
    }

    public NotificationHelper setCategory(String category) {
        builder.setCategory(category);
        return this;
    }

    public NotificationHelper setChannelId(String channelId) {
        builder.setChannelId(channelId);
        return this;
    }

    public NotificationHelper setExtras(Bundle extras) {
        builder.setExtras(extras);
        return this;
    }

    public NotificationHelper setOngoing(boolean ongoing) {
        builder.setOngoing(ongoing);
        return this;
    }

    public NotificationHelper setVibrate(long[] parttern) {
        builder.setVibrate(parttern);
        return this;
    }

    public NotificationHelper setGroup(String groupKey) {
        builder.setGroup(groupKey);
        return this;
    }

    public NotificationHelper setPriority(int priority) {
        builder.setPriority(priority);
        return this;
    }

    public NotificationHelper setSound(Uri uri) {
        builder.setSound(uri);
        return this;
    }

    public NotificationHelper setSound(Uri uri, int streamType) {
        builder.setSound(uri, streamType);
        return this;
    }

    public NotificationHelper setNumber(int number) {
        builder.setNumber(number);
        return this;
    }

    public NotificationHelper setProgress(int max, int progress, boolean indeterminate) {
        builder.setProgress(max, progress, indeterminate);
        return this;
    }

    public NotificationHelper setDefaults(int defaults) {
        builder.setDefaults(defaults);
        return this;
    }

    public NotificationHelper setShowWhen(boolean showWhen) {
        builder.setShowWhen(showWhen);
        return this;
    }

    public NotificationHelper setLocalOnly(boolean localOnly) {
        builder.setLocalOnly(localOnly);
        return this;
    }

    public NotificationHelper setTicker(CharSequence tickerText) {
        builder.setTicker(tickerText);
        return this;
    }

    public NotificationHelper setTicker(CharSequence tickerText, RemoteViews views) {
        builder.setTicker(tickerText, views);
        return this;
    }

    public NotificationHelper setVisibility(int visibility) {
        builder.setVisibility(visibility);
        return this;
    }

    public NotificationHelper setUsesChronometer(boolean usesChronometer) {
        builder.setUsesChronometer(usesChronometer);
        return this;
    }

    public NotificationHelper setDeleteIntent(PendingIntent intent) {
        builder.setDeleteIntent(intent);
        return this;
    }

    public NotificationHelper setSubText(String subText) {
        builder.setSubText(subText);
        return this;
    }

    public NotificationHelper setTimeoutAfter(long duration) {
        builder.setTimeoutAfter(duration);
        return this;
    }


    public Notification build() {
        Notification notification = builder.build();
        mContext = null;
        builder = null;
        helper = null;
        return notification;
    }
}
