package com.dylan.library.utils.thread;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Author: Dylan
 * Date: 2023/3/18
 * Desc:
 */
public class CompletableFutureUtils {

    /**
     * 执行异步操作，并且阻塞所在线程，如果是在UI线程
     * 开启的异步任务，那么UI进程就会被阻塞，所以
     * 要考虑使用场景
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void runAsyncAndBlock(Runnable runnable){
        runAsyncAndBlock(1,  runnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void runAsyncAndBlock(int count, Runnable runnable){
        CompletableFuture<Void> completableFuture=CompletableFuture.runAsync(() -> {
            for (int i = 0; i < count; i++) {
                runnable.run();
            }
        });
        List<CompletableFuture<Void>> futureAll = new ArrayList<>();
        futureAll.add(completableFuture);
        //等待全部执行完成
        CompletableFuture.allOf(futureAll.toArray(new CompletableFuture[0])).join();
    }



}