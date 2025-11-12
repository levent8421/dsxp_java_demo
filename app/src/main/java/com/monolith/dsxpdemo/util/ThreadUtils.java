package com.monolith.dsxpdemo.util;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Create By YANYiZHI
 * Create Time: 2025/11/12 9:35
 * Description:
 * 线程工具类
 *
 * @author YANYiZHI
 */
public class ThreadUtils {
    public static void sleepMs(int amount) throws InterruptedException{
        Thread.sleep(amount);
    }

    public static void sleepMs(long amount) throws InterruptedException{
        Thread.sleep(amount);
    }

    private static class NamedThreadFactory implements ThreadFactory {
        private final String name;
        private int inc = 0;
        private final boolean incEnabled;

        private NamedThreadFactory(String name, boolean incEnabled) {
            this.name = name;
            this.incEnabled = incEnabled;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, incEnabled ? (name + inc++) : name);
        }
    }

    public static ThreadPoolExecutor createPool(int size, String name) {
        NamedThreadFactory namedThreadFactory = new NamedThreadFactory(name, size > 1);
        LinkedBlockingDeque<Runnable> blockQueue = new LinkedBlockingDeque<>();
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, blockQueue, namedThreadFactory);
    }

    public static ScheduledExecutorService createSingleScheduled(int size, String name) {
        NamedThreadFactory namedThreadFactory = new NamedThreadFactory(name, size > 1);
        return Executors.newSingleThreadScheduledExecutor(namedThreadFactory);
    }
}
