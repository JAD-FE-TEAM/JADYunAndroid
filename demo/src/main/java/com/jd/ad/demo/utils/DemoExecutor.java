package com.jd.ad.demo.utils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class DemoExecutor {

    private final static ScheduledThreadPoolExecutor POOL;

    static {
        POOL = new ScheduledThreadPoolExecutor(1);
        POOL.setKeepAliveTime(30L, TimeUnit.SECONDS);
        POOL.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    private DemoExecutor() {
    }

    public static void execute(Runnable command) {
        POOL.execute(command);
    }

    public static ScheduledFuture<?> execute(Runnable command, long delay, TimeUnit unit) {
        return POOL.schedule(command, delay, unit);
    }

    public static ScheduledFuture<?> scheduleDelay(Runnable command, long delay, TimeUnit unit) {
        return POOL.schedule(command, delay, unit);
    }

    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return POOL.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    public static void remove(Runnable runnable) {
        POOL.remove(runnable);
    }
}
