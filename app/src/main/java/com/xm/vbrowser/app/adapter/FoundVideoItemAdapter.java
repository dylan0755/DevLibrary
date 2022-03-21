package com.xm.vbrowser.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dylan.common.ButterKnifeRecyclerViewHolder;
import com.dylan.library.adapter.BaseRecyclerAdapter;
import com.dylan.library.callback.SingleClickListener;
import com.dylan.library.io.FileDownLoader;
import com.dylan.library.io.FileUtils;
import com.dylan.library.m3u8.core.DefaultVideoFilter;
import com.dylan.library.m3u8.core.DownLoadListener;
import com.dylan.library.m3u8.core.M3u8DownLoader;
import com.dylan.library.m3u8.entry.M3U8;
import com.dylan.library.utils.ContextUtils;
import com.dylan.library.utils.EmptyUtils;
import com.dylan.library.utils.Logger;
import com.dylan.library.widget.DrawableTextView;
import com.dylan.library.widget.progressbar.ProgressBarDrawableDecorator;

import com.dylan.mylibrary.R;
import com.xm.vbrowser.app.entity.VideoFormat;
import com.xm.vbrowser.app.entity.VideoInfo;
import com.xm.vbrowser.app.util.FileUtil;
import com.xm.vbrowser.app.util.TimeUtil;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;


/**
 * Author: Dylan
 * Date: 2022/3/17
 * Desc:
 */

public class FoundVideoItemAdapter extends BaseRecyclerAdapter<VideoInfo, FoundVideoItemAdapter.ViewHolder> {


    private static int MSG_UPDATE = 10;
    private int colorBlack = Color.parseColor("#666666");
    private int colorGray = Color.parseColor("#999999");
    private int colorRed = Color.parseColor("#EB0636");
    private boolean isActivityBack;
    private int taskCount;
    private LinkedList<VideoInfo> waitQueue = new LinkedList();

