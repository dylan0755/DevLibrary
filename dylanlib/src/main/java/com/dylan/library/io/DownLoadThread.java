package com.dylan.library.io;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dylan on 2017/12/5.
 */

public  class DownLoadThread extends Thread {
    private Handler mHandler;
    String downLoadUrl;
    int startIndex;
    int endIndex;
    int threadId;
    private boolean pause;
    private boolean cancel;
    private String desFilePath;
    private String desDir;
    private String DOWNLOAD_PAUSE="pause";
    private String cacheFileName;


    public DownLoadThread(String downLoadUrl, String desFilePath, Handler handler, int threadId, int startIndex, int endIndex){
        this.downLoadUrl = downLoadUrl;
        this.desFilePath = desFilePath;
        File desFile=new File(desFilePath);
        desDir=desFile.getParentFile().getAbsolutePath();
        this.threadId=threadId;
        this.startIndex=startIndex;
        this.endIndex=endIndex;
        this.mHandler=handler;
        cacheFileName=desDir +"/."+desFile.getName()+"_"+threadId+".txt";
    }

    public void toPause(){
        pause=true;
    }

    public void toCancel(){
        cancel=true;
        delteCacheFile();
    }

    public void reStart(){
        synchronized (DOWNLOAD_PAUSE) {
            pause=false;
            DOWNLOAD_PAUSE.notifyAll();
        }
    }




    @Override
    public void run() {
        try {
            File tempfile=new File(cacheFileName);
            if(tempfile.exists()&&tempfile.length()>0){
                FileInputStream fis=new FileInputStream(tempfile);
                byte[] temp=new byte[1024];
                int len=fis.read(temp);
                String tempLength=new String(temp, 0, len);
                int downLoadLength= Integer.parseInt(tempLength);
                int alreadyDownLoad=downLoadLength-startIndex;
                Message message= Message.obtain();
                message.what= FileDownLoader.UDAPTE_PROGRESS;
                message.arg1=alreadyDownLoad;
                mHandler.sendMessage(message);
                startIndex=downLoadLength;
                fis.close();
            }

            URL url=new URL(downLoadUrl);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);
            connection.setConnectTimeout(5000);
            int responseCode=connection.getResponseCode();
            if(responseCode==206){
                InputStream is=connection.getInputStream();
                BufferedRandomAccessFile raf=new BufferedRandomAccessFile(desFilePath, "rwd");
                raf.seek(startIndex);
                int len=0;
                int total=0;//当前线程下载文件的总长度
                byte[] buff=new byte[1024];
                while((len=is.read(buff))!=-1){
                    synchronized (DOWNLOAD_PAUSE) {
                        if (pause) {
                            DOWNLOAD_PAUSE.wait();
                        }
                    }
                    raf.write(buff,0,len);
                    BufferedRandomAccessFile record=new BufferedRandomAccessFile(cacheFileName, "rwd");
                    if (cancel){
                        record.close();
                        is.close();
                        raf.close();
                        delteCacheFile();
                        //删除掉不完整的目标文件
                        File desFile=new File(desFilePath);
                        if (desFile.exists()){
                            desFile.delete();
                        }
                        return;
                    }
                    //记录文件进度
                    total=total+len;
                    record.write((total+startIndex+"").getBytes());
                    record.close();
                    Message message= Message.obtain();
                    message.what= FileDownLoader.UDAPTE_PROGRESS;
                    message.arg1=len;
                    mHandler.sendMessage(message);
                    Thread.sleep(10);
                }
                is.close();
                raf.close();
                //System.out.println("threadId "+threadId+"下载完毕！");
            }else{
                Log.e("下载失败","threadId "+threadId);
                System.out.println("");
            }


        } catch (Exception e) {
            if ( e instanceof java.net.ProtocolException){
                Message message= Message.obtain();
                message.what= FileDownLoader.ERROR_DONWLOAD;
                message.obj="下载异常";
                mHandler.sendMessage(message);
            }
            e.printStackTrace();
        }finally{
            delteCacheFile();
        }
    }

    private void delteCacheFile() {
        File file=new File(cacheFileName);
        if (file.exists()){
            file.delete();
        }
    }
}