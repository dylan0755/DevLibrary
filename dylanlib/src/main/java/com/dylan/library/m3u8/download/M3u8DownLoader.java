package com.dylan.library.m3u8.download;

import com.dylan.library.io.FileUtils;
import com.dylan.library.m3u8.listener.DownloadListener;
import com.dylan.library.m3u8.utils.Constant;


/**
 * @author liyaling
 * @email ts_liyaling@qq.com
 * @date 2019/12/14 16:02
 */

public class M3u8DownLoader {

    public static final String M3U8URL = "http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8";

    public static void downLoad(final String url, final String outDirPath,final String saveFileName,final DownloadListener downloadListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                M3u8DownloadFactory.M3u8Download m3u8Download = M3u8DownloadFactory.getInstance(M3U8URL);
                FileUtils.createDirIfNotExists(outDirPath);
                //设置生成目录
                m3u8Download.setDir(outDirPath);
                //设置视频名称
                m3u8Download.setFileName(saveFileName);
                //设置线程数
                m3u8Download.setThreadCount(8);
                //设置重试次数
                m3u8Download.setRetryCount(10);
                //设置连接超时时间（单位：毫秒）
                m3u8Download.setTimeoutMillisecond(10000L);
                 /*
                设置日志级别
               可选值：NONE INFO DEBUG ERROR
                */
                m3u8Download.setLogLevel(Constant.INFO);
                //设置监听器间隔（单位：毫秒）
                m3u8Download.setInterval(500L);
                //添加额外请求头
               /*  Map<String, Object> headersMap = new HashMap<>();
                  headersMap.put("Content-Type", "text/html;charset=utf-8");
                 m3u8Download.addRequestHeaderMap(headersMap);*/
                //如果需要的话设置http代理
                //m3u8Download.setProxy("172.50.60.3",8090);
                //添加监听器
                m3u8Download.addListener(downloadListener);
                //开始下载
                try {
                    m3u8Download.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (downloadListener!=null)downloadListener.onError(e);
                }
            }
        }).start();


    }
}
