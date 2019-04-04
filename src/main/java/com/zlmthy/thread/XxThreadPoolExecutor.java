package com.zlmthy.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池
 * @author zengliming
 * @date 2019-04-04
 */
public class XxThreadPoolExecutor {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int KEEP_ALIVE_TIME = 200;

    private static class getHook{

        private static ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(5),new ThreadPoolExecutor.AbortPolicy());
    }

    public static void run(Runnable runnable){
        getHook.executor.execute(runnable);
    }

    public static Future submit(Runnable runnable){
        return getHook.executor.submit(runnable);
    }

}
