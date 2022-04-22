package org.nekotori.handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程单例，用于共享线程池
 *
 * @author: JayDeng
 * @date: 25/08/2021 14:34
 * @description:
 * @version: {@link }
 */
public class ThreadSingleton {

    private static final ExecutorService service = Executors.newFixedThreadPool(20);

    public static void run(Runnable runnable) {
        service.execute(runnable);
    }
}
    