    public void setActivityBack(boolean activityBack) {
        isActivityBack = activityBack;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE) {
                if (isActivityBack) return;
                notifyDataSetChanged();
            }
        }
    };

    public FoundVideoItemAdapter() {

    }



    public void appendNotifyDataChanged(VideoInfo videoInfo) {
        if (EmptyUtils.isEmpty(getDataList())){
            List<VideoInfo> list=new ArrayList<>();
            list.add(videoInfo);
            bind(list);
        }else{
            getDataList().add(0,videoInfo);
            notifyDataSetChanged();
        }

    }


    @Override
    public int getLayoutId() {
        return R.layout.item_video_detected;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final VideoInfo videoInfo, int i) {
        VideoFormat videoFormat = videoInfo.getVideoFormat();
        holder.tvTitle.setText(videoInfo.getFileName() + "." + videoFormat.getName());
        if ("m3u8".equals(videoFormat.getName())) {
            holder.tvDuration.setVisibility(View.VISIBLE);
            holder.tvDuration.setText(TimeUtil.formatTime((int) videoInfo.getDuration()));
            holder.tvSize.setVisibility(View.GONE);
            M3U8 m3U8 = videoInfo.getM3U8();
            if (EmptyUtils.isNotEmpty(m3U8.getTsList())) {
                String filePath = m3U8.getTsList().get(0).getTsUrl();
                Glide.with(holder.tvSize.getContext()).load(filePath).into(holder.ivPic);
            }
        } else {
            holder.tvSize.setVisibility(View.VISIBLE);
            holder.tvSize.setText(FileUtil.getFormatedFileSize(videoInfo.getSize()));
            holder.tvDuration.setVisibility(View.GONE);
            Glide.with(holder.tvSize.getContext()).load(videoInfo.getUrl()).into(holder.ivPic);

        }

        holder.ivDownLoad.setImageResource(R.drawable.icon_grabvideo_download);
        if (videoInfo.getDownLoadStatus() == VideoInfo.TASK_NONE) {
            holder.tvDownLoad.setText("点击下载");
            holder.tvDownLoad.setTextColor(colorBlack);
            holder.progressBar.setProgress(0);
        } else if (videoInfo.getDownLoadStatus() == VideoInfo.TASK_DOWNLOADING) {
            holder.tvDownLoad.setText("正在下载");
            holder.tvDownLoad.setTextColor(colorGray);
            holder.progressBar.setProgress(videoInfo.getDownLoadProgress());
        } else if (videoInfo.getDownLoadStatus() == VideoInfo.TASK_FINISHED) {
            holder.tvDownLoad.setText("已下载");
            holder.tvDownLoad.setTextColor(colorGray);
            holder.ivDownLoad.setImageResource(R.drawable.icon_grabvideo_downloadfinish);
            holder.progressBar.setProgress(100);
        } else if (videoInfo.getDownLoadStatus()== VideoInfo.TASK_ERROR){
            holder.tvDownLoad.setText("下载失败");
            holder.tvDownLoad.setTextColor(colorRed);
            holder.progressBar.setProgress(videoInfo.getDownLoadProgress());
        }else{
            holder.tvDownLoad.setText("排队中");
            holder.tvDownLoad.setTextColor(colorBlack);
            holder.progressBar.setProgress(0);
            if (taskCount<2){
                //取出队列
                if (!waitQueue.isEmpty()){
                    if (videoInfo==waitQueue.removeLast()){
                        toDownLoad(videoInfo,holder.llDownLoad);
                    }
                }

            }
        }

        final ViewHolder holder1=holder;
        holder.llDownLoad.setOnClickListener(null);
        holder.llDownLoad.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (videoInfo.getDownLoadStatus() == 0) {
                    if (taskCount>=2){
                        videoInfo.setDownLoadStatus(VideoInfo.TASK_WAITTING);
                        waitQueue.addFirst(videoInfo);
                        notifyDataSetChanged();
                        return;
                    }
                    toDownLoad(videoInfo,holder1.llDownLoad);
                }
            }
        });

    }


    private void toDownLoad(final VideoInfo videoInfo, final LinearLayout llDownLoad) {
        taskCount++;
        videoInfo.setDownLoadStatus(VideoInfo.TASK_DOWNLOADING);
        notifyDataSetChanged();
        if ("m3u8".equals(videoInfo.getVideoFormat().getName())) {
            String dirPath =Environment.getExternalStorageDirectory().toString()+"/grabvideo";
            FileUtils.createDirIfNotExists(dirPath);
            //文件名称
            String fileName = System.currentTimeMillis()+".mp4";
            //创建下载实例，设置并发线程数
            M3u8DownLoader videoDownload = new M3u8DownLoader(3,new BouncyCastleProvider());
            //设置下载后的文件存储路径
            videoDownload.setDirPath(dirPath);
            //设置视频过滤器，当下载链接包含多个视频文件时，由用户指定选择哪个视频文件，可以不设置
            videoDownload.setVideoListFilter(new DefaultVideoFilter());
            //开始下载
            Logger.e("ideoInfo.getUrl()="+videoInfo.getUrl());
            videoDownload.startDownload(videoInfo.getUrl(), fileName, new DownLoadListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onProgress(int finished, int sum, final float percent) {
                    llDownLoad.post(new Runnable() {
                        @Override
                        public void run() {
                            videoInfo.setDownLoadStatus(VideoInfo.TASK_DOWNLOADING);
                            int progress = (int) percent;
                            Logger.e("progress=" + progress);
                            videoInfo.setDownLoadProgress(progress);
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });
                }

                @Override
                public void onComplete(final String savePath) {
                    llDownLoad.post(new Runnable() {
                        @Override
                        public void run() {
                            taskCount--;
                            FileUtils.notifyScanFile(getContext(), savePath);
                            videoInfo.setDownLoadProgress(100);
                            videoInfo.setDownLoadStatus(VideoInfo.TASK_FINISHED);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                           
                        }
                    });
                }

                @Override
                public void onError(Exception exception) {
                    taskCount--;
                    llDownLoad.post(new Runnable() {
                        @Override
                        public void run() {
                            videoInfo.setDownLoadStatus(VideoInfo.TASK_ERROR);
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });
                }
            });
        } else {
            FileDownLoader fileDownLoader = new FileDownLoader();
            fileDownLoader.downLoad(videoInfo.getUrl(), "grabVideo_" + System.currentTimeMillis() + ".mp4");
            fileDownLoader.setDownLoadListener(new FileDownLoader.DownLoadListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onError(int i, String s) {
                    taskCount--;
                    llDownLoad.post(new Runnable() {
                        @Override
                        public void run() {
                            videoInfo.setDownLoadStatus(VideoInfo.TASK_ERROR);
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });
                }

                @Override
                public void onProgress(long l, long l1, final int i) {
                    llDownLoad.post(new Runnable() {
                        @Override
                        public void run() {
                            videoInfo.setDownLoadStatus(VideoInfo.TASK_DOWNLOADING);
                            videoInfo.setDownLoadProgress(i);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                        }
                    });

                }

                @Override
                public void onComplete(long l, final String s, boolean b) {
                    taskCount--;
                    llDownLoad.post(new Runnable() {
                        @Override
                        public void run() {
                            FileUtils.notifyScanFile(llDownLoad.getContext(), s);
                            videoInfo.setDownLoadProgress(100);
                            videoInfo.setDownLoadStatus(VideoInfo.TASK_FINISHED);
                            mHandler.sendEmptyMessage(MSG_UPDATE);
                           
                        }
                    });

                }
            });
        }
    }

     class ViewHolder extends ButterKnifeRecyclerViewHolder {
        @BindView(R.id.ivPic)
        ImageView ivPic;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.tvDuration)
        DrawableTextView tvDuration;
        @BindView(R.id.tvSize)
        DrawableTextView tvSize;
        @BindView(R.id.ivDownLoad)
        ImageView ivDownLoad;
        @BindView(R.id.tvDownLoad)
        TextView tvDownLoad;
        @BindView(R.id.llDownLoad)
        LinearLayout llDownLoad;
        @BindView(R.id.rootView)
        LinearLayout rootView;

        public ViewHolder(View view) {
            super(view);
            ProgressBarDrawableDecorator decorator = new ProgressBarDrawableDecorator(progressBar);
            decorator.setBGSolidColor(Color.parseColor("#EEEEEE"));
            decorator.setProgressColor(Color.parseColor("#FADB22"));
            decorator.setCornerRadius(2);
            decorator.decorate();
        }
    }


}