package com.dylan.library.utils.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: Dylan
 * Date: 2019/9/1
 * Desc:
 */


/**
 * 固定线程池 new ThreadPoolExecutor(10,20,200L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),namedThreadFactory);
 * （1）当currentSize<corePoolSize时，没什么好说的，直接启动一个核心线程并执行任务。
 * （2）当currentSize>=corePoolSize、并且workQueue未满时，添加进来的任务会被安排到workQueue中等待执行。
 * （3）当workQueue已满，但是currentSize<maximumPoolSize时，会立即开启一个非核心线程来执行任务。
 * （4）当currentSize>=corePoolSize、workQueue已满、并且currentSize>maximumPoolSize时，调用handler默认抛出RejectExecutionExpection异常。
 */

public class ThreadPools {
    private static ThreadPools threadPools;
    private ExecutorService mFixThreadPool;
    private ThreadPools(){
        mFixThreadPool=creatFixThreadPool();
    }


	private ExecutorService creatFixThreadPool() {
		  // mFixThreadPool= Executors.newFixedThreadPool() newFixedThreadPool和newSingleThreadExecutor:
        //主要问题是堆积的请求处理队列可能会耗费非常大的内存，甚至OOM。
		 ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-%d").build();
        return new ThreadPoolExecutor(8,16,200L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),namedThreadFactory);
	}


    public static ThreadPools getInstance(){
         if (threadPools ==null){
             synchronized (ThreadPools.class){
                 threadPools =new ThreadPools();
             }
         }
         return threadPools;
    }


    public void fixedThreadPoolRun(Runnable runnable){
        if (isShutDown()){
            mFixThreadPool=creatFixThreadPool();
        }
        mFixThreadPool.execute(runnable);
    }

    //释放资源，如果某个线程中有 while循环操作，那么此方法是中断不了该线程的
    public void shutdownNow(){
        mFixThreadPool.shutdownNow();
    }

    public boolean isShutDown(){
        return mFixThreadPool.isShutdown();
    }

    public boolean isTerminated(){
        return mFixThreadPool.isTerminated();
    }

}
