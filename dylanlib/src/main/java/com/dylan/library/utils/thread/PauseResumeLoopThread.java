package com.dylan.library.utils.thread;


/**
 * Author: Dylan
 * Date: 2022/4/23
 * Desc: 控制线程的暂停和恢复运行
 */

public abstract class PauseResumeLoopThread extends Thread{
    private boolean mRunning=true;
    private final Object lock = new Object();
    private boolean pause = false;

    public void resumeThread(){
        //线程恢复
        synchronized (lock) {
            if (!pause)return;
            pause = false;
            lock.notifyAll();
        }
    }

    public boolean isPause() {
        return pause;
    }

    public void pauseThread(){
        pause=true;
    }



    public void exitThread(){
        mRunning=false;
    }


    //这个方法只能在子线程中的run方法重调用，不然会阻塞主线程
    private void pause(){
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while(mRunning){
            if (pause){
                pause();
            }
            doRun();
        }
    }


    public abstract void doRun();
}